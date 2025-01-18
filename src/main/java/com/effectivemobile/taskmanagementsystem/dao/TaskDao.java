package com.effectivemobile.taskmanagementsystem.dao;

import com.effectivemobile.taskmanagementsystem.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskDao extends JpaRepository<Task, Long> {
    Long id(Long id);
}
