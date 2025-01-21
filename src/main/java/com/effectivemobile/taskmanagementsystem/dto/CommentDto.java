package com.effectivemobile.taskmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Комментарий")
public class CommentDto {
    @Nullable
    private Long id;

    @Schema(description = "Комментарий к заданию", example = "Комментарий к заданию")
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(min = 5, max = 255, message = "Текст комментария должен содержать от 5 до 255 символов")
    private String text;

    @NotNull
    private Long authorId;

    @NotNull
    private Long taskId;
}
