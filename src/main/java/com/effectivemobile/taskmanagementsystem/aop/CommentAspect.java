package com.effectivemobile.taskmanagementsystem.aop;

import com.effectivemobile.taskmanagementsystem.dto.request.comment.CommentDtoCreateRequest;
import com.effectivemobile.taskmanagementsystem.dto.response.CommentDtoResponse;
import com.effectivemobile.taskmanagementsystem.dto.response.TaskDtoResponse;
import com.effectivemobile.taskmanagementsystem.exception.AttemptingAccessOtherUserEntityException;
import com.effectivemobile.taskmanagementsystem.model.User;
import com.effectivemobile.taskmanagementsystem.service.CommentService;
import com.effectivemobile.taskmanagementsystem.service.TaskService;
import com.effectivemobile.taskmanagementsystem.service.UserService;
import com.effectivemobile.taskmanagementsystem.util.Role;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CommentAspect {
    private final UserService userService;

    private final CommentService commentService;

    private final TaskService taskService;

    /**
     * Prevents the user from trying to update another user's comment.
     */
    @Before(value = "execution(* com.effectivemobile.taskmanagementsystem.service.CommentServiceImpl" +
            ".update(long,..)) && args(id,..)", argNames = "id")
    public void protectAnotherUserCommentBeforeUpdate(long id) {
        User currentUser = userService.getCurrentAppUser();

        if (!currentUser.getRole().equals(Role.ROLE_ADMIN)) {
            CommentDtoResponse comment = commentService.get(id);
            if (!currentUser.getId().equals(comment.getAuthorId())) {
                throw new AttemptingAccessOtherUserEntityException(
                        "Попытка пользователя с id = %d изменения комментария пользователя с id = %d"
                                .formatted(currentUser.getId(), comment.getAuthorId()));
            }
        }
    }

    /**
     * Prevents the user from trying to create a comment on another user's task.
     */
    @Before(value = "execution(* com.effectivemobile.taskmanagementsystem.service.CommentServiceImpl.create(..))")
    public void preventAttemptingCreateCommentOnAnotherUserTask(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        CommentDtoCreateRequest dto = (CommentDtoCreateRequest) args[0];

        User currentUser = userService.getCurrentAppUser();
        if (!currentUser.getRole().equals(Role.ROLE_ADMIN)) {
            TaskDtoResponse task = taskService.get(dto.getTaskId());
            if (!currentUser.getId().equals(dto.getAuthorId())) {
                throw new AttemptingAccessOtherUserEntityException(
                        "Попытка пользователя с id = %d создать комментарий под видом исполнителя с id = %d"
                                .formatted(currentUser.getId(), dto.getAuthorId()));
            } else if (!currentUser.getId().equals(task.getImplementorId())) {
                throw new AttemptingAccessOtherUserEntityException(
                        "Попытка пользователя с id = %d создать комментарий к заданию исполнителя с id = %d"
                                .formatted(currentUser.getId(), task.getImplementorId()));
            }
        }
    }
}
