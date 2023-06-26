package com.e2i.wemeet.config.security.filter;

import static org.springframework.http.HttpMethod.POST;

import com.e2i.wemeet.config.security.token.JwtEnv;
import com.e2i.wemeet.config.security.token.Payload;
import com.e2i.wemeet.config.security.token.TokenInjector;
import com.e2i.wemeet.config.security.token.handler.RefreshTokenHandler;
import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.token.RefreshTokenMismatchException;
import com.e2i.wemeet.exception.token.TokenNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/*
 * Spring Security - Authentication Filter
 * FilterChainProxy - LoginProcessingFilter 이후 실행
 * AccessToken 의 유효성을 검증하고 인증 객체 (authenticated == true) 생성
 * */
@Slf4j
public class RefreshTokenProcessingFilter extends OncePerRequestFilter {
    private static final String REFRESH_REQUEST_URL = "/v1/auth/refresh";

    private final RequestMatcher filterRequestMatcher;
    private final RedisTemplate<String, String> redisTemplate;
    private final RefreshTokenHandler refreshTokenHandler;
    private final TokenInjector tokenInjector;
    private final ObjectMapper objectMapper;

    public RefreshTokenProcessingFilter(RedisTemplate<String, String> redisTemplate, RefreshTokenHandler refreshTokenHandler, TokenInjector tokenInjector,  ObjectMapper objectMapper) {
        this.filterRequestMatcher = new AntPathRequestMatcher(REFRESH_REQUEST_URL, POST.name());
        this.redisTemplate = redisTemplate;
        this.refreshTokenHandler = refreshTokenHandler;
        this.tokenInjector = tokenInjector;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        if (filterRequestMatcher.matches(request)) {
            reIssueToken(request, response);
        }

        filterChain.doFilter(request, response);
    }

    /* Refresh Token 재발급 로직 수행
    * - Request Body 에서 memberId, role 정보를 가져옴
    * - Cookie 에 담긴 RefreshToken 의 유효성을 검증함
    * -> 유효성 검증 이후 AccessToken, RefreshToken 재발급
    * */
    private void reIssueToken(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        Payload payload = getPayload(request);

        validateRefreshToken(request, payload);
        tokenInjector.injectToken(response, payload);

        log.info("RefreshToken has reIssued - memberId : {}", payload.getMemberId());
    }

    private Payload getPayload(HttpServletRequest request) throws IOException {
        return objectMapper.readValue(request.getInputStream(), Payload.class);
    }

    private void validateRefreshToken(HttpServletRequest request, Payload payload) {
        String refreshToken = getRefreshTokenFromCookie(request);

        // RefreshToken  유효성 검증
        refreshTokenHandler.decodeToken(refreshToken);

        // Redis 에 저장된 RefreshToken 값과 일치하는지 검증
        if (!matchesRefreshTokenInRedis(payload, refreshToken)) {
            throw new RefreshTokenMismatchException();
        }
    }

    // Redis 에서 발급했던 SMS 인증 번호를 가져옴
    private boolean matchesRefreshTokenInRedis(Payload payload, String refreshToken) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        // get Key from payload (Ex) "memberId-1-USER"
        String redisKey = JwtEnv.getRedisKeyForRefresh(payload);
        String savedRefresh = operations.get(redisKey);
        return refreshToken.equals(savedRefresh);
    }

    // Cookie 에서 Refresh Token 을 가져옴
    private static String getRefreshTokenFromCookie(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
            .filter(cookie -> cookie.getName().equals(JwtEnv.REFRESH.getKey()))
            .map(Cookie::getValue)
            .findFirst()
            .orElseThrow(() -> new TokenNotFoundException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
    }
}
