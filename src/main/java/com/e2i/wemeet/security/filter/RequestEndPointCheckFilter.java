package com.e2i.wemeet.security.filter;

import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.badrequest.InvalidHttpRequestException;
import com.e2i.wemeet.security.handler.HttpRequestEndPointChecker;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class RequestEndPointCheckFilter extends OncePerRequestFilter {

    private final HttpRequestEndPointChecker httpRequestEndPointChecker;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        if (!httpRequestEndPointChecker.isEndPointExist(request)) {
            throw new InvalidHttpRequestException(ErrorCode.INVALID_HTTP_REQUEST);
        }

        filterChain.doFilter(request, response);
    }
}
