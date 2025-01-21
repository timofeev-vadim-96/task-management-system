package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.converter.CommentConverter;
import com.effectivemobile.taskmanagementsystem.converter.TaskConverter;
import com.effectivemobile.taskmanagementsystem.dao.SearchCriteriaWithPaginationTaskDao;
import com.effectivemobile.taskmanagementsystem.dao.SearchCriteriaWithPaginationTaskDaoImpl;
import com.effectivemobile.taskmanagementsystem.dao.TaskDao;
import com.effectivemobile.taskmanagementsystem.dto.TaskDto;
import com.effectivemobile.taskmanagementsystem.exception.EntityNotFoundException;
import com.effectivemobile.taskmanagementsystem.exception.UnsatisfactoryValueException;
import com.effectivemobile.taskmanagementsystem.model.Task;
import com.effectivemobile.taskmanagementsystem.util.TaskPriority;
import com.effectivemobile.taskmanagementsystem.util.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Сервис для работы с тасками")
@DataJpaTest
@Import({TaskServiceImpl.class, UserServiceImpl.class, TaskConverter.class, CommentConverter.class,
        SearchCriteriaWithPaginationTaskDaoImpl.class})
@Transactional(propagation = Propagation.NEVER)
class TaskServiceImplTest {
    @Autowired
    private TaskService taskService;

    @SpyBean
    private TaskDao taskDao;

    @SpyBean
    private UserService userService;

    @SpyBean
    private SearchCriteriaWithPaginationTaskDao criteriaDao;

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void get(long id) {
        TaskDto task = taskService.get(id);

        assertThat(task).isNotNull().hasFieldOrPropertyWithValue("id", id);
        verify(taskDao, times(1)).findById(id);
    }

    @ParameterizedTest
    @MethodSource("getArguments")
    void getALl(Long implementorId, Long authorId,
                TaskStatus status, TaskPriority priority, long expectedResultSize) {
        final int page = 1;
        final int size = 20;

        Page<TaskDto> tasks = taskService.getAll(implementorId, authorId, status, priority,
                page, size);

        assertEquals(expectedResultSize, tasks.getTotalElements());
        assertEquals(page, tasks.getTotalPages());
        verify(criteriaDao, times(1)).findAll(any(), any());
    }

    @Test
    void getAllByAuthor() {
        final int expectedTasksSize = 10;
        final int page = 1;
        final long authorId = 1L;

        Page<TaskDto> tasks = taskService.getAll(null, authorId, null, null,
                page, expectedTasksSize);

        assertEquals(expectedTasksSize, tasks.getTotalElements());
        assertEquals(page, tasks.getTotalPages());
        verify(criteriaDao, times(1)).findAll(any(), any());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void create() {
        TaskDto dto = TaskDto.builder()
                .title("title")
                .description("description")
                .priority(TaskPriority.НИЗКИЙ)
                .status(TaskStatus.ЗАВЕРШЕНО)
                .authorId(1L)
                .implementorId(2L)
                .build();

        TaskDto created = taskService.create(dto);

        assertThat(created).isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id", "comments")
                .isEqualTo(dto);
        verify(taskDao, times(1)).save(any());
    }

    @Test
    void creteNegativeWhenAuthorDoesNotExists() {
        long notExistingAuthorId = 11L;
        TaskDto dto = TaskDto.builder()
                .id(1L)
                .authorId(11L)
                .build();

        assertThrowsExactly(EntityNotFoundException.class, () -> taskService.create(dto));
        verify(userService, times(1)).getById(notExistingAuthorId);
    }

    @Test
    void creteNegativeWhenImplementorDoesNotExists() {
        long notExistingImplementorId = 11L;
        TaskDto dto = TaskDto.builder()
                .id(1L)
                .authorId(11L)
                .build();

        assertThrowsExactly(EntityNotFoundException.class, () -> taskService.create(dto));
        verify(userService, times(1)).getById(notExistingImplementorId);
    }

    @Test
    void update() {
        TaskDto dto = TaskDto.builder()
                .id(1L)
                .title("title")
                .description("description")
                .priority(TaskPriority.НИЗКИЙ)
                .status(TaskStatus.ЗАВЕРШЕНО)
                .authorId(1L)
                .implementorId(2L)
                .build();

        TaskDto updated = taskService.update(dto);
        Task task = taskDao.findById(dto.getId()).get();

        assertThat(updated).isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("comments")
                .isEqualTo(dto);
        assertThat(task).hasFieldOrPropertyWithValue("title", dto.getTitle())
                .hasFieldOrPropertyWithValue("description", dto.getDescription())
                .hasFieldOrPropertyWithValue("status", dto.getStatus())
                .hasFieldOrPropertyWithValue("priority", dto.getPriority());
        assertEquals(dto.getAuthorId(), task.getAuthor().getId());
        assertEquals(dto.getImplementorId(), task.getImplementor().getId());

        verify(taskDao, times(1)).save(any());
    }

    @Test
    void updateNegativeWhenAuthorDoesNotExists() {
        long notExistingImplementorId = 11L;
        TaskDto dto = TaskDto.builder()
                .id(1L)
                .implementorId(11L)
                .build();

        assertThrowsExactly(EntityNotFoundException.class, () -> taskService.update(dto));
        verify(userService, times(1)).getById(notExistingImplementorId);
    }

    @Test
    void updateNegativeWhenIdIsNull() {
        TaskDto dto = TaskDto.builder().build();

        assertThrowsExactly(UnsatisfactoryValueException.class, () -> taskService.update(dto));
    }

    @Test
    void updateNegativeWhenTaskDoesNotExists() {
        long notExistingId = 11L;
        TaskDto dto = TaskDto.builder()
                .id(11L)
                .build();

        assertThrowsExactly(EntityNotFoundException.class, () -> taskService.update(dto));
        verify(taskDao, times(1)).findById(notExistingId);
    }

    @Test
    void updateStatus() {
        long id = 1L;
        TaskStatus status = TaskStatus.ЗАВЕРШЕНО;

        TaskDto updated = taskService.update(id, status);
        Task task = taskDao.findById(id).get();

        assertThat(updated).isNotNull()
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("status", status);
        assertEquals(status, task.getStatus());
        verify(taskDao, times(1)).save(any());
    }

    @Test
    void updateStatusNegative() {
        long notExistingId = 11L;

        assertThrowsExactly(EntityNotFoundException.class,
                () -> taskService.update(notExistingId, TaskStatus.ЗАВЕРШЕНО));
        verify(taskDao, times(1)).findById(notExistingId);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteById() {
        long commentId = 1L;
        assertDoesNotThrow(() -> taskDao.findById(commentId));

        taskService.deleteById(commentId);

        assertThrowsExactly(EntityNotFoundException.class, () -> taskService.get(commentId));
        verify(taskDao, times(1)).deleteById(commentId);
    }

    private static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of(null, null, null, null, 10),
                Arguments.of(null, 1L, null, null, 10),
                Arguments.of(2L, null, null, null, 2),
                Arguments.of(null, null, TaskStatus.В_ПРОЦЕССЕ, null, 3),
                Arguments.of(null, null, null, TaskPriority.СРЕДНИЙ, 3)
        );
    }
}