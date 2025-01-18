package com.effectivemobile.taskmanagementsystem.controller;

import com.effectivemobile.taskmanagementsystem.dto.CommentDto;
import com.effectivemobile.taskmanagementsystem.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "Контроллер комментариев", description = "Контроллер для работы с комментариями")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("api/v1/comment")
    @Operation(summary = "Создание нового комментария")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "комментарий создан"),
            @ApiResponse(responseCode = "403",
                    description = "попытка создать комментарий к заданию другого исполнителя"),
            @ApiResponse(responseCode = "404",
                    description = "id задания или автора задания не корректны")
    })
    public ResponseEntity<CommentDto> create(@RequestBody CommentDto dto) {
        CommentDto created = commentService.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("api/v1/comment/{id}")
    @Operation(summary = "Получение комментария по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "комментарий найден"),
            @ApiResponse(responseCode = "404", description = "id комментария не корректно")
    })
    public ResponseEntity<CommentDto> get(@PathVariable("id") long id) {
        CommentDto comment = commentService.get(id);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @GetMapping("api/v1/comment/task/{taskId}")
    @Operation(summary = "Получение всех комментариев по id задания")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "комментарии найдены")
    })
    public ResponseEntity<List<CommentDto>> getAllByTaskId(@PathVariable("taskId") long taskId) {
        List<CommentDto> comments = commentService.getAllByTask(taskId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PutMapping("api/v1/comment")
    @Operation(summary = "Обновление текста комментария")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "комментарий изменен"),
            @ApiResponse(responseCode = "403",
                    description = "попытка внести изменения в комментарий другого исполнителя"),
            @ApiResponse(responseCode = "404",
                    description = "id комментария не корректно")
    })
    public ResponseEntity<CommentDto> update(@RequestParam long id, @NotBlank @RequestParam String comment) {
        CommentDto updated = commentService.update(id, comment);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("api/v1/comment/{id}")
    @Operation(summary = "Удаление комментария")
    @ApiResponse(responseCode = "200", description = "комментарий удален")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        commentService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
