package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.ServerException;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private CommentService commentService;

    @Nested
    class saveComment {
        @Test
        public void comment_등록_중_할일을_찾지_못해_에러가_발생한다() {
            // given
            long todoId = 1;
            CommentSaveRequest request = new CommentSaveRequest("contents");
            AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);

            given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
                commentService.saveComment(authUser, todoId, request);
            });

            // then
            assertEquals("Todo not found", exception.getMessage());
        }

        @Test
        public void comment를_정상적으로_등록한다() {
            // given
            AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
            long todoId = 1;
            CommentSaveRequest request = new CommentSaveRequest("contents");
            
            User user = User.fromAuthUser(authUser);
            Todo findTodo = new Todo("title", "title", "contents", user);
            Comment savedComment = new Comment(request.getContents(), user, findTodo);
            ReflectionTestUtils.setField(savedComment, "id", 1L);

            given(todoRepository.findById(anyLong())).willReturn(Optional.of(findTodo));
            given(commentRepository.save(any())).willReturn(savedComment);

            // when
            CommentSaveResponse result = commentService.saveComment(authUser, todoId, request);

            // then
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("contents", request.getContents());
        }
    }

    @Nested
    class getComments {

        @Test
        void comments_조회() {
            // given
            long userId = 1L;
            User user = new User("email", "1234", UserRole.USER);
            ReflectionTestUtils.setField(user, "id", userId);

            long todoId = 1L;
            Todo todo = new Todo("title", "contents", "icy", user);
            ReflectionTestUtils.setField(todo, "id", todoId);

            long commentId = 1L;
            Comment comment = new Comment("contents", user, todo);
            ReflectionTestUtils.setField(comment, "id", commentId);

            List<Comment> commentList = new ArrayList<>();
            commentList.add(comment);

            given(commentRepository.findByTodoIdWithUser(anyLong())).willReturn(commentList);

            // when
            List<CommentResponse> comments = commentService.getComments(todoId);

            // then
            assertNotNull(comments);
            assertEquals(1L, comments.get(0).getId());
            assertEquals("contents", comments.get(0).getContents());
            assertEquals(1L, comments.get(0).getUser().getId());
            assertEquals("email", comments.get(0).getUser().getEmail());
        }
    }
}
