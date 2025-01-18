package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.converter.CommentConverter;
import com.effectivemobile.taskmanagementsystem.dao.CommentDao;
import com.effectivemobile.taskmanagementsystem.dao.TaskDao;
import com.effectivemobile.taskmanagementsystem.dto.CommentDto;
import com.effectivemobile.taskmanagementsystem.exception.EntityNotFoundException;
import com.effectivemobile.taskmanagementsystem.model.AppUser;
import com.effectivemobile.taskmanagementsystem.model.Comment;
import com.effectivemobile.taskmanagementsystem.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentDao commentDao;

    private final CommentConverter commentConverter;

    private final UserService userService;

    private final TaskDao taskDao;

    @Override
    @Transactional(readOnly = true)
    public CommentDto get(long id) {
        Comment comment = commentDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id = %d is not found".formatted(id)));
        return commentConverter.convertToDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("comments")
    public List<CommentDto> getAllByTask(long taskId) {
        return commentConverter.convertToDtos(commentDao.findAllByTaskId(taskId));
    }

    @Override
    @Transactional
    public CommentDto create(CommentDto dto) {
        Task task = taskDao.findById(dto.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Task with id = %d is not found"
                        .formatted(dto.getTaskId())));
        AppUser author = userService.getById(dto.getAuthorId());

        Comment comment = Comment.builder()
                .text(dto.getText())
                .task(task)
                .author(author)
                .build();

        return commentConverter.convertToDto(commentDao.save(comment));
    }

    @Override
    @Transactional
    public CommentDto update(long id, String text) {
        Comment comment = commentDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id = %d is not found".formatted(id)));
        comment.setText(text);
        return commentConverter.convertToDto(commentDao.save(comment));
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentDao.deleteById(id);
    }
}
