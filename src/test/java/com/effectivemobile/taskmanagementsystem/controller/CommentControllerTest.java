package com.effectivemobile.taskmanagementsystem.controller;

import com.effectivemobile.taskmanagementsystem.dto.request.comment.CommentDtoCreateRequest;
import com.effectivemobile.taskmanagementsystem.dto.response.CommentDtoResponse;
import com.effectivemobile.taskmanagementsystem.security.filter.JwtAuthenticationFilter;
import com.effectivemobile.taskmanagementsystem.service.CommentService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CommentController.class},
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class))
@DisplayName("Контроллер для работы с комментами")
class CommentControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CommentService commentService;

    private CommentDtoResponse response;

    @BeforeEach
    void init() {
        response = CommentDtoResponse.builder()
                .id(1L)
                .text("some text")
                .authorId(2L)
                .taskId(1L)
                .build();
    }

    @Test
    void create() throws Exception {
        CommentDtoCreateRequest request = CommentDtoCreateRequest.builder()
                .text(response.getText())
                .authorId(response.getAuthorId())
                .taskId(response.getTaskId())
                .build();
        when(commentService.create(request)).thenReturn(response);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void get() throws Exception {
        long requestId = response.getId();
        when(commentService.get(requestId)).thenReturn(response);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/comment/{id}", requestId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void getAllByTaskId() throws Exception {
        long requestId = response.getTaskId();
        when(commentService.getAllByTask(requestId)).thenReturn(List.of(response));

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/comment/task/{taskId}", requestId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(response))));
    }

    @Test
    void update() throws Exception {
        long requestId = response.getId();
        String requestText = response.getText();
        when(commentService.update(requestId, requestText)).thenReturn(response);

        mvc.perform(MockMvcRequestBuilders.put("/api/v1/comment")
                        .param("id", String.valueOf(requestId))
                        .param("comment", requestText)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void delete() throws Exception {
        long requestId = response.getId();
        doNothing().when(commentService).deleteById(requestId);

        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/comment/{id}", response.getTaskId()))
                .andExpect(status().isOk());
    }
}