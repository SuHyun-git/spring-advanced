package org.example.expert.domain.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void signup() throws Exception {
        // given
        SignupRequest signupRequest = new SignupRequest("a@a.com", "password", "USER");
        SignupResponse signupResponse = new SignupResponse("bearerToken");
        given(authService.signup(any(SignupRequest.class))).willReturn(signupResponse);
        
        // when
        ResultActions resultActions = mvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.bearerToken").value("bearerToken"));
    }

    @Test
    void signin() throws Exception{
        // given
        SigninRequest signinRequest = new SigninRequest("a@a.com", "password");
        SigninResponse signupResponse = new SigninResponse("bearerToken");
        given(authService.signin(any(SigninRequest.class))).willReturn(signupResponse);

        // when
        ResultActions resultActions = mvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signinRequest)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.bearerToken").value("bearerToken"));;
    }
}