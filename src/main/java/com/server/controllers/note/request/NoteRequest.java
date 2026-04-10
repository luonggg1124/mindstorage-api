package com.server.controllers.note.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String content;

    @NotNull(message = "Chủ đề không được để trống")
    @Positive(message = "Chủ đề không hợp lệ.")
    private Long topicId;

    private Long parentId;
}
