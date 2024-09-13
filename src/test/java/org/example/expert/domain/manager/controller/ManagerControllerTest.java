package org.example.expert.domain.manager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.service.ManagerService;
import org.example.expert.domain.todo.controller.TodoController;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManagerController.class)
class ManagerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ManagerService managerService;

    @MockBean
    private AuthUserArgumentResolver authUserArgumentResolver;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ManagerController(managerService, jwtUtil)).setCustomArgumentResolvers(authUserArgumentResolver).build();
    }

    @Test
    void saveManager() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        given(authUserArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(authUser);

        long todoId = 1L;
        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(2L);
        ManagerSaveResponse managerSaveResponse = new ManagerSaveResponse(1L, new UserResponse(1L, "a@a.com"));

        given(managerService.saveManager(any(), anyLong(), any(ManagerSaveRequest.class))).willReturn(managerSaveResponse);

        // when
        ResultActions resultActions = mvc.perform(post("/todos/{todoId}/managers", todoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(managerSaveRequest)));

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void getMembers() throws Exception {
        // given
        long todoId = 1L;
        given(managerService.getManagers(anyLong())).willReturn(List.of());

        // when
        ResultActions resultActions = mvc.perform(get("/todos/{todoId}/managers", todoId));

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void deleteManager() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        given(authUserArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(authUser);

        long todoId = 1L;
        long managerId = 2L;

        doNothing().when(managerService).deleteManager(anyLong(), anyLong(), anyLong());

        // when
        ResultActions resultActions = mvc.perform(delete("/todos/{todoId}/managers/{managerId}", todoId, managerId));

        // then
        resultActions.andExpect(status().isOk());
        verify(managerService, times(1)).deleteManager(anyLong(), anyLong(), anyLong());
    }
}