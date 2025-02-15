package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.dao.SearchCriteriaWithPaginationTaskDao;
import com.effectivemobile.taskmanagementsystem.dao.SearchCriteriaWithPaginationTaskDaoImpl;
import com.effectivemobile.taskmanagementsystem.dao.TaskDao;
import com.effectivemobile.taskmanagementsystem.dto.request.task.TaskDtoCreateRequest;
import com.effectivemobile.taskmanagementsystem.dto.request.task.TaskDtoUpdateRequest;
import com.effectivemobile.taskmanagementsystem.dto.response.TaskDtoResponse;
import com.effectivemobile.taskmanagementsystem.exception.EntityNotFoundException;
import com.effectivemobile.taskmanagementsystem.mapper.CommentMapper;
import com.effectivemobile.taskmanagementsystem.mapper.TaskMapper;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@Import({TaskServiceImpl.class, UserServiceImpl.class, TaskMapper.class, CommentMapper.class,
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
        TaskDtoResponse task = taskService.get(id);

        assertThat(task).isNotNull().hasFieldOrPropertyWithValue("id", id);
        verify(taskDao, times(1)).findById(id);
    }

    @ParameterizedTest
    @MethodSource("getArguments")
    void getALl(Long implementorId, Long authorId,
                TaskStatus status, TaskPriority priority, long expectedResultSize) {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));

        Page<TaskDtoResponse> tasks = taskService.getAll(implementorId, authorId, status, priority,
                pageable);

        assertEquals(expectedResultSize, tasks.getTotalElements());
        assertEquals(1, tasks.getTotalPages());
        verify(criteriaDao, times(1)).findAll(any(), any());
    }

    @Test
    void getAllByAuthor() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        final long authorId = 1L;

        Page<TaskDtoResponse> tasks = taskService.getAll(null, authorId, null, null,
                pageable);

        assertEquals(10, tasks.getTotalElements());
        assertEquals(1, tasks.getTotalPages());
        verify(criteriaDao, times(1)).findAll(any(), any());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void create() {
        TaskDtoCreateRequest dto = TaskDtoCreateRequest.builder()
                .title("title")
                .description("description")
                .priority(TaskPriority.LOW)
                .status(TaskStatus.COMPLETED)
                .authorId(1L)
                .implementorId(2L)
                .build();

        TaskDtoResponse created = taskService.create(dto);

        assertThat(created).isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id", "comments")
                .isEqualTo(dto);
        verify(taskDao, times(1)).save(any());
    }

    @Test
    void creteNegativeWhenAuthorDoesNotExists() {
        long notExistingAuthorId = 11L;
        TaskDtoCreateRequest dto = TaskDtoCreateRequest.builder()
                .authorId(11L)
                .build();

        assertThrowsExactly(EntityNotFoundException.class, () -> taskService.create(dto));
        verify(userService, times(1)).getById(notExistingAuthorId);
    }

    @Test
    void creteNegativeWhenImplementorDoesNotExists() {
        long notExistingImplementorId = 11L;
        TaskDtoCreateRequest dto = TaskDtoCreateRequest.builder()
                .authorId(11L)
                .build();

        assertThrowsExactly(EntityNotFoundException.class, () -> taskService.create(dto));
        verify(userService, times(1)).getById(notExistingImplementorId);
    }

    @Test
    void update() {
        TaskDtoUpdateRequest dto = TaskDtoUpdateRequest.builder()
                .id(1L)
                .title("title")
                .description("description")
                .priority(TaskPriority.LOW)
                .status(TaskStatus.COMPLETED)
                .authorId(1L)
                .implementorId(2L)
                .build();

        TaskDtoResponse updated = taskService.update(dto);
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
        TaskDtoUpdateRequest dto = TaskDtoUpdateRequest.builder()
                .id(1L)
                .implementorId(11L)
                .build();

        assertThrowsExactly(EntityNotFoundException.class, () -> taskService.update(dto));
        verify(userService, times(1)).getById(notExistingImplementorId);
    }

    @Test
    void updateNegativeWhenTaskDoesNotExists() {
        long notExistingId = 11L;
        TaskDtoUpdateRequest dto = TaskDtoUpdateRequest.builder()
                .id(11L)
                .build();

        assertThrowsExactly(EntityNotFoundException.class, () -> taskService.update(dto));
        verify(taskDao, times(1)).findById(notExistingId);
    }

    @Test
    void updateStatus() {
        long id = 1L;
        TaskStatus status = TaskStatus.COMPLETED;

        TaskDtoResponse updated = taskService.update(id, status);
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
                () -> taskService.update(notExistingId, TaskStatus.COMPLETED));
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
                Arguments.of(null, null, TaskStatus.IN_PROCESS, null, 3),
                Arguments.of(null, null, null, TaskPriority.MIDDLE, 3)
        );
    }
}