package com.server.services.note;

import java.util.List;
import java.util.Optional;



import com.server.models.entities.Note;
import com.server.services.note.dto.NoteByTopicDto;
import com.server.services.others.data.dto.PageResponse;


public interface NoteService {

    PageResponse<NoteByTopicDto> getNotesByTopic(Long topicId, String keyWord, Integer page, Integer size);
    List<Note> getAllListNote();

    List<Note> getListByKeyWord(String keyWord);

    Note create(String title, String content, Long topicId, Optional<Long> parentId);

    Note update(Long id, String title, String content, Long topicId, Optional<Long> parentId);

    void delete(Long id);

    Note getNoteById(Long id);
}
