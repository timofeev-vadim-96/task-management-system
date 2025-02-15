package com.effectivemobile.taskmanagementsystem.controller;

import com.effectivemobile.taskmanagementsystem.dto.request.task.TaskDtoCreateRequest;
import com.effectivemobile.taskmanagementsystem.dto.request.task.TaskDtoUpdateRequest;
import com.effectivemobile.taskmanagementsystem.dto.response.TaskDtoResponse;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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

    private TaskDtoResponse response;

    @BeforeEach
    void init() {
        response = TaskDtoResponse.builder()
                .id(1L)
                .title("title")
                .description("description")
                .priority(TaskPriority.HIGH)
                .status(TaskStatus.IN_STAY)
                .authorId(1L)
                .implementorId(2L)
                .build();
    }

    @Test
    void get() throws Exception {
        long requestId = response.getId();
        when(taskService.get(requestId)).thenReturn(response);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/task/{id}", requestId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void getAll() throws Exception {
        PageImpl<TaskDtoResponse> tasks = new PageImpl<>(
                List.of(response),
                PageRequest.of(1, 1),
                1L);
        when(taskService.getAll(any(), any(), any(),
                any(), any(Pageable.class))).thenReturn(tasks);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/task")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(tasks)));
    }

    @Test
    void create() throws Exception {
        when(taskService.create(any(TaskDtoCreateRequest.class))).thenReturn(response);
        TaskDtoCreateRequest request = TaskDtoCreateRequest.builder()
                .title(response.getTitle())
                .description(response.getDescription())
                .priority(response.getPriority())
                .status(response.getStatus())
                .authorId(response.getAuthorId())
                .implementorId(response.getImplementorId())
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/task")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void update() throws Exception {
        when(taskService.update(any(TaskDtoUpdateRequest.class))).thenReturn(response);
        TaskDtoUpdateRequest request = TaskDtoUpdateRequest.builder()
                .id(response.getId())
                .title(response.getTitle())
                .description(response.getDescription())
                .priority(response.getPriority())
                .status(response.getStatus())
                .authorId(response.getAuthorId())
                .implementorId(response.getImplementorId())
                .build();

        mvc.perform(MockMvcRequestBuilders.put("/api/v1/task")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void statusUpdate() throws Exception {
        long requestId = response.getId();
        TaskStatus requestStatus = response.getStatus();
        when(taskService.update(anyLong(), any(TaskStatus.class))).thenReturn(response);

        mvc.perform(MockMvcRequestBuilders.put("/api/v1/task/status")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("id", String.valueOf(requestId))
                        .param("status", requestStatus.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void delete() throws Exception {
        long requestId = response.getId();
        doNothing().when(taskService).deleteById(requestId);

        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/task/{id}", requestId))
                .andExpect(status().isOk());
    }
}