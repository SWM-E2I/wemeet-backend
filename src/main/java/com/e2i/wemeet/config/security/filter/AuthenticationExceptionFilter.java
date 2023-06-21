package com.e2i.wemeet.config.security.filter;

import com.e2i.wemeet.exception.CustomException;
import com.e2i.wemeet.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
* Spring Security - Authentication
* FilterChainProxy - LogoutFilter 이후 발생한 예외를 처리한다
* */
@Slf4j
@RequiredArgsConstructor
public class AuthenticationExceptionFilter extends OncePerRequestFilter {
    private final String AUTH_LOG_FORMAT = "Auth Error Class : {}, Error Code : {}, Message : {}";

    private final ObjectMapper objectMapper;
    private final MessageSourceAccessor messageSourceAccessor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            final int code = e.getErrorCode().getCode();
            final String message = messageSourceAccessor.getMessage(e.getMessage());

            log.info(AUTH_LOG_FORMAT, e.getClass().getSimpleName(), code, message);

            response.setStatus(401);
            response.setCharacterEncoding("utf-8");
            response.getWriter()
                    .println(objectMapper.writeValueAsString(
                            new ErrorResponse(code, message)));
        }
    }
}
