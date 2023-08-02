package com.e2i.wemeet.security.filter;

import static com.e2i.wemeet.exception.ErrorCode.UNAUTHORIZED;

import com.e2i.wemeet.dto.response.ResponseStatus;
import com.e2i.wemeet.exception.CustomException;
import com.e2i.wemeet.exception.ErrorResponse;
import com.e2i.wemeet.exception.badrequest.InvalidHttpRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.filter.OncePerRequestFilter;

/*
 * Spring Security - Authentication Filter
 * FilterChainProxy - LogoutFilter 이후 실행
 * 인증 절차에서 발생한 예외를 처리
 * */
@Slf4j
@RequiredArgsConstructor
public class AuthenticationExceptionFilter extends OncePerRequestFilter {

    private static final String AUTH_LOG_FORMAT = "Auth Error Class : {}, Error Code : {}, Message : {}";
    private static final String TOKEN_ERROR_LOG_FORMAT = "Token Error Class : {}, Message : {}";
    private static final String TOKEN_ERROR_MESSAGE = "Token error has been occurred in Spring Security Filter";
    private static final String AUTH_COMMON_ERROR_MESSAGE = "Auth error has been occurred in Spring Security Filter";

    private final ObjectMapper objectMapper;
    private final MessageSourceAccessor messageSourceAccessor;

    /*
     * 인증 절차 수행 중 발생하는 예외를 처리
     * */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (InvalidHttpRequestException e) {
            setErrorResponse(response, e);
        } catch (CustomException e) {
            setErrorResponse(response, e);
        } catch (AccessDeniedException e) {
            setErrorResponse(response, e);
        } catch (Exception e) {
            setErrorResponse(response, e);
        }
    }

    // CustomException 예외 응답
    private void setErrorResponse(HttpServletResponse response, CustomException e)
        throws IOException {
        final int code = e.getErrorCode().getCode();
        final String message = messageSourceAccessor.getMessage(e.getMessage());

        log.info(AUTH_LOG_FORMAT, e.getClass().getSimpleName(), code, message);

        setErrorResponseBody(response, ResponseStatus.FAIL, code, message);
    }

    // AuthorizationManager 에서 발생한 인가 예외 발생 시
    private void setErrorResponse(HttpServletResponse response, AccessDeniedException e)
        throws IOException {
        final int code = UNAUTHORIZED.getCode();
        final String message = messageSourceAccessor.getMessage(UNAUTHORIZED.getMessageKey());

        log.info(AUTH_LOG_FORMAT, e.getClass().getSimpleName(), code, message);

        setErrorResponseBody(response, ResponseStatus.FAIL, code, message);
    }

    // CustomException 으로 정의하지 않은, 예상치 못한 예외 발생시
    private void setErrorResponse(HttpServletResponse response, Exception e) throws IOException {
        log.warn(AUTH_LOG_FORMAT, e.getClass().getSimpleName(), 50000, e.getMessage());

        setErrorResponseBody(response, ResponseStatus.ERROR, 50000, AUTH_COMMON_ERROR_MESSAGE);
    }

    // 유효하지 않은 Http 요청이 들어온 경우 - 404 예외 반환
    private void setErrorResponse(HttpServletResponse response, InvalidHttpRequestException e)
        throws IOException {
        final int code = e.getErrorCode().getCode();
        final String message = messageSourceAccessor.getMessage(e.getMessage());

        log.info(AUTH_LOG_FORMAT, e.getClass().getSimpleName(), code, message);

        setErrorResponseBody(response, ResponseStatus.ERROR, code, message);
        response.setStatus(404);
    }

    /*
     * 예외 응답 반환
     * - 401 Unauthorized
     * - ErrorResponse
     */
    private void setErrorResponseBody(HttpServletResponse response, ResponseStatus status, int code,
        String message)
        throws IOException {
        response.setStatus(401);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.getWriter()
            .println(objectMapper.writeValueAsString(
                new ErrorResponse(status, code, message)));
    }

}
