package com.effectivemobile.taskmanagementsystem.aop;

import com.effectivemobile.taskmanagementsystem.dto.CommentDto;
import com.effectivemobile.taskmanagementsystem.dto.TaskDto;
import com.effectivemobile.taskmanagementsystem.exception.AttemptingAccessOtherUserEntityException;
import com.effectivemobile.taskmanagementsystem.model.AppUser;
import com.effectivemobile.taskmanagementsystem.service.CommentService;
import com.effectivemobile.taskmanagementsystem.service.TaskService;
import com.effectivemobile.taskmanagementsystem.service.UserService;
import com.effectivemobile.taskmanagementsystem.util.AppRole;
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
        AppUser currentUser = userService.getCurrentAppUser();

        if (!currentUser.getRole().equals(AppRole.ROLE_ADMIN)) {
            CommentDto comment = commentService.get(id);
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
        CommentDto dto = (CommentDto) args[0];

        AppUser currentUser = userService.getCurrentAppUser();
        if (!currentUser.getRole().equals(AppRole.ROLE_ADMIN)) {
            TaskDto task = taskService.get(dto.getTaskId());
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
