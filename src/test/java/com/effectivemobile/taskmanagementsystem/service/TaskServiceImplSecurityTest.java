package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.converter.TaskConverter;
import com.effectivemobile.taskmanagementsystem.dao.SearchCriteriaWithPaginationTaskDao;
import com.effectivemobile.taskmanagementsystem.dao.TaskDao;
import com.effectivemobile.taskmanagementsystem.dto.TaskDto;
import com.effectivemobile.taskmanagementsystem.exception.AttemptingAccessOtherUserEntityException;
import com.effectivemobile.taskmanagementsystem.model.Task;
import com.effectivemobile.taskmanagementsystem.util.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Тест безопасности сервиса для работы с тасками")
@SpringBootTest
@Transactional(propagation = Propagation.NEVER)
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

    @Test
    @WithUserDetails(value = ADMIN_EMAIL)
    void getAllByAdmin() {
        final int expectedTasksSize = 10;
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));

        Page<TaskDto> tasks = taskService.getAll(null, null, null, null, pageable);
        assertEquals(expectedTasksSize, tasks.getTotalElements());
        verify(criteriaDao, atLeastOnce()).findAll(any(List.class), any(Pageable.class));
    }

    /**
     * User with id = 2 have only 2 tasks. For this specific logic we need to use db to ensure filtering enabled
     */
    @Test
    @WithUserDetails(value = USER_EMAIL)
    void getAllByUserSuccessfully() {
        final long userId = 2L;
        final int expectedTasksSize = 2;
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));

        Page<TaskDto> tasks = taskService
                .getAll(userId, null, null, null, pageable);
        Page<TaskDto> tasksWithoutFilterByAuthor = taskService
                .getAll(null, null, null, null, pageable);

        assertEquals(expectedTasksSize, tasks.getTotalElements());
        assertEquals(expectedTasksSize, tasksWithoutFilterByAuthor.getTotalElements());
        verify(criteriaDao, atLeastOnce()).findAll(any(List.class), any(Pageable.class));
    }

    @Test
    @WithUserDetails(value = USER_EMAIL)
    void getAllByUserDeniedByImplementorIdParam() {
        final long anotherUserId = 3L;
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));

        assertThrowsExactly(AttemptingAccessOtherUserEntityException.class,
                () -> taskService.getAll(anotherUserId, null, null, null, pageable));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @WithUserDetails(value = ADMIN_EMAIL)
    void getByAdmin(long id) {
        when(taskDao.findById(id))
                .thenReturn(Optional.of(Task.builder().id(id).build()));
        when(taskConverter.convertToDto(any(Task.class)))
                .thenReturn(TaskDto.builder().id(id).build());

        assertDoesNotThrow(() -> taskService.get(id));
    }

    @Test
    @WithUserDetails(value = USER_EMAIL)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getByUserSuccessfully() {
        final long userTaskId = 1L;
        final long userId = 2L;
        when(taskDao.findById(userTaskId))
                .thenReturn(Optional.of(Task.builder().build()));
        when(taskConverter.convertToDto(any(Task.class)))
                .thenReturn(TaskDto.builder().id(userTaskId).implementorId(userId).build());

        assertDoesNotThrow(() -> taskService.get(userTaskId));
    }

    @Test
    @WithUserDetails(value = USER_EMAIL)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getByUserDeniedByAnotherUserTask() {
        final long anotherUserTaskId = 3L;
        final long anotherUserId = 4L;
        when(taskDao.findById(anotherUserTaskId))
                .thenReturn(Optional.of(Task.builder().build()));
        when(taskConverter.convertToDto(any(Task.class)))
                .thenReturn(TaskDto.builder().id(anotherUserTaskId).implementorId(anotherUserId).build());

        assertThrowsExactly(AttemptingAccessOtherUserEntityException.class, () -> taskService.get(anotherUserTaskId));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @WithUserDetails(value = ADMIN_EMAIL)
    void updateByAdmin(long id) {
        final long adminId = 1L;
        when(taskDao.findById(id))
                .thenReturn(Optional.of(Task.builder().build()));
        when(taskDao.save(any(Task.class))).thenReturn(Task.builder().id(id).build());
        when(taskConverter.convertToDto(any(Task.class)))
                .thenReturn(TaskDto.builder().id(id).authorId(adminId).build());

        assertDoesNotThrow(() -> taskService.update(id, TaskStatus.ЗАВЕРШЕНО));
    }

    @Test
    @WithUserDetails(value = USER_EMAIL)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateByUserSuccessfully() {
        final long userTaskId = 1L;
        final long userId = 2L;
        when(taskDao.findById(userTaskId))
                .thenReturn(Optional.of(Task.builder().build()));
        when(taskDao.save(any(Task.class))).thenReturn(Task.builder().build());
        when(taskConverter.convertToDto(any(Task.class)))
                .thenReturn(TaskDto.builder().id(userTaskId).implementorId(userId).build());

        assertDoesNotThrow(() -> taskService.update(userTaskId, TaskStatus.ЗАВЕРШЕНО));
    }

    @Test
    @WithUserDetails(value = USER_EMAIL)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
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
