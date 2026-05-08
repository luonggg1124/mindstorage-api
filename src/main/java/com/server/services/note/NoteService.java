package com.server.services.note;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.server.exceptions.ConflictException;
import com.server.exceptions.NotFoundException;
import com.server.models.entities.Note;
import com.server.models.entities.Topic;
import com.server.models.entities.User;
import com.server.models.entities.Group;
import com.server.models.entities.Space;
import com.server.models.entities.SpaceMember;
import com.server.models.enums.RoleAction;
import com.server.repositories.group.GroupRepository;
import com.server.repositories.space.SpaceRepository;
import com.server.repositories.space.SpaceMemberRepository;
import com.server.repositories.note.NoteRepository;
import com.server.repositories.topic.TopicRepository;
import com.server.services.attachment.AttachmentService;
import com.server.services.auth.AuthService;
import com.server.services.note.dto.NoteByParentDto;
import com.server.services.note.dto.NoteByTopicDto;
import com.server.services.others.data.DataService;
import com.server.services.others.data.dto.PageResponse;
import com.server.services.user.dto.SimpleUserDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class NoteService {
    private final NoteRepository noteRepository;
    private final TopicRepository topicRepository;
    private final AuthService authService;
    private final DataService dataService;
    private final AttachmentService fileService;
    private final GroupRepository groupRepository;
    private final SpaceRepository spaceRepository;
    private final SpaceMemberRepository spaceMemberRepository;

    public PageResponse<NoteByTopicDto> getNotesByTopic(UUID topicId, String keyWord, Integer page, Integer size) {
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
                fileService.buildPublicUrl(u.getAvatarFileKey()),
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

    public List<Note> getAllListNote() {
        return noteRepository.findAllByDeletedAtIsNull();
    }

  public PageResponse<NoteByParentDto> notesByParent(UUID parentId, Integer page, Integer size) {
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
                fileService.buildPublicUrl(u.getAvatarFileKey()),
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
    @Transactional
    public Note create(String title, String content, UUID topicId, Optional<UUID> parentId) {
        User currentUser = authService.authUser();
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chủ đề với"));
        Space space = spaceFromTopic(topic);
        if (!canManageNotesInSpace(currentUser, space)) {
            throw new ConflictException("Không có quyền tạo ghi chú", "topicId");
        }

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
        Note saved = noteRepository.save(note);
        touchContainerActivity(topic);
        return saved;
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

    @Transactional
    public Note update(UUID id, String title, String content, UUID topicId, Optional<UUID> parentId) {
        User currentUser = authService.authUser();

        Note note = noteRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Note not found with id: " + id));
        Space existingSpace = spaceFromNote(note);
        if (!canManageNotesInSpace(currentUser, existingSpace)) {
            throw new ConflictException("Không có quyền cập nhật ghi chú", "noteId");
        }

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chủ đề với"));
        Space targetSpace = spaceFromTopic(topic);
        if (!existingSpace.getId().equals(targetSpace.getId()) && !canManageNotesInSpace(currentUser, targetSpace)) {
            throw new ConflictException("Không có quyền chuyển ghi chú sang chủ đề này", "topicId");
        }

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

        Note saved = noteRepository.save(note);
        touchContainerActivity(topic);
        return saved;
    }

    @Transactional
    public void delete(UUID id) {
        User currentUser = authService.authUser();
        Note note = noteRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy."));
        Space space = spaceFromNote(note);
        if (!canManageNotesInSpace(currentUser, space)) {
            throw new ConflictException("Không có quyền xoá ghi chú", "noteId");
        }
        note.setDeletedBy(currentUser);
        note.setDeletedAt(LocalDateTime.now());
        noteRepository.save(note);

        if (note.getTopic() != null) {
            touchContainerActivity(note.getTopic());
        }
    }

    public Note getNoteById(UUID id) {
        return noteRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Note not found with id: " + id));
    }

    private void touchContainerActivity(Topic topic) {
        if (topic == null) {
            return;
        }
        Group group = topic.getGroup();
        if (group == null) {
            return;
        }
        Space space = group.getSpace();
        LocalDateTime now = LocalDateTime.now();
        groupRepository.touchLastActivityAt(group.getId(), now);
        if (space != null) {
            spaceRepository.touchLastActivityAt(space.getId(), now);
        }
    }

    private Space spaceFromTopic(Topic topic) {
        Group group = topic == null ? null : topic.getGroup();
        if (group == null) {
            throw new ConflictException("Chủ đề không thuộc nhóm nào", "groupId");
        }
        Space space = group.getSpace();
        if (space == null) {
            throw new ConflictException("Nhóm không thuộc không gian nào", "spaceId");
        }
        return space;
    }

    private Space spaceFromNote(Note note) {
        Topic topic = note == null ? null : note.getTopic();
        if (topic == null) {
            throw new ConflictException("Ghi chú không thuộc chủ đề nào", "topicId");
        }
        return spaceFromTopic(topic);
    }

    private boolean canManageNotesInSpace(User actor, Space space) {
        if (space.getCreator() != null && space.getCreator().getId().equals(actor.getId())) {
            return true;
        }
        RoleAction role = spaceMemberRepository.findBySpace_IdAndUser_Id(space.getId(), actor.getId())
                .map(SpaceMember::getRole)
                .orElseThrow(() -> new ConflictException("Bạn không thuộc không gian này", "spaceId"));
        return role == RoleAction.OWNER || role == RoleAction.EDITOR;
    }
}
