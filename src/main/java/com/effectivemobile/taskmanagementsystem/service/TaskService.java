package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.dto.request.task.TaskDtoCreateRequest;
import com.effectivemobile.taskmanagementsystem.dto.request.task.TaskDtoUpdateRequest;
import com.effectivemobile.taskmanagementsystem.dto.response.TaskDtoResponse;
import com.effectivemobile.taskmanagementsystem.util.TaskPriority;
import com.effectivemobile.taskmanagementsystem.util.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskDtoResponse get(long id);

    Page<TaskDtoResponse> getAll(Long implementorId, Long authorId, TaskStatus status,
                                 TaskPriority priority, Pageable pageable);

    TaskDtoResponse create(TaskDtoCreateRequest dto);

    TaskDtoResponse update(TaskDtoUpdateRequest dto);

    TaskDtoResponse update(long id, TaskStatus status);

    void deleteById(long id);
}
