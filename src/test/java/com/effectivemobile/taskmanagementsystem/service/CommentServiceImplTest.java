package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.converter.CommentConverter;
import com.effectivemobile.taskmanagementsystem.dao.CommentDao;
import com.effectivemobile.taskmanagementsystem.dao.TaskDao;
import com.effectivemobile.taskmanagementsystem.dto.CommentDto;
import com.effectivemobile.taskmanagementsystem.exception.EntityNotFoundException;
import com.effectivemobile.taskmanagementsystem.model.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DataJpaTest
@Import({CommentServiceImpl.class, CommentConverter.class, UserServiceImpl.class})
@DisplayName("Сервис для работы с комментариями")
@Transactional(propagation = Propagation.NEVER)
class CommentServiceImplTest {
    @Autowired
    private CommentService commentService;

    @SpyBean
    private CommentDao commentDao;

    @SpyBean
    private UserService userService;

    @SpyBean
    private TaskDao taskDao;

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void get(long id) {
        CommentDto comment = commentService.get(id);

        assertThat(comment).isNotNull().hasFieldOrPropertyWithValue("id", id);
        verify(commentDao, times(1)).findById(id);
    }

    @Test
    void getNegative() {
        long notExistingId = 11;

        assertThrowsExactly(EntityNotFoundException.class, () -> commentService.get(notExistingId));
        verify(commentDao, times(1)).findById(notExistingId);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void getAllByTask(long taskId) {
        List<CommentDto> commentsTyTaskId = commentService.getAllByTask(taskId);

        assertThat(commentsTyTaskId).isNotNull()
                .isNotEmpty()
                .allSatisfy(commentDto ->
                        assertThat(commentDto.getTaskId()).isEqualTo(taskId));
        verify(commentDao, times(1)).findAllByTaskId(taskId);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void create() {
        CommentDto dto = CommentDto.builder()
                .text("test comment text")
                .taskId(1L)
                .authorId(2L)
                .build();

        CommentDto saved = commentService.create(dto);

        assertThat(saved).isNotNull()
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(dto);
        verify(commentDao, times(1)).save(any());
    }

    @Test
    void createNegativeWhenAuthorDoesNotExists() {
        long notExistingAuthor = 11L;
        CommentDto dto = CommentDto.builder()
                .text("test comment text")
                .taskId(1L)
                .authorId(notExistingAuthor)
                .build();

        assertThrowsExactly(EntityNotFoundException.class, () -> commentService.create(dto));
        verify(userService, times(1)).getById(notExistingAuthor);
    }

    @Test
    void createNegativeWhenTaskDoesNotExists() {
        long notExistingTask = 11L;
        CommentDto dto = CommentDto.builder()
                .text("test comment text")
                .taskId(notExistingTask)
                .authorId(1L)
                .build();

        assertThrowsExactly(EntityNotFoundException.class, () -> commentService.create(dto));
        verify(taskDao, times(1)).findById(notExistingTask);
    }

    @Test
    void update() {
        String textToUpdate = "updated comment text";
        long commentId = 1L;

        CommentDto updated = commentService.update(commentId, textToUpdate);
        Comment comment = commentDao.findById(commentId).get();

        assertThat(updated).isNotNull()
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", commentId)
                .hasFieldOrPropertyWithValue("text", textToUpdate);
        assertEquals(textToUpdate, comment.getText());
        verify(commentDao, times(1)).save(any());
    }

    @Test
    void updateNegative() {
        long notExistingId = 11L;

        assertThrowsExactly(EntityNotFoundException.class, () -> commentService.update(notExistingId, "any"));
        verify(commentDao, times(1)).findById(notExistingId);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteById() {
        long commentId = 1L;
        assertDoesNotThrow(() -> commentDao.findById(commentId));

        commentService.deleteById(commentId);

        assertThrowsExactly(EntityNotFoundException.class, () -> commentService.get(commentId));
        verify(commentDao, times(1)).deleteById(commentId);
    }
}