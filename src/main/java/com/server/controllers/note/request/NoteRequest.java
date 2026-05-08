package com.server.controllers.note.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String content;

    @NotNull(message = "Chủ đề không được để trống")
    private UUID topicId;

    private UUID parentId;
}
