package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.converter.TaskConverter;
import com.effectivemobile.taskmanagementsystem.dao.SearchCriteriaWithPaginationTaskDao;
import com.effectivemobile.taskmanagementsystem.dao.TaskDao;
import com.effectivemobile.taskmanagementsystem.dto.TaskDto;
import com.effectivemobile.taskmanagementsystem.exception.EntityNotFoundException;
import com.effectivemobile.taskmanagementsystem.exception.UnsatisfactoryValueException;
import com.effectivemobile.taskmanagementsystem.model.AppUser;
import com.effectivemobile.taskmanagementsystem.model.Task;
import com.effectivemobile.taskmanagementsystem.util.SearchCriteria;
import com.effectivemobile.taskmanagementsystem.util.TaskPriority;
import com.effectivemobile.taskmanagementsystem.util.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskDao taskDao;

    private final TaskConverter taskConverter;

    private final UserService userService;

    private final SearchCriteriaWithPaginationTaskDao criteriaDao;

    @Override
    @Transactional(readOnly = true)
    public TaskDto get(long id) {
        Task task = taskDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id = %d is not found".formatted(id)));
        return taskConverter.convertToDto(task);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("tasks")
    public Page<TaskDto> getAll(Long implementorId, Long authorId,
                                TaskStatus status, TaskPriority priority,
                                int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        List<SearchCriteria> criteria = collectSearchCriteriaParams(implementorId, authorId, status, priority);

        Page<Task> tasks = criteriaDao.findAll(criteria, pageable);

        return new PageImpl<>(
                tasks.getContent().stream().map(taskConverter::convertToDto).collect(Collectors.toList()),
                pageable,
                tasks.getTotalElements()
        );
    }

    @Override
    @Transactional
    public TaskDto create(TaskDto dto) {
        AppUser author = userService.getById(dto.getAuthorId());
        AppUser implementor = userService.getById(dto.getImplementorId());
        Task task = Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priority(dto.getPriority())
                .status(dto.getStatus())
                .author(author)
                .implementor(implementor)
                .build();

        return taskConverter.convertToDto(taskDao.save(task));
    }

    /**
     * Update Task except inner author and comments
     *
     * @param dto task to update
     * @return updated TaskDto
     */
    @Override
    @Transactional
    public TaskDto update(TaskDto dto) {
        if (dto.getId() == null) {
            throw new UnsatisfactoryValueException("Id must not be null");
        }
        Task task = taskDao.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Task with id = %d is not found"
                        .formatted(dto.getId())));
        AppUser implementor = userService.getById(dto.getImplementorId());

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());
        task.setImplementor(implementor);

        return taskConverter.convertToDto(taskDao.save(task));
    }

    @Override
    @Transactional
    public TaskDto update(long id, TaskStatus status) {
        Task task = taskDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id = %d is not found".formatted(id)));
        task.setStatus(status);
        return taskConverter.convertToDto(taskDao.save(task));
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
