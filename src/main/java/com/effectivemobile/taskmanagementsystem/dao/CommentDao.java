package com.effectivemobile.taskmanagementsystem.dao;

import com.effectivemobile.taskmanagementsystem.model.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentDao extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = {"author", "task"})
    List<Comment> findAllByTaskId(long taskId);
}
