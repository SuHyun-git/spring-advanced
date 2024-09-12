package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    TodoRepository todoRepository;

    @Mock
    WeatherClient weatherClient;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    TodoService todoService;


    @Nested
    class saveTodo {
        @Test
        void 저장_성공() {
            //given
            long userId = 2L;
            AuthUser authUser = new AuthUser(userId, "email", UserRole.USER);

            TodoSaveRequest todoSaveRequest = new TodoSaveRequest("title", "contents");

            User user = User.fromAuthUser(authUser);

            given(weatherClient.getTodayWeather()).willReturn("icy");

            Todo saveTodo = new Todo("title", "contents", "icy", user);
            ReflectionTestUtils.setField(saveTodo, "id", 1L);
            given(todoRepository.save(any(Todo.class))).willReturn(saveTodo);

            //when
            TodoSaveResponse todoSaveResponse = todoService.saveTodo(authUser, todoSaveRequest);

            //then
            assertNotNull(saveTodo);
            assertEquals(1L, todoSaveResponse.getId());
            assertEquals(2L, todoSaveResponse.getUser().getId());
        }

    }

    @Nested
    class getTodos {
        @Test
        void 모든_todo_조회_성공() {
            // given
            int page = 1;
            int size = 10;

            Pageable pageable = PageRequest.of(page - 1, size);

            Todo todo = new Todo("title", "content", "icy", new User());

            Page<Todo> todos = new PageImpl<>(List.of(todo), pageable, 1);
            given(todoRepository.findAllByOrderByModifiedAtDesc(pageable)).willReturn(todos);

            // when
            Page<TodoResponse> todoList = todoService.getTodos(page, size);

            // then
            assertEquals(todos.getTotalElements(), todoList.getTotalElements());
        }


    }

    @Nested
    class getTodo {
        @Test
        void 일정_조회_성공() {
            //given
            long todoId = 1L;
            long userId = 2L;
            User user = new User("email", "1234", UserRole.USER);
            ReflectionTestUtils.setField(user, "id", userId);

            Todo todo = new Todo("title", "contents", "icy", user);
            ReflectionTestUtils.setField(todo, "id", todoId);

            given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.of(todo));

            //when
            TodoResponse todoResponse = todoService.getTodo(todoId);

            //then
            assertNotNull(todoResponse);
            assertEquals(1, todoResponse.getId());
        }

        @Test
        void 일정_조회_실패() {
            //given
            long todoId = 1L;
            long userId = 2L;
            User user = new User("email", "1234", UserRole.USER);
            ReflectionTestUtils.setField(user, "id", userId);

            Todo todo = new Todo("title", "contents", "icy", user);
            ReflectionTestUtils.setField(todo, "id", todoId);

            given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.empty());

            //when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> todoService.getTodo(todoId));

            //then
            assertEquals("Todo not found", exception.getMessage());
        }
    }
}