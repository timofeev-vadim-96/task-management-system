package com.effectivemobile.taskmanagementsystem.controller;

import com.effectivemobile.taskmanagementsystem.dto.request.task.TaskDtoCreateRequest;
import com.effectivemobile.taskmanagementsystem.dto.request.task.TaskDtoUpdateRequest;
import com.effectivemobile.taskmanagementsystem.dto.response.TaskDtoResponse;
import com.effectivemobile.taskmanagementsystem.service.TaskService;
import com.effectivemobile.taskmanagementsystem.util.TaskPriority;
import com.effectivemobile.taskmanagementsystem.util.TaskStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Контроллер заданий", description = "Контроллер для работы с заданиями")
public class TaskController {
    private final TaskService taskService;

    @GetMapping("api/v1/task/{id}")
    @Operation(summary = "Получение задания по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "задание найдено"),
            @ApiResponse(responseCode = "403", description = "попытка получить доступ к заданию другого исполнителя"),
            @ApiResponse(responseCode = "404", description = "id задания не корректно")
    })
    public ResponseEntity<TaskDtoResponse> get(@PathVariable("id") long id) {
        TaskDtoResponse task = taskService.get(id);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @GetMapping("api/v1/task")
    @Operation(summary = "Получение заданий с пагинацией и фильтрацией по заданным параметрам")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "задания найдены"),
            @ApiResponse(responseCode = "403", description = "попытка доступа ко всем заданиям другого исполнителя")
    })
    public ResponseEntity<Page<TaskDtoResponse>> getAll(
            @RequestParam(value = "implementorId", required = false) Long implementorId,
            @RequestParam(value = "authorId", required = false) Long authorId,
            @RequestParam(value = "status", required = false) TaskStatus status,
            @RequestParam(value = "priority", required = false) TaskPriority priority,
            @PageableDefault(
                    size = 20,
                    page = 0,
                    sort = {"id"},
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<TaskDtoResponse> tasks = taskService.getAll(implementorId, authorId, status, priority, pageable);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @PostMapping("api/v1/task")
    @Operation(summary = "Создание нового задания")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "задание создано"),
            @ApiResponse(responseCode = "404", description = "id автора задания или его исполнителя не корректны")
    })
    public ResponseEntity<TaskDtoResponse> create(@RequestBody TaskDtoCreateRequest dto) {
        TaskDtoResponse task = taskService.create(dto);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @PutMapping("api/v1/task")
    @Operation(summary = "Обновление задания")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "задание изменено"),
            @ApiResponse(responseCode = "400", description = "id обновляемого задания не может быть null"),
            @ApiResponse(responseCode = "404", description = "id задания или id автора задания не корректны")
    })
    public ResponseEntity<TaskDtoResponse> update(@RequestBody TaskDtoUpdateRequest dto) {
        TaskDtoResponse task = taskService.update(dto);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @PutMapping("api/v1/task/status")
    @Operation(summary = "Обновление статуса задания")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "задание изменено"),
            @ApiResponse(responseCode = "403", description = "попытка изменения статуса задания другого пользователя"),
            @ApiResponse(responseCode = "404", description = "id задания не корректно")
    })
    public ResponseEntity<TaskDtoResponse> update(@RequestParam long id, @RequestParam TaskStatus status) {
        TaskDtoResponse task = taskService.update(id, status);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @DeleteMapping("api/v1/task/{id}")
    @Operation(summary = "Удаление задания")
    @ApiResponse(responseCode = "200", description = "задание удалено")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        taskService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
