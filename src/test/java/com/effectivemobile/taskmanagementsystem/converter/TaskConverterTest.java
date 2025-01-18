package com.effectivemobile.taskmanagementsystem.converter;

import com.effectivemobile.taskmanagementsystem.dto.CommentDto;
import com.effectivemobile.taskmanagementsystem.dto.TaskDto;
import com.effectivemobile.taskmanagementsystem.model.AppUser;
import com.effectivemobile.taskmanagementsystem.model.Comment;
import com.effectivemobile.taskmanagementsystem.model.Task;
import com.effectivemobile.taskmanagementsystem.util.TaskPriority;
import com.effectivemobile.taskmanagementsystem.util.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {TaskConverter.class, CommentConverter.class})
class TaskConverterTest {
    @Autowired
    private TaskConverter taskConverter;

    @MockBean
    private CommentConverter commentConverter;

    private Task task;

    private Comment comment;

    @BeforeEach
    void init() {
        task = Task.builder()
                .id(1L)
                .title("some title")
                .description("some description")
                .priority(TaskPriority.НИЗКИЙ)
                .status(TaskStatus.В_ОЖИДАНИИ)
                .author(new AppUser())
                .implementor(new AppUser())
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("some text")
                .author(new AppUser())
                .task(task)
                .build();

        when(commentConverter.convertToDto(any(Comment.class))).thenReturn(CommentDto.builder().id(1L).build());
    }

    @Test
    void convertToDtoWithComments() {
        when(commentConverter.convertToDtos(any())).thenReturn(List.of(CommentDto.builder().id(1L).build()));
        task.setComments(List.of(comment));

        TaskDto dto = taskConverter.convertToDto(task);

        assertThat(dto).hasFieldOrPropertyWithValue("title", task.getTitle())
                .hasFieldOrPropertyWithValue("description", task.getDescription())
                .hasFieldOrPropertyWithValue("status", task.getStatus())
                .hasFieldOrPropertyWithValue("priority", task.getPriority());
        assertEquals(dto.getAuthorId(), task.getAuthor().getId());
        assertEquals(dto.getImplementorId(), task.getImplementor().getId());
        assertThat(dto.getComments()).isNotEmpty();
    }

    @Test
    void convertToDtoWithoutComments() {
        TaskDto dto = taskConverter.convertToDto(task);

        assertThat(dto).hasFieldOrPropertyWithValue("title", task.getTitle())
                .hasFieldOrPropertyWithValue("description", task.getDescription())
                .hasFieldOrPropertyWithValue("status", task.getStatus())
                .hasFieldOrPropertyWithValue("priority", task.getPriority());
        assertEquals(dto.getAuthorId(), task.getAuthor().getId());
        assertEquals(dto.getImplementorId(), task.getImplementor().getId());
        assertThat(dto.getComments()).isEmpty();
    }

    @Test
    void convertToDtos() {
        when(commentConverter.convertToDtos(any())).thenReturn(List.of(CommentDto.builder().id(1L).build()));
        task.setComments(List.of(comment));

        List<TaskDto> dtos = taskConverter.convertToDtos(List.of(task));

        assertThat(dtos).isNotNull().isNotEmpty();
        TaskDto dto = dtos.get(0);
        assertThat(dto).hasFieldOrPropertyWithValue("title", task.getTitle())
                .hasFieldOrPropertyWithValue("description", task.getDescription())
                .hasFieldOrPropertyWithValue("status", task.getStatus())
                .hasFieldOrPropertyWithValue("priority", task.getPriority());
        assertEquals(dto.getAuthorId(), task.getAuthor().getId());
        assertEquals(dto.getImplementorId(), task.getImplementor().getId());
        assertThat(dto.getComments()).isNotEmpty();
    }
}