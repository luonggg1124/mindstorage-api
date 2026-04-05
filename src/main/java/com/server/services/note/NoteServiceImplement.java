package com.server.services.note;

import java.util.List;

import org.springframework.stereotype.Service;

import com.server.exceptions.NotFoundException;
import com.server.models.entities.Note;
import com.server.repositories.note.NoteRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NoteServiceImplement implements NoteService {
    private final NoteRepository noteRepository;

    @Override
    public List<Note> getAllListNote() {
        return noteRepository.findAll();
    }

    @Override
    public List<Note> getListByKeyWord(String keyWord) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Note add(Note note) {
        return noteRepository.save(note);
    }

    @Override
    public Note update(Note note,Long id) {
        // Note note = noteRepository.findById(id).orElse(null);
        return noteRepository.save(note);
    }

    @Override
    public void delete(Long id) {
        noteRepository.deleteById(id);
    }

    @Override
    public Note getNoteById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Note not found with id: " + id));
    }
}
