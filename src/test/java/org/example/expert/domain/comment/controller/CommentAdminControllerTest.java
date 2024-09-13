package org.example.expert.domain.comment.controller;

import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.service.CommentAdminService;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentAdminController.class)
class CommentAdminControllerTest {
    
    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private CommentAdminService commentAdminService;


    @Test
    void 삭제_성공() throws Exception {
        // given
        long commentId = 1L;

        doNothing().when(commentAdminService).deleteComment(commentId);
        
        // when
        mvc.perform(delete("/admin/comments/{commentId}", commentId));

        //then
        verify(commentAdminService, times(1)).deleteComment(anyLong());
    }
}