package com.effectivemobile.taskmanagementsystem.dao;

import com.effectivemobile.taskmanagementsystem.model.Task;
import com.effectivemobile.taskmanagementsystem.util.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchCriteriaWithPaginationTaskDao {
    Page<Task> findAll(List<SearchCriteria> params, Pageable pageable);
}
