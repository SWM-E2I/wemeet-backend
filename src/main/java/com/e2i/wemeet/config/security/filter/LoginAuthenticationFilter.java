package com.e2i.wemeet.config.security.filter;

import com.e2i.wemeet.dto.request.LoginRequestDto;
import com.e2i.wemeet.exception.badrequest.InvalidHttpRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;

import static org.springframework.http.HttpMethod.POST;

/*
* 로그인 요청을 처리하는 필터
* */
public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String LOGIN_REQUEST_URL = "/v1/auth/phone/validate";
    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;

    public LoginAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
        super(LOGIN_REQUEST_URL, authenticationManager);
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        validateHttpRequestMethod(request);
        LoginRequestDto loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
        loginRequest.validateDataFormat();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginRequest, loginRequest.credential());
        return authenticationManager.authenticate(authentication);
    }

    private void validateHttpRequestMethod(HttpServletRequest request) {
        if (!request.getMethod().equals(POST.name())) {
            throw new InvalidHttpRequestException();
        }
    }
}
