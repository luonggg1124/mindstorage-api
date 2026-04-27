package com.server.services.note;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.server.models.entities.Note;
import com.server.services.note.dto.NoteByParentDto;
import com.server.services.note.dto.NoteByTopicDto;
import com.server.services.others.data.dto.PageResponse;


public interface NoteService {

    PageResponse<NoteByTopicDto> getNotesByTopic(UUID topicId, String keyWord, Integer page, Integer size);
    List<Note> getAllListNote();

    PageResponse<NoteByParentDto> notesByParent(UUID parentId, Integer page, Integer size);

    Note create(String title, String content, UUID topicId, Optional<UUID> parentId);

    Note update(UUID id, String title, String content, UUID topicId, Optional<UUID> parentId);

    void delete(UUID id);

    Note getNoteById(UUID id);
}
