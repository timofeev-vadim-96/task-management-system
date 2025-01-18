package com.effectivemobile.taskmanagementsystem.converter;

import com.effectivemobile.taskmanagementsystem.dto.CommentDto;
import com.effectivemobile.taskmanagementsystem.model.Comment;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentConverter {
    public CommentDto convertToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorId(comment.getAuthor().getId())
                .taskId(comment.getTask().getId())
                .build();
    }

    public List<CommentDto> convertToDtos(Collection<Comment> comments) {
        return comments.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}
