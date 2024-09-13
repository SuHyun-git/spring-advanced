package org.example.expert.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.controller.UserController;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private AuthUserArgumentResolver authUserArgumentResolver;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new CommentController(commentService)).setCustomArgumentResolvers(authUserArgumentResolver).build();
    }

    @Nested
    class saveComment {
        @Test
        void 댓글_저장() throws Exception {
            // given
            long todoId = 1L;
            CommentSaveRequest commentSaveRequest = new CommentSaveRequest("contents");
            CommentSaveResponse commentSaveResponse = new CommentSaveResponse(1L, "contents", new UserResponse(1L, "email"));

            AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
            given(authUserArgumentResolver.supportsParameter(any())).willReturn(true);
            given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(authUser);

            given(commentService.saveComment(any(), anyLong(), any(CommentSaveRequest.class))).willReturn(commentSaveResponse);

            // when
            ResultActions resultActions = mvc.perform(post("/todos/{todoId}/comments", todoId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(commentSaveRequest)));
            // then
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.contents").value("contents"))
                    .andExpect(jsonPath("$.user.id").value(1L))
                    .andExpect(jsonPath("$.user.email").value("email"));
        }
    }



    @Nested
    class getComments {
        @Test
        void 댓글_조회() throws Exception {
            // given
            long todoId = 1L;
            given(commentService.getComments(anyLong())).willReturn(List.of());

            // when
            ResultActions resultActions = mvc.perform(get("/todos/{todoId}/comments", todoId));

            // then
            resultActions.andExpect(status().isOk());
        }
    }
}