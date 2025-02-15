package com.effectivemobile.taskmanagementsystem.dto.request.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Комментарий")
public class CommentDtoCreateRequest {
    @Schema(description = "Комментарий к заданию", example = "Комментарий к заданию")
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(min = 5, max = 255, message = "Текст комментария должен содержать от 5 до 255 символов")
    private String text;

    @NotNull
    @Schema(description = "Идентификатор автора")
    private Long authorId;

    @NotNull
    @Schema(description = "Идентификатор задания")
    private Long taskId;
}
