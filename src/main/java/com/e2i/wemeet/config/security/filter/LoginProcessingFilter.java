package com.e2i.wemeet.config.security.filter;

import static org.springframework.http.HttpMethod.POST;

import com.e2i.wemeet.dto.request.LoginRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/*
 * Spring Security - Authentication Filter
 * FilterChainProxy - AuthenticationExceptionFilter 이후에 실행
 * 로그인 요청을 실행 -> 유효한 요청일 경우, AccessToken, RefreshToken 반환
 * */
public class LoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private static final String LOGIN_REQUEST_URL = "/v1/auth/phone/validate";

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;
    private final AuthenticationSuccessHandler successHandler;

    public LoginProcessingFilter(AuthenticationManager authenticationManager,
        AuthenticationSuccessHandler successHandler, ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher(LOGIN_REQUEST_URL, POST.name()), authenticationManager);
        this.authenticationManager = authenticationManager;
        this.successHandler = successHandler;
        this.objectMapper = objectMapper;
    }

    /*
     * 로그인 요청 검증 (SMS 인증 번호가 일치하는지)
     * 인증에 성공하였다면 successfulAuthentication 실행
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        LoginRequestDto loginRequest = objectMapper.readValue(request.getInputStream(),
            LoginRequestDto.class);
        loginRequest.validateDataFormat();

        // authenticated == false
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(loginRequest, loginRequest.credential());

        return authenticationManager.authenticate(authentication);
    }

    /* 인증 성공 후 처리
     * - JWT 발급
     * - SecurityContext 에 인증 객체 저장
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }
}
