package com.server.controllers.note;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.controllers.note.request.NoteRequest;
import com.server.controllers.note.response.CreateNoteResponse;
import com.server.models.entities.Note;
import com.server.services.note.NoteService;
import com.server.services.others.data.dto.PageResponse;
import com.server.services.note.dto.NoteByParentDto;
import com.server.services.note.dto.NoteByTopicDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note")
public class NoteController {
    private final NoteService noteService;

    @GetMapping("/by-topic/{topicId}")
    public ResponseEntity<PageResponse<NoteByTopicDto>> getNotesByTopic(
            @PathVariable UUID topicId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") @Positive(message = "Số trang không hợp lệ.") Integer page,
            @RequestParam(defaultValue = "10") @Positive(message = "Số lượng bản ghi trên trang không hợp lệ.") Integer size) {
        return ResponseEntity.ok(noteService.getNotesByTopic(topicId, q, page, size));
    }

    @GetMapping("/by-parent/{parentId}")
    public ResponseEntity<PageResponse<NoteByParentDto>> getNotesByParent(
            @PathVariable UUID parentId,
            @RequestParam(defaultValue = "0") @Positive(message = "Số trang không hợp lệ.") Integer page,
            @RequestParam(defaultValue = "10") @Positive(message = "Số lượng bản ghi trên trang không hợp lệ.") Integer size) {
        return ResponseEntity.ok(noteService.notesByParent(parentId, page, size));
    }

    @PostMapping
    public ResponseEntity<CreateNoteResponse> create(@Valid @RequestBody NoteRequest noteRequest) {
        Note savedNote = noteService.create(
                noteRequest.getTitle(),
                noteRequest.getContent(),
                noteRequest.getTopicId(),
                Optional.ofNullable(noteRequest.getParentId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateNoteResponse(savedNote.getId(),
                savedNote.getTitle(), savedNote.getContent(), savedNote.getCreatedAt(), savedNote.getUpdatedAt()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreateNoteResponse> update(@PathVariable UUID id,
            @Valid @RequestBody NoteRequest noteRequest) {
        Note updatedNote = noteService.update(
                id,
                noteRequest.getTitle(),
                noteRequest.getContent(),
                noteRequest.getTopicId(),
                Optional.ofNullable(noteRequest.getParentId()));

        return ResponseEntity.ok(new CreateNoteResponse(
                updatedNote.getId(),
                updatedNote.getTitle(),
                updatedNote.getContent(),
                updatedNote.getCreatedAt(),
                updatedNote.getUpdatedAt()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        noteService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
