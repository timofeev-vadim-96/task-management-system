package com.effectivemobile.taskmanagementsystem.converter;

import com.effectivemobile.taskmanagementsystem.dto.TaskDto;
import com.effectivemobile.taskmanagementsystem.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskConverter {
    private final CommentConverter commentConverter;

    public TaskDto convertToDto(Task task) {
        TaskDto dto = TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .authorId(task.getAuthor().getId())
                .implementorId(task.getImplementor().getId())
                .priority(task.getPriority())
                .status(task.getStatus())
                .build();
        if (task.getComments() != null) {
            dto.setComments(commentConverter.convertToDtos(task.getComments()));
        } else {
            dto.setComments(new ArrayList<>());
        }

        return dto;
    }

    public List<TaskDto> convertToDtos(Collection<Task> tasks) {
        return tasks.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}
