package com.effectivemobile.taskmanagementsystem.controller;

import com.effectivemobile.taskmanagementsystem.dto.TaskDto;
import com.effectivemobile.taskmanagementsystem.security.filter.JwtAuthenticationFilter;
import com.effectivemobile.taskmanagementsystem.service.TaskService;
import com.effectivemobile.taskmanagementsystem.util.TaskPriority;
import com.effectivemobile.taskmanagementsystem.util.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {TaskController.class},
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class))
@DisplayName("Контроллер для работы с тасками")
class TaskControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TaskService taskService;

    private TaskDto task;

    @BeforeEach
    void init() {
        task = TaskDto.builder()
                .id(1L)
                .title("title")
                .description("description")
                .priority(TaskPriority.ВЫСОКИЙ)
                .status(TaskStatus.В_ОЖИДАНИИ)
                .authorId(1L)
                .implementorId(2L)
                .build();
    }

    @Test
    void get() throws Exception {
        when(taskService.get(task.getId())).thenReturn(task);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/task/{id}", task.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(task)));
    }

    @Test
    void getAll() throws Exception {
        PageImpl<TaskDto> tasks = new PageImpl<>(
                List.of(task),
                PageRequest.of(1, 1),
                1L);
        when(taskService.getAll(any(), any(), any(),
                any(), anyInt(), anyInt())).thenReturn(tasks);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/task")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(tasks)));
    }

    @Test
    void create() throws Exception {
        when(taskService.create(any(TaskDto.class))).thenReturn(task);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/task")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(task)));
    }

    @Test
    void update() throws Exception {
        when(taskService.update(any(TaskDto.class))).thenReturn(task);

        mvc.perform(MockMvcRequestBuilders.put("/api/v1/task")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(task)));
    }

    @Test
    void testUpdate() throws Exception {
        when(taskService.update(anyLong(), any(TaskStatus.class))).thenReturn(task);

        mvc.perform(MockMvcRequestBuilders.put("/api/v1/task/status")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("id", String.valueOf(task.getId()))
                        .param("status", task.getStatus().name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(task)));
    }

    @Test
    void delete() throws Exception {
        doNothing().when(taskService).deleteById(task.getId());

        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/task/{id}", task.getId()))
                .andExpect(status().isOk());
    }
}