package com.effectivemobile.taskmanagementsystem.aop;

import com.effectivemobile.taskmanagementsystem.dto.response.TaskDtoResponse;
import com.effectivemobile.taskmanagementsystem.exception.AttemptingAccessOtherUserEntityException;
import com.effectivemobile.taskmanagementsystem.model.User;
import com.effectivemobile.taskmanagementsystem.service.TaskService;
import com.effectivemobile.taskmanagementsystem.service.UserService;
import com.effectivemobile.taskmanagementsystem.util.Role;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class TaskAspect {
    private final UserService userService;

    private final TaskService taskService;

    /**
     * Prevents the user from attempting to receive another user's task.
     */
    @AfterReturning(
            pointcut = "execution(* com.effectivemobile.taskmanagementsystem.service.TaskServiceImpl.get(..))",
            returning = "task")
    public void checkTaskImplementorAfterReturn(TaskDtoResponse task) {
        User currentUser = userService.getCurrentAppUser();
        if (!currentUser.getRole().equals(Role.ROLE_ADMIN) && !currentUser.getId().equals(task.getImplementorId())) {
            throw new AttemptingAccessOtherUserEntityException(
                    "Попытка пользователя с id = %d доступа к заданию исполнителя с id = %d"
                            .formatted(currentUser.getId(), task.getImplementorId()));
        }
    }

    /**
     * Prevents the user from trying to update another user's task.
     */
    @Before(value = "execution(* com.effectivemobile.taskmanagementsystem.service.TaskServiceImpl" +
            ".update(long,..)) && args(id,..)", argNames = "id")
    public void protectAnotherUserTaskBeforeUpdate(long id) {
        User currentUser = userService.getCurrentAppUser();
        if (!currentUser.getRole().equals(Role.ROLE_ADMIN)) {
            TaskDtoResponse task = taskService.get(id);
            if (!currentUser.getId().equals(task.getImplementorId())) {
                throw new AttemptingAccessOtherUserEntityException(
                        "Попытка пользователя с id = %d изменения статуса задания исполнителя с id = %d"
                                .formatted(currentUser.getId(), task.getImplementorId()));
            }
        }
    }

    /**
     * Adds logic that excludes the possibility of the user receiving other users tasks.
     */
    @Around(value = "execution(* com.effectivemobile.taskmanagementsystem.service.TaskServiceImpl.getAll(..))")
    public Object ensureImplementorIdCriteriaIsDeterminedBeforeGetAll(ProceedingJoinPoint joinPoint) throws Throwable {
        User currentUser = userService.getCurrentAppUser();

        if (!currentUser.getRole().equals(Role.ROLE_ADMIN)) {
            Object[] args = joinPoint.getArgs();
            Long implementorId = (Long) args[0];
            if (implementorId == null) {
                args[0] = currentUser.getId();

                return joinPoint.proceed(args);
            } else if (!implementorId.equals(currentUser.getId())) {
                throw new AttemptingAccessOtherUserEntityException(
                        "Попытка пользователя с id = %d доступа к заданиям исполнителя с id = %d"
                                .formatted(currentUser.getId(), implementorId));
            }
        }

        return joinPoint.proceed();
    }
}
