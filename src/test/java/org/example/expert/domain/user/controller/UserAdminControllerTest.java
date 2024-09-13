package org.example.expert.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.service.UserAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAdminController.class)
class UserAdminControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserAdminService userAdminService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 권한_변경() throws Exception {
        long userId = 1L;
        UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest("USER");

        doNothing().when(userAdminService).changeUserRole(anyLong(), any(UserRoleChangeRequest.class));

        // when
        mvc.perform(patch("/admin/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRoleChangeRequest)))
                .andExpect(status().isOk());

        // then
        verify(userAdminService, times(1)).changeUserRole(anyLong(), any(UserRoleChangeRequest.class));

    }
}