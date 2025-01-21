package com.effectivemobile.taskmanagementsystem.dto;

import com.effectivemobile.taskmanagementsystem.util.TaskPriority;
import com.effectivemobile.taskmanagementsystem.util.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "Задание")
public class TaskDto {
    @Nullable
    private Long id;

    @Schema(description = "Заголовок задания", example = "Заголовок")
    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 5, max = 255, message = "Заголовок должен содержать от 5 до 255 символов")
    private String title;

    @Schema(description = "Описание к заданию", example = "Описание")
    @NotBlank(message = "Текст описания к заданию не может быть пустым")
    @Size(min = 5, max = 255, message = "Текст описания к заданию должен содержать от 5 до 255 символов")
    private String description;

    @Nullable
    private List<CommentDto> comments;

    @NotNull
    private TaskPriority priority;

    @NotNull
    private TaskStatus status;

    @NotNull
    private Long authorId;

    @NotNull
    private Long implementorId;
}
