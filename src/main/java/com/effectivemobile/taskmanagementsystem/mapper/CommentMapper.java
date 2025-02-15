package com.effectivemobile.taskmanagementsystem.mapper;

import com.effectivemobile.taskmanagementsystem.dto.response.CommentDtoResponse;
import com.effectivemobile.taskmanagementsystem.model.Comment;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper implements DtoMapper<CommentDtoResponse, Comment> {
    public CommentDtoResponse convertToDto(Comment comment) {
        return CommentDtoResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorId(comment.getAuthor().getId())
                .taskId(comment.getTask().getId())
                .build();
    }

    public List<CommentDtoResponse> convertToDtos(Collection<Comment> comments) {
        return comments.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}
