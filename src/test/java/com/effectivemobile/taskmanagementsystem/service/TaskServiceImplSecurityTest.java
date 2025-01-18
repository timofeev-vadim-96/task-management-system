package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.converter.TaskConverter;
import com.effectivemobile.taskmanagementsystem.dao.SearchCriteriaWithPaginationTaskDao;
import com.effectivemobile.taskmanagementsystem.dao.TaskDao;
import com.effectivemobile.taskmanagementsystem.dto.TaskDto;
import com.effectivemobile.taskmanagementsystem.exception.AttemptingAccessOtherUserEntityException;
import com.effectivemobile.taskmanagementsystem.model.Task;
import com.effectivemobile.taskmanagementsystem.util.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@DisplayName("Тест безопасности сервиса для работы с тасками")
@SpringBootTest
public class TaskServiceImplSecurityTest {
    private final static String ADMIN_EMAIL = "testAdmin@gmail.com"; //id = 1

    private final static String USER_EMAIL = "testUser@gmail.com"; //id = 2 (tasksIds = {1,10})

    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskDao taskDao;

    @MockBean
    private TaskConverter taskConverter;

    @SpyBean
    private SearchCriteriaWithPaginationTaskDao criteriaDao;

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @WithUserDetails(value = ADMIN_EMAIL)
    void getAllByAdmin(long implementorId) {
        doReturn(Page.empty()).when(criteriaDao).findAll(any(List.class), any(Pageable.class));

        assertDoesNotThrow(
                () -> taskService.getAll(implementorId, null, null, null, 1, 20));
    }

    /**
     * here we use embedded db to check for forced filtering for a specific user
     */
    @Test
    @WithUserDetails(value = USER_EMAIL)
    void getAllByUserSuccessfully() {
        final long userId = 2L;
        final int expectedTasksSize = 2;

        Page<TaskDto> tasks = taskService
                .getAll(userId, null, null, null, 1, 20);
        Page<TaskDto> tasksWithoutFilterByAuthor = taskService
                .getAll(null, null, null, null, 1, 20);

        assertEquals(expectedTasksSize, tasks.getTotalElements());
        assertEquals(expectedTasksSize, tasksWithoutFilterByAuthor.getTotalElements());
    }

    @Test
    @WithUserDetails(value = USER_EMAIL)
    void getAllByUserDeniedByImplementorIdParam() {
        final long anotherUserId = 3L;

        assertThrowsExactly(AttemptingAccessOtherUserEntityException.class,
                () -> taskService.getAll(anotherUserId, null, null, null, 1, 20));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @WithUserDetails(value = ADMIN_EMAIL)
    void getByAdmin(long id) {
        when(taskDao.findById(id))
                .thenReturn(Optional.of(Task.builder().build()));
        when(taskConverter.convertToDto(any(Task.class)))
                .thenReturn(TaskDto.builder().implementorId(id).build());

        assertDoesNotThrow(() -> taskService.get(id));
    }

    @Test
    @WithUserDetails(value = USER_EMAIL)
    void getByUserSuccessfully() {
        final long userTaskId = 1L;
        final long userId = 2L;
        when(taskDao.findById(userTaskId))
                .thenReturn(Optional.of(Task.builder().build()));
        when(taskConverter.convertToDto(any(Task.class)))
                .thenReturn(TaskDto.builder().implementorId(userId).build());

        assertDoesNotThrow(() -> taskService.get(userTaskId));
    }

    @Test
    @WithUserDetails(value = USER_EMAIL)
    void getByUserDeniedByAnotherUserTask() {
        final long anotherUserTaskId = 3L;
        final long anotherUserId = 4L;
        when(taskDao.findById(anotherUserTaskId))
                .thenReturn(Optional.of(Task.builder().build()));
        when(taskConverter.convertToDto(any(Task.class)))
                .thenReturn(TaskDto.builder().implementorId(anotherUserId).build());

        assertThrowsExactly(AttemptingAccessOtherUserEntityException.class, () -> taskService.get(anotherUserTaskId));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @WithUserDetails(value = ADMIN_EMAIL)
    void updateByAdmin(long id) {
        final long adminId = 1L;
        when(taskDao.findById(id))
                .thenReturn(Optional.of(Task.builder().build()));
        when(taskConverter.convertToDto(any(Task.class)))
                .thenReturn(TaskDto.builder().implementorId(adminId).build());

        assertDoesNotThrow(() -> taskService.update(id, TaskStatus.ЗАВЕРШЕНО));
    }

    @Test
    @WithUserDetails(value = USER_EMAIL)
    void updateByUserSuccessfully() {
        final long userTaskId = 1L;
        final long userId = 2L;
        when(taskDao.findById(userTaskId))
                .thenReturn(Optional.of(Task.builder().build()));
        when(taskConverter.convertToDto(any(Task.class)))
                .thenReturn(TaskDto.builder().implementorId(userId).build());

        assertDoesNotThrow(() -> taskService.update(userTaskId, TaskStatus.ЗАВЕРШЕНО));
    }

    @Test
    @WithUserDetails(value = USER_EMAIL)
    void updateByUserDeniedByAnotherUserTask() {
        final long anotherUserTaskId = 3L;
        final long anotherUserId = 4L;
        when(taskDao.findById(anotherUserTaskId))
                .thenReturn(Optional.of(Task.builder().build()));
        when(taskConverter.convertToDto(any(Task.class)))
                .thenReturn(TaskDto.builder().implementorId(anotherUserId).build());

        assertThrowsExactly(AttemptingAccessOtherUserEntityException.class,
                () -> taskService.update(anotherUserTaskId, TaskStatus.ЗАВЕРШЕНО));
    }
}
