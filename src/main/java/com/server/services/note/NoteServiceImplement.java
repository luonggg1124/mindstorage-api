package com.server.services.note;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.server.exceptions.NotFoundException;
import com.server.models.entities.Note;
import com.server.models.entities.Topic;
import com.server.models.entities.User;
import com.server.repositories.note.NoteRepository;
import com.server.repositories.topic.TopicRepository;
import com.server.services.auth.AuthService;
import com.server.services.note.dto.NoteByParentDto;
import com.server.services.note.dto.NoteByTopicDto;
import com.server.services.others.data.DataService;
import com.server.services.others.data.dto.PageResponse;
import com.server.services.user.dto.SimpleUserDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class NoteServiceImplement implements NoteService {
    private final NoteRepository noteRepository;
    private final TopicRepository topicRepository;
    private final AuthService authService;
    private final DataService dataService;

    @Override
    public PageResponse<NoteByTopicDto> getNotesByTopic(Long topicId, String keyWord, Integer page, Integer size) {
        int pageIndex = page == null ? 0 : Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageIndex, size);

        String embeddingVector = null;
        if (keyWord != null && !keyWord.isBlank()) {
            float[] keywordEmbedding = dataService.generateEmbedding(keyWord.trim());
            if (keywordEmbedding != null && keywordEmbedding.length > 0) {
                embeddingVector = dataService.toVectorString(keywordEmbedding);
            }
        }

        Page<Note> pageData = noteRepository.notesByTopic(topicId, embeddingVector, pageable);
        Page<NoteByTopicDto> notesByTopic = pageData.map(this::toNoteByTopicDto);
        return new PageResponse<NoteByTopicDto>(notesByTopic.getContent(), notesByTopic.getTotalElements(),
                notesByTopic.getNumber() + 1, notesByTopic.getSize());
    }

    private NoteByTopicDto toNoteByTopicDto(Note note) {
        User u = note.getCreator();
        SimpleUserDto creator = u == null ? null : new SimpleUserDto(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getAvatarUrl(),
                u.getFullName(),
                u.getGender());

        return new NoteByTopicDto(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                creator,
                note.getCreatedAt(),
                note.getUpdatedAt());
    }

    @Override
    public List<Note> getAllListNote() {
        return noteRepository.findAllByDeletedAtIsNull();
    }

  @Override
  public PageResponse<NoteByParentDto> notesByParent(Long parentId, Integer page, Integer size) {
    int pageIndex = page == null ? 0 : Math.max(page - 1, 0);
    Pageable pageable = PageRequest.of(pageIndex, size);
    Page<Note> pageData = noteRepository.findAllByParent_IdAndDeletedAtIsNull(parentId, pageable);
    Page<NoteByParentDto> notes = pageData.map(this::toNoteByParentDto);
    return new PageResponse<NoteByParentDto>(notes.getContent(), notes.getTotalElements(), notes.getNumber() + 1, notes.getSize());
  }

    private NoteByParentDto toNoteByParentDto(Note note) {
        User u = note.getCreator();
        SimpleUserDto creator = u == null ? null : new SimpleUserDto(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getAvatarUrl(),
                u.getFullName(),
                u.getGender());
        return new NoteByParentDto(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                creator,
                note.getCreatedAt(),
                note.getUpdatedAt());
    }
    @Override
    public Note create(String title, String content, Long topicId, Optional<Long> parentId) {
        User currentUser = authService.authUser();
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chủ đề với"));

        Note parent = parentId
                .flatMap(noteRepository::findByIdAndDeletedAtIsNull)
                .orElse(null);

        String topicName = topic.getName();
        String parentName = parent == null ? null : parent.getTitle();

        String textForEmbedding = buildEmbeddingText(title, content, topicName, parentName);
        log.debug("Text for embedding: {}", textForEmbedding);
        float[] embedding = dataService.generateEmbedding(textForEmbedding);
       

        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setEmbedding(embedding);
        note.setTopic(topic);
        note.setParent(parent);
        note.setCreator(currentUser);
        return noteRepository.save(note);
    }

    private String buildEmbeddingText(String title, String content, String topicName, String parentName) {
        log.debug("Title: {}", title);
        log.debug("Content: {}", content);
        log.debug("Topic name: {}", topicName);
        log.debug("Parent name: {}", parentName);
        StringBuilder sb = new StringBuilder();
        if (title != null && !title.isBlank()) {
            sb.append("title: ").append(title.trim()).append('\n');
        }
        if (content != null && !content.isBlank()) {
            String plainContent = dataService.plainTextFromHtml(content);
            if (!plainContent.isBlank()) {
                sb.append("content: ").append(plainContent).append('\n');
            }
        }
        if (topicName != null && !topicName.isBlank()) {
            sb.append("topic: ").append(topicName.trim()).append('\n');
        }
        if (parentName != null && !parentName.isBlank()) {
            sb.append("parent: ").append(parentName.trim()).append('\n');
        }
        return sb.toString().trim();
    }

    @Override
    public Note update(Long id, String title, String content, Long topicId, Optional<Long> parentId) {
        User currentUser = authService.authUser();

        Note note = noteRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Note not found with id: " + id));

        if (!note.getCreator().getId().equals(currentUser.getId())) {
            throw new NotFoundException("Không tìm thấy note");
        }

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chủ đề với"));

        Note parent = parentId
                .flatMap(noteRepository::findByIdAndDeletedAtIsNull)
                .orElse(null);

        if (parent != null && parent.getId().equals(note.getId())) {
            throw new NotFoundException("Parent không hợp lệ");
        }

        String topicName = topic.getName();
        String parentName = parent == null ? null : parent.getTitle();

        String textForEmbedding = buildEmbeddingText(title, content, topicName, parentName);
        float[] embedding = dataService.generateEmbedding(textForEmbedding);
        

        note.setTitle(title);
        note.setContent(content);
        note.setTopic(topic);
        note.setParent(parent);
        note.setEmbedding(embedding);

        return noteRepository.save(note);
    }

    @Override
    public void delete(Long id) {
        User currentUser = authService.authUser();
        Note note = noteRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy."));
        note.setDeletedBy(currentUser);
        note.setDeletedAt(LocalDateTime.now());
        noteRepository.save(note);
    }

    @Override
    public Note getNoteById(Long id) {
        return noteRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Note not found with id: " + id));
    }
}
