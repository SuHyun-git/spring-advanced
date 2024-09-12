package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
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

import javax.management.ReflectionException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    AuthService authService;

    @Nested
    class signup {
        @Test
        void 이미_존재하는_이메일_에러() {
            // given
            SignupRequest signupRequest = new SignupRequest("a@a.com", "password", "USER");
//            User user = new User("a@a.com", "password", UserRole.USER);
//            ReflectionTestUtils.setField(user, "id", 1L);

            given(userRepository.existsByEmail(anyString())).willReturn(true);

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> authService.signup(signupRequest));

            // then
            assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
        }

        @Test
        void 회원가입_성공() {
            // given
            SignupRequest signupRequest = new SignupRequest("a@a.com", "password", "USER");

            given(userRepository.existsByEmail(anyString())).willReturn(false);

            String encodedPassword = "encodedPassword";
            given(passwordEncoder.encode(any())).willReturn(encodedPassword);

            User saveUser = new User(signupRequest.getEmail(), encodedPassword, UserRole.USER);
            given(userRepository.save(any())).willReturn(saveUser);
            ReflectionTestUtils.setField(saveUser, "id", 1L);

            String bearerToken = "token";;
            given(jwtUtil.createToken(any(), any(), any())).willReturn(bearerToken);

            // when
            SignupResponse signup = authService.signup(signupRequest);

            // then
            assertEquals("token", signup.getBearerToken());
        }
    }

    @Nested
    class signin {
        @Test
        void 가입되지_않은_유저() {
            // given
            SigninRequest signinRequest = new SigninRequest("email", "password");
            given(userRepository.findByEmail(signinRequest.getEmail())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> authService.signin(signinRequest));

            // then
            assertEquals("가입되지 않은 유저입니다.", exception.getMessage());

        }

        @Test
        void 잘못된_비밀번호() {
            // given
            SigninRequest signinRequest = new SigninRequest("email", "password");

            User user = new User(signinRequest.getEmail(), signinRequest.getPassword(), UserRole.USER);
            given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

            given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

            // when
            AuthException exception = assertThrows(AuthException.class, () -> authService.signin(signinRequest));

            // then
            assertEquals("잘못된 비밀번호입니다.", exception.getMessage());

        }

        @Test
        void 로그인_성공() {
            // given
            SigninRequest signinRequest = new SigninRequest("email", "password");

            User user = new User(signinRequest.getEmail(), signinRequest.getPassword(), UserRole.USER);
            given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

            given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

            String bearerToken = "bearerToken";
            given(jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole())).willReturn(bearerToken);

            // when
            SigninResponse signin = authService.signin(signinRequest);

            // then
            assertEquals(bearerToken, signin.getBearerToken());

        }
    }

}