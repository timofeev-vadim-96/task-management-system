package com.effectivemobile.taskmanagementsystem.mapper;

import com.effectivemobile.taskmanagementsystem.dto.response.CommentDtoResponse;
import com.effectivemobile.taskmanagementsystem.dto.response.TaskDtoResponse;
import com.effectivemobile.taskmanagementsystem.model.Comment;
import com.effectivemobile.taskmanagementsystem.model.Task;
import com.effectivemobile.taskmanagementsystem.model.User;
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

@SpringBootTest(classes = {TaskMapper.class, CommentMapper.class})
class TaskMapperTest {
    @Autowired
    private TaskMapper taskMapper;

    @MockBean
    private CommentMapper commentMapper;

    private Task task;

    private Comment comment;

    @BeforeEach
    void init() {
        task = Task.builder()
                .id(1L)
                .title("some title")
                .description("some description")
                .priority(TaskPriority.LOW)
                .status(TaskStatus.IN_STAY)
                .author(new User())
                .implementor(new User())
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("some text")
                .author(new User())
                .task(task)
                .build();

        when(commentMapper.convertToDto(any(Comment.class))).thenReturn(CommentDtoResponse.builder().id(1L).build());
    }

    @Test
    void convertToDtoWithComments() {
        when(commentMapper.convertToDtos(any())).thenReturn(List.of(CommentDtoResponse.builder().id(1L).build()));
        task.setComments(List.of(comment));

        TaskDtoResponse dto = taskMapper.convertToDto(task);

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
        TaskDtoResponse dto = taskMapper.convertToDto(task);

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
        when(commentMapper.convertToDtos(any())).thenReturn(List.of(CommentDtoResponse.builder().id(1L).build()));
        task.setComments(List.of(comment));

        List<TaskDtoResponse> dtos = taskMapper.convertToDtos(List.of(task));

        assertThat(dtos).isNotNull().isNotEmpty();
        TaskDtoResponse dto = dtos.get(0);
        assertThat(dto).hasFieldOrPropertyWithValue("title", task.getTitle())
                .hasFieldOrPropertyWithValue("description", task.getDescription())
                .hasFieldOrPropertyWithValue("status", task.getStatus())
                .hasFieldOrPropertyWithValue("priority", task.getPriority());
        assertEquals(dto.getAuthorId(), task.getAuthor().getId());
        assertEquals(dto.getImplementorId(), task.getImplementor().getId());
        assertThat(dto.getComments()).isNotEmpty();
    }
}