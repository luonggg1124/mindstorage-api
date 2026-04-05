package com.server.services.note;

import java.util.List;

import com.server.models.entities.Note;

public interface  NoteService {
    
    List<Note> getAllListNote();

    List<Note> getListByKeyWord(String keyWord);

    Note add(Note note);

    Note update(Note note,Long id);

    void delete(Long id);

    Note getNoteById(Long id);
}
