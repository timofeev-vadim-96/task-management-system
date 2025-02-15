package com.effectivemobile.taskmanagementsystem.mapper;

import com.effectivemobile.taskmanagementsystem.dto.response.CommentDtoResponse;
import com.effectivemobile.taskmanagementsystem.dto.response.TaskDtoResponse;
import com.effectivemobile.taskmanagementsystem.model.Comment;
import com.effectivemobile.taskmanagementsystem.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskMapper implements DtoMapper<TaskDtoResponse, Task> {
    private final DtoMapper<CommentDtoResponse, Comment> commentMapper;

    public TaskDtoResponse convertToDto(Task task) {
        TaskDtoResponse dto = TaskDtoResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .authorId(task.getAuthor().getId())
                .implementorId(task.getImplementor().getId())
                .priority(task.getPriority())
                .status(task.getStatus())
                .build();
        if (task.getComments() != null) {
            dto.setComments(commentMapper.convertToDtos(task.getComments()));
        } else {
            dto.setComments(new ArrayList<>());
        }

        return dto;
    }

    public List<TaskDtoResponse> convertToDtos(Collection<Task> tasks) {
        return tasks.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}
