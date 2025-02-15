package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.dto.request.comment.CommentDtoCreateRequest;
import com.effectivemobile.taskmanagementsystem.dto.response.CommentDtoResponse;

import java.util.List;

public interface CommentService {
    CommentDtoResponse get(long id);

    List<CommentDtoResponse> getAllByTask(long id);

    CommentDtoResponse create(CommentDtoCreateRequest dto);

    CommentDtoResponse update(long id, String text);

    void deleteById(long id);
}

