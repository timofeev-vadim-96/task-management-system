package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.converter.CommentConverter;
import com.effectivemobile.taskmanagementsystem.dao.CommentDao;
import com.effectivemobile.taskmanagementsystem.dto.CommentDto;
import com.effectivemobile.taskmanagementsystem.exception.AttemptingAccessOtherUserEntityException;
import com.effectivemobile.taskmanagementsystem.model.AppUser;
import com.effectivemobile.taskmanagementsystem.model.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Тест безопасности сервиса для работы с комментариями")
@SpringBootTest
public class CommentServiceImplSecurityTest {
    private final static String ADMIN_EMAIL = "testAdmin@gmail.com"; //id = 1

    private final static String USER_EMAIL = "testUser@gmail.com"; //id = 2 (tasksIds/commentIds = {1,10})

    @Autowired
    private CommentService commentService;

    @MockBean
    private CommentDao commentDao;

    @MockBean
    private CommentConverter commentConverter;

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @WithUserDetails(value = ADMIN_EMAIL)
    void createByAdmin(long id) {
        CommentDto dto = CommentDto.builder()
                .authorId(id)
                .taskId(id)
                .build();
        when(commentDao.save(any(Comment.class))).thenReturn(Comment.builder().build());
        when(commentConverter.convertToDto(any(Comment.class))).thenReturn(dto);

        assertDoesNotThrow(() -> commentService.create(dto));
    }

    @Test
    @WithUserDetails(value = USER_EMAIL)
    void createByUserSuccessfully() {
        final long authorId = 2L;
        final long taskId = 1L;

        CommentDto dto = CommentDto.builder()
                .authorId(authorId)
                .taskId(taskId)
                .build();
        when(commentDao.save(any(Comment.class))).thenReturn(Comment.builder().build());
        when(commentConverter.convertToDto(any(Comment.class))).thenReturn(dto);

        assertDoesNotThrow(() -> commentService.create(dto));
    }

    @Test
    @WithUserDetails(value = USER_EMAIL)
    void createByUserDeniedOnAnotherUser() {
        final long anotherUserId = 3L;

        CommentDto dto = CommentDto.builder()
                .authorId(anotherUserId)
                .taskId(1L)
                .build();

        assertThrowsExactly(AttemptingAccessOtherUserEntityException.class, () -> commentService.create(dto));
    }

    @Test
    @WithUserDetails(value = USER_EMAIL)
    void createByUserDeniedOnAnotherUserTask() {
        final long anotherUserTaskId = 2L;

        CommentDto dto = CommentDto.builder()
                .authorId(2L)
                .taskId(anotherUserTaskId)
                .build();

        assertThrowsExactly(AttemptingAccessOtherUserEntityException.class, () -> commentService.create(dto));
    }

    @Test
    @WithUserDetails(value = USER_EMAIL)
    void updateByUserSuccessfully() {
        final long userId = 2L;
        when(commentDao.findById(userId)).thenReturn(Optional.of(Comment.builder()
                .author(AppUser.builder()
                        .id(userId)
                        .build())
                .build()));
        when(commentDao.save(any(Comment.class))).thenReturn(Comment.builder().build());
        when(commentConverter.convertToDto(any(Comment.class)))
                .thenReturn(CommentDto.builder()
                        .authorId(userId).build());

        assertDoesNotThrow(() -> commentService.update(userId, "any"));
    }

    @Test
    @WithUserDetails(value = USER_EMAIL)
    void updateByUserDeniedOnAnotherUserComment() {
        final long anotherUserCommentId = 3L;
        when(commentDao.findById(anotherUserCommentId)).thenReturn(Optional.of(Comment.builder()
                .author(AppUser.builder()
                        .id(anotherUserCommentId)
                        .build())
                .build()));
        when(commentConverter.convertToDto(any(Comment.class)))
                .thenReturn(CommentDto.builder()
                        .authorId(anotherUserCommentId)
                        .build());

        assertThrowsExactly(AttemptingAccessOtherUserEntityException.class,
                () -> commentService.update(anotherUserCommentId, "any"));
    }
}
