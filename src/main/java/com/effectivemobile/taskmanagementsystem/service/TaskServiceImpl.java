package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.dao.SearchCriteriaWithPaginationTaskDao;
import com.effectivemobile.taskmanagementsystem.dao.TaskDao;
import com.effectivemobile.taskmanagementsystem.dto.request.task.TaskDtoCreateRequest;
import com.effectivemobile.taskmanagementsystem.dto.request.task.TaskDtoUpdateRequest;
import com.effectivemobile.taskmanagementsystem.dto.response.TaskDtoResponse;
import com.effectivemobile.taskmanagementsystem.exception.EntityNotFoundException;
import com.effectivemobile.taskmanagementsystem.mapper.DtoMapper;
import com.effectivemobile.taskmanagementsystem.model.Task;
import com.effectivemobile.taskmanagementsystem.model.User;
import com.effectivemobile.taskmanagementsystem.util.SearchCriteria;
import com.effectivemobile.taskmanagementsystem.util.TaskPriority;
import com.effectivemobile.taskmanagementsystem.util.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskDao taskDao;

    private final DtoMapper<TaskDtoResponse, Task> taskMapper;

    private final UserService userService;

    private final SearchCriteriaWithPaginationTaskDao criteriaDao;

    @Override
    @Transactional(readOnly = true)
    public TaskDtoResponse get(long id) {
        Task task = taskDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id = %d is not found".formatted(id)));
        TaskDtoResponse dto = taskMapper.convertToDto(task);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDtoResponse> getAll(Long implementorId, Long authorId,
                                        TaskStatus status, TaskPriority priority,
                                        Pageable pageable) {
        List<SearchCriteria> criteria = collectSearchCriteriaParams(implementorId, authorId, status, priority);

        Page<Task> tasks = criteriaDao.findAll(criteria, pageable);

        return new PageImpl<>(
                tasks.getContent().stream().map(taskMapper::convertToDto).collect(Collectors.toList()),
                pageable,
                tasks.getTotalElements()
        );
    }

    @Override
    @Transactional
    public TaskDtoResponse create(TaskDtoCreateRequest dto) {
        User author = userService.getById(dto.getAuthorId());
        User implementor = userService.getById(dto.getImplementorId());
        Task task = Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priority(dto.getPriority())
                .status(dto.getStatus())
                .author(author)
                .implementor(implementor)
                .build();

        return taskMapper.convertToDto(taskDao.save(task));
    }

    /**
     * Update Task except inner author and comments
     *
     * @param dto task to update
     * @return updated TaskDto
     */
    @Override
    @Transactional
    public TaskDtoResponse update(TaskDtoUpdateRequest dto) {
        Task task = taskDao.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Task with id = %d is not found"
                        .formatted(dto.getId())));
        User implementor = userService.getById(dto.getImplementorId());

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());
        task.setImplementor(implementor);

        return taskMapper.convertToDto(taskDao.save(task));
    }

    @Override
    @Transactional
    public TaskDtoResponse update(long id, TaskStatus status) {
        Task task = taskDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id = %d is not found".formatted(id)));
        task.setStatus(status);
        return taskMapper.convertToDto(taskDao.save(task));
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        taskDao.deleteById(id);
    }

    private List<SearchCriteria> collectSearchCriteriaParams(Long implementorId, Long authorId,
                                                             TaskStatus status, TaskPriority priority) {
        List<SearchCriteria> params = new ArrayList<>();
        if (authorId != null) {
            params.add(new SearchCriteria("author", ":", authorId));
        }
        if (implementorId != null) {
            params.add(new SearchCriteria("implementor", ":", implementorId));
        }
        if (status != null) {
            params.add(new SearchCriteria("status", ":", status.name()));
        }
        if (priority != null) {
            params.add(new SearchCriteria("priority", ":", priority.name()));
        }

        return params;
    }
}
