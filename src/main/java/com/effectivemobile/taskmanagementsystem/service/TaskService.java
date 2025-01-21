package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.dto.TaskDto;
import com.effectivemobile.taskmanagementsystem.util.TaskPriority;
import com.effectivemobile.taskmanagementsystem.util.TaskStatus;
import org.springframework.data.domain.Page;

public interface TaskService {
    TaskDto get(long id);

    Page<TaskDto> getAll(Long implementorId, Long authorId, TaskStatus status,
                         TaskPriority priority, int page, int size);

    TaskDto create(TaskDto dto);

    TaskDto update(TaskDto dto);

    TaskDto update(long id, TaskStatus status);

    void deleteById(long id);
}
