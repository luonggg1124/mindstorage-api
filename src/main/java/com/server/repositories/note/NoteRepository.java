package com.server.repositories.note;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.models.entities.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
    
}
