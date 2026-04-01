package com.server.services.note;

import org.springframework.stereotype.Service;

import com.server.repositories.note.NoteRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NoteServiceImplement implements NoteService {
    private final NoteRepository noteRepository;
}
