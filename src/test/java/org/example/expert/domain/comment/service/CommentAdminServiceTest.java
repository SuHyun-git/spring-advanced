package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.repository.CommentRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentAdminServiceTest {

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentAdminService commentAdminService;

    @Nested
    class deleteComment {
        @Test
        void comment_삭제() {
            // given
            long commentId = 1L;

            doNothing().when(commentRepository).deleteById(anyLong());

            // when
            commentAdminService.deleteComment(commentId);

            // then
            verify(commentRepository, times(1)).deleteById(anyLong());
        }
    }
}