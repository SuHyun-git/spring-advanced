package org.example.expert.domain.user.service;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserAdminService userAdminService;

    @Nested
    class changeUserRole {

        @Test
        void 권한_변경_성공() {
            // givn
            long userId = 1L;
            UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest("ADMIN");

            User findUser = new User("email", "password", UserRole.USER);

            given(userRepository.findById(userId)).willReturn(Optional.of(findUser));
            
            // when
            userAdminService.changeUserRole(userId, userRoleChangeRequest);

            // then
            assertEquals(UserRole.ADMIN, findUser.getUserRole());
        }

        @Test
        void 권한_변경_실패() {
            // givn
            long userId = 1L;
            UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest("ADMIN");

            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userAdminService.changeUserRole(userId, userRoleChangeRequest));

            // then
            assertEquals("User not found", exception.getMessage());
        }
    }

}