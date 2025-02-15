package com.effectivemobile.taskmanagementsystem.dto.response;

import com.effectivemobile.taskmanagementsystem.util.TaskPriority;
import com.effectivemobile.taskmanagementsystem.util.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "Задание")
public class TaskDtoResponse {
    @Schema(description = "Идентификатор")
    private Long id;

    @Schema(description = "Заголовок", example = "Заголовок")
    private String title;

    @Schema(description = "Описание к заданию", example = "Описание")
    private String description;

    @Schema(description = "Комментарии к заданию")
    private List<CommentDtoResponse> comments;

    @Schema(description = "Приоритет")
    private TaskPriority priority;

    @Schema(description = "Статус")
    private TaskStatus status;

    @Schema(description = "Идентификатор автора")
    private Long authorId;

    @Schema(description = "Идентификатор исполнителя")
    private Long implementorId;
}
