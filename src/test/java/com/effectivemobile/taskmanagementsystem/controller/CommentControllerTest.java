package com.effectivemobile.taskmanagementsystem.controller;

import com.effectivemobile.taskmanagementsystem.dto.CommentDto;
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

    private CommentDto comment;

    @BeforeEach
    void init() {
        comment = CommentDto.builder()
                .id(1L)
                .text("some text")
                .authorId(2L)
                .taskId(1L)
                .build();
    }

    @Test
    void create() throws Exception {
        when(commentService.create(comment)).thenReturn(comment);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(comment)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(comment)));
    }

    @Test
    void get() throws Exception {
        when(commentService.get(comment.getId())).thenReturn(comment);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/comment/{id}", comment.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(comment)));
    }

    @Test
    void getAllByTaskId() throws Exception {
        when(commentService.getAllByTask(comment.getTaskId())).thenReturn(List.of(comment));

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/comment/task/{taskId}", comment.getTaskId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(comment))));
    }

    @Test
    void update() throws Exception {
        when(commentService.update(comment.getId(), comment.getText())).thenReturn(comment);

        mvc.perform(MockMvcRequestBuilders.put("/api/v1/comment")
                        .param("id", String.valueOf(comment.getId()))
                        .param("comment", comment.getText())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(comment)));
    }

    @Test
    void delete() throws Exception {
        doNothing().when(commentService).deleteById(comment.getId());

        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/comment/{id}", comment.getTaskId()))
                .andExpect(status().isOk());
    }
}