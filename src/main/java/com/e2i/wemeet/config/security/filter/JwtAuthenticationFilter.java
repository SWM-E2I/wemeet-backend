package com.e2i.wemeet.config.security.filter;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.config.security.token.JwtEnv;
import com.e2i.wemeet.config.security.token.Payload;
import com.e2i.wemeet.config.security.token.handler.AccessTokenHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/*
 * Spring Security - Authentication Filter
 * FilterChainProxy - ExceptionTranslationFilter 이전에 실행
 * AccessToken 의 유효성을 검증하고 인증 객체 (authenticated == true) 저장
 * */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AccessTokenHandler accessTokenHandler;

    /*
    * AccessToken 이 없을 경우, 다음 필터로 요청을 넘김
    * SecurityConfig 에서 정의한 일부 url 제외, 모든 요청은 인증 객체가 authenticated 인 상태여야합니다.
    * 따라서 AccessToken 이 없을 경우 AuthorizationManager 에 의해 403 예외를 반환합니다.
    * */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader(JwtEnv.ACCESS.getKey());
        Payload payload = accessTokenHandler.extractToken(accessToken);

        if (payload != null) {
            setAuthentication(payload);
        }

        filterChain.doFilter(request, response);
    }

    // Authentication 객체 저장
    private void setAuthentication(@NotNull final Payload payload) {
        MemberPrincipal principal = new MemberPrincipal(payload);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
