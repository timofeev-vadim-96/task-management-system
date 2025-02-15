package com.effectivemobile.taskmanagementsystem.mapper;

import com.effectivemobile.taskmanagementsystem.dto.response.CommentDtoResponse;
import com.effectivemobile.taskmanagementsystem.model.Comment;
import com.effectivemobile.taskmanagementsystem.model.Task;
import com.effectivemobile.taskmanagementsystem.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {CommentMapper.class})
class CommentMapperTest {
    @Autowired
    private CommentMapper commentMapper;

    private Comment comment;

    @BeforeEach
    void init() {
        comment = Comment.builder()
                .id(1L)
                .text("some text")
                .author(User.builder().id(1L).build())
                .task(Task.builder().id(1L).build())
                .build();
    }

    @Test
    void convertToDto() {
        CommentDtoResponse dto = commentMapper.convertToDto(comment);

        assertThat(dto).isNotNull().hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", comment.getId())
                .hasFieldOrPropertyWithValue("text", comment.getText())
                .hasFieldOrPropertyWithValue("authorId", comment.getAuthor().getId())
                .hasFieldOrPropertyWithValue("taskId", comment.getTask().getId());
    }

    @Test
    void convertToDtos() {
        List<CommentDtoResponse> dtos = commentMapper.convertToDtos(List.of(comment));
        CommentDtoResponse dto = dtos.get(0);

        assertThat(dto).isNotNull().hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", comment.getId())
                .hasFieldOrPropertyWithValue("text", comment.getText())
                .hasFieldOrPropertyWithValue("authorId", comment.getAuthor().getId())
                .hasFieldOrPropertyWithValue("taskId", comment.getTask().getId());
    }
}