package com.effectivemobile.taskmanagementsystem.dao;

import com.effectivemobile.taskmanagementsystem.model.Task;
import com.effectivemobile.taskmanagementsystem.util.SearchCriteria;
import com.effectivemobile.taskmanagementsystem.util.TaskPriority;
import com.effectivemobile.taskmanagementsystem.util.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Кастомный репозиторий для получения тасок с пагинацией и фильтрацией по критериям")
@DataJpaTest
@Import(SearchCriteriaWithPaginationTaskDaoImpl.class)
class SearchCriteriaWithPaginationTaskDaoImplTest {
    @Autowired
    private SearchCriteriaWithPaginationTaskDao criteriaDao;

    @ParameterizedTest
    @MethodSource("getArguments")
    void findAll(SearchCriteria criteria, int expectedResultSize) {
        Pageable pageable = PageRequest.of(1, 20);
        Page<Task> tasks = criteriaDao.findAll(List.of(criteria), pageable);

        assertEquals(expectedResultSize, tasks.getTotalElements());
        assertEquals(1, tasks.getTotalPages());
    }

    private static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of(new SearchCriteria("author", ":", 1), 10),
                Arguments.of(new SearchCriteria("implementor", ":", 2), 2),
                Arguments.of(new SearchCriteria("status", ":", TaskStatus.В_ПРОЦЕССЕ), 3),
                Arguments.of(new SearchCriteria("priority", ":", TaskPriority.СРЕДНИЙ), 3));
    }
}