package org.example.expert.domain.todo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.controller.UserController;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TodoService todoService;

    @MockBean
    private AuthUserArgumentResolver authUserArgumentResolver;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new TodoController(todoService)).setCustomArgumentResolvers(authUserArgumentResolver).build();
    }

    @Test
    void saveTodo() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        given(authUserArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(authUser);

        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("title", "contents");
        TodoSaveResponse todoSaveResponse = new TodoSaveResponse(1L, "title", "contents", "icy", new UserResponse(1L, "a@a.com"));

        given(todoService.saveTodo(any(), any(TodoSaveRequest.class))).willReturn(todoSaveResponse);

        // when
        ResultActions resultActions = mvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoSaveRequest)));
        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.contents").value("contents"));
    }

    @Test
    void getTodos() throws Exception {
        // given
        int page = 1;
        int size = 10;

        Pageable pageable = PageRequest.of(page - 1, size);
        TodoResponse todoResponse = new TodoResponse(1L, "title", "contents", "icy", new UserResponse(1L, "a@a.com"), null, null);
        Page<TodoResponse> todoResponses = new PageImpl<>(List.of(todoResponse),pageable, 10);

        given(todoService.getTodos(anyInt(), anyInt())).willReturn(todoResponses);

        // when
        ResultActions resultActions = mvc.perform(get("/todos", page, size));

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void getTodo() throws Exception {
        // given
        long todoId = 1L;
        TodoResponse todoResponse = new TodoResponse(1L, "title", "contents", "icy", new UserResponse(1L, "a@a.com"), null, null);

        given(todoService.getTodo(anyLong())).willReturn(todoResponse);

        // when
        ResultActions resultActions = mvc.perform(get("/todos/{todoId}", todoId));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("title"));
    }
}