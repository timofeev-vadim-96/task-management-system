package com.effectivemobile.taskmanagementsystem.converter;

import com.effectivemobile.taskmanagementsystem.dto.CommentDto;
import com.effectivemobile.taskmanagementsystem.model.AppUser;
import com.effectivemobile.taskmanagementsystem.model.Comment;
import com.effectivemobile.taskmanagementsystem.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {CommentConverter.class})
class CommentConverterTest {
    @Autowired
    private CommentConverter commentConverter;

    private Comment comment;

    @BeforeEach
    void init() {
        comment = Comment.builder()
                .id(1L)
                .text("some text")
                .author(AppUser.builder().id(1L).build())
                .task(Task.builder().id(1L).build())
                .build();
    }

    @Test
    void convertToDto() {
        CommentDto dto = commentConverter.convertToDto(comment);

        assertThat(dto).isNotNull().hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", comment.getId())
                .hasFieldOrPropertyWithValue("text", comment.getText())
                .hasFieldOrPropertyWithValue("authorId", comment.getAuthor().getId())
                .hasFieldOrPropertyWithValue("taskId", comment.getTask().getId());
    }

    @Test
    void convertToDtos() {
        List<CommentDto> dtos = commentConverter.convertToDtos(List.of(comment));
        CommentDto dto = dtos.get(0);

        assertThat(dto).isNotNull().hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", comment.getId())
                .hasFieldOrPropertyWithValue("text", comment.getText())
                .hasFieldOrPropertyWithValue("authorId", comment.getAuthor().getId())
                .hasFieldOrPropertyWithValue("taskId", comment.getTask().getId());
    }
}