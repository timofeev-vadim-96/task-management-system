package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.dao.CommentDao;
import com.effectivemobile.taskmanagementsystem.dao.TaskDao;
import com.effectivemobile.taskmanagementsystem.dto.request.comment.CommentDtoCreateRequest;
import com.effectivemobile.taskmanagementsystem.dto.response.CommentDtoResponse;
import com.effectivemobile.taskmanagementsystem.exception.EntityNotFoundException;
import com.effectivemobile.taskmanagementsystem.mapper.DtoMapper;
import com.effectivemobile.taskmanagementsystem.model.Comment;
import com.effectivemobile.taskmanagementsystem.model.Task;
import com.effectivemobile.taskmanagementsystem.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentDao commentDao;

    private final DtoMapper<CommentDtoResponse, Comment> commentMapper;

    private final UserService userService;

    private final TaskDao taskDao;

    @Override
    @Transactional(readOnly = true)
    public CommentDtoResponse get(long id) {
        Comment comment = commentDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id = %d is not found".formatted(id)));
        return commentMapper.convertToDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("comments")
    public List<CommentDtoResponse> getAllByTask(long taskId) {
        return commentMapper.convertToDtos(commentDao.findAllByTaskId(taskId));
    }

    @Override
    @Transactional
    public CommentDtoResponse create(CommentDtoCreateRequest dto) {
        Task task = taskDao.findById(dto.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Task with id = %d is not found"
                        .formatted(dto.getTaskId())));
        User author = userService.getById(dto.getAuthorId());

        Comment comment = Comment.builder()
                .text(dto.getText())
                .task(task)
                .author(author)
                .build();

        return commentMapper.convertToDto(commentDao.save(comment));
    }

    @Override
    @Transactional
    public CommentDtoResponse update(long id, String text) {
        Comment comment = commentDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id = %d is not found".formatted(id)));
        comment.setText(text);
        return commentMapper.convertToDto(commentDao.save(comment));
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentDao.deleteById(id);
    }
}
