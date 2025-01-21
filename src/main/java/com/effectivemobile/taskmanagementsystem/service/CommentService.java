package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto get(long id);

    List<CommentDto> getAllByTask(long id);

    CommentDto create(CommentDto dto);

    CommentDto update(long id, String text);

    void deleteById(long id);
}

