package com.server.controllers.note;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.controllers.note.request.NoteRequest;
import com.server.models.entities.Note;
import com.server.services.auth.AuthService;
import com.server.services.note.NoteService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note")
public class NoteController {
    private final NoteService noteService;
    private final AuthService authService;

    @GetMapping("list")
    public ResponseEntity<?> listNote(){
        return ResponseEntity.ok(noteService.getAllListNote());
    }

    @PostMapping("add")
    public ResponseEntity<?> addNote(@Valid @RequestBody NoteRequest noteRequest) {
        Note note = new Note();
        note.setTitle(noteRequest.getTitle());
        note.setContent(noteRequest.getContent());
        note.setCreator(authService.authUser());
        note.setEmbedding(generateDefaultEmbedding());
        
        if (noteRequest.getParentId() != null) {
            Note parentNote = noteService.getNoteById(noteRequest.getParentId());
            note.setParent(parentNote);
        }
        
        Note savedNote = noteService.add(note);
        return ResponseEntity.ok(savedNote);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id, @Valid @RequestBody NoteRequest noteRequest) {
        Note note = noteService.getNoteById(id);
        note.setTitle(noteRequest.getTitle());
        note.setContent(noteRequest.getContent());
        
        if (noteRequest.getParentId() != null) {
            Note parentNote = noteService.getNoteById(noteRequest.getParentId());
            note.setParent(parentNote);
        } else {
            note.setParent(null);
        }
        
        Note updatedNote = noteService.update(note, id);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id) {
        noteService.delete(id);
        return ResponseEntity.ok("Note deleted successfully");
    }

    private String generateDefaultEmbedding() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 1536; i++) {
            if (i > 0) sb.append(",");
            sb.append("0.0");
        }
        sb.append("]");
        return sb.toString();
    }
}
