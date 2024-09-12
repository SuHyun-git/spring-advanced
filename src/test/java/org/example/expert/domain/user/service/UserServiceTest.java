package org.example.expert.domain.user.service;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Nested
    class getUser {
        @Test
        void userId를_찾지_못함() {
            // given
            long userId = 1L;
            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.getUser(userId));

            //then
            assertEquals("User not found", exception.getMessage());

        }

        @Test
        void user_찾기_성공() {
            // given
            long userId = 1L;

            User user = new User("email", "password", UserRole.USER);
            ReflectionTestUtils.setField(user, "id", userId);

            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            // when
            UserResponse userResponse = userService.getUser(userId);

            //then
            assertNotNull(userResponse);
            assertEquals(1L, userResponse.getId());
            assertEquals("email", userResponse.getEmail());
        }
    }


    @Nested
    class changePassword {

        @Test
        void 비밀번호_형식_오류_8자이하() {
            // given
            long userId = 1L;
            UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("oldPassword", "new");

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.changePassword(userId, userChangePasswordRequest));

            //then
            assertEquals("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", exception.getMessage());
        }

        @Test
        void 비밀번호_형식_오류_숫자_없음() {
            // given
            long userId = 1L;
            UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("oldPassword", "newPassword");

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.changePassword(userId, userChangePasswordRequest));

            //then
            assertEquals("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", exception.getMessage());
        }

        @Test
        void 비밀번호_형식_오류_대문자_없음() {
            // given
            long userId = 1L;
            UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("oldPassword", "8newpassword");

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.changePassword(userId, userChangePasswordRequest));

            //then
            assertEquals("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", exception.getMessage());
        }

        @Test
        void userId_찾지_못함() {
            // given
            long userId = 1L;
            UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("oldPassword", "Aa123456");

            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.changePassword(userId, userChangePasswordRequest));

            //then
            assertEquals("User not found", exception.getMessage());
        }



        @Test
        void 새로운_비밀번호와_기본_비밀번호가_같은_오류() {
            // given
            long userId = 1L;
            UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("oldPassword", "Aa123456");

            User user = new User("email", "password", UserRole.USER);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.changePassword(userId, userChangePasswordRequest));

            //then
            assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
        }

        @Test
        void 잘못된_비밀번호_오류() {
            // given
            long userId = 1L;
            UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("oldPassword", "Aa123456");

            User user = new User("email", "password", UserRole.USER);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.changePassword(userId, userChangePasswordRequest));

            //then
            assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
        }

        @Test
        void 비밀번호_변경_성공() {
            // given
            long userId = 1L;
            UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("oldPassword", "Aa123456");

            User user = new User("email", "password", UserRole.USER);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            given(passwordEncoder.matches(anyString(), anyString())).willReturn(false, true);

            given(passwordEncoder.encode(anyString())).willReturn("Aa123456");

            // when
            userService.changePassword(userId, userChangePasswordRequest);

            //then
            verify(passwordEncoder, times(1)).encode(anyString());
        }
    }
}