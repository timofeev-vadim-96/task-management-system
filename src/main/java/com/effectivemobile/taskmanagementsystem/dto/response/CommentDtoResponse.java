package com.effectivemobile.taskmanagementsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Комментарий")
public class CommentDtoResponse {
    @Schema(description = "Идентификатор")
    private Long id;

    @Schema(description = "Комментарий к заданию", example = "Комментарий к заданию")
    private String text;

    @Schema(description = "Идентификатор автора")
    private Long authorId;

    @Schema(description = "Идентификатор задания")
    private Long taskId;
}
