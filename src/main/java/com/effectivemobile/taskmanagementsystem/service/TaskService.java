package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.dto.TaskDto;
import com.effectivemobile.taskmanagementsystem.util.TaskPriority;
import com.effectivemobile.taskmanagementsystem.util.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskDto get(long id);

    Page<TaskDto> getAll(Long implementorId, Long authorId, TaskStatus status,
                         TaskPriority priority, Pageable pageable);

    TaskDto create(TaskDto dto);

    TaskDto update(TaskDto dto);

    TaskDto update(long id, TaskStatus status);

    void deleteById(long id);
}
