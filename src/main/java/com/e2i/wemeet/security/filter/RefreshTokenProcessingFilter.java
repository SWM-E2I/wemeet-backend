package com.e2i.wemeet.security.filter;

import static org.springframework.http.HttpMethod.POST;

import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.exception.token.RefreshTokenMismatchException;
import com.e2i.wemeet.exception.token.RefreshTokenNotExistException;
import com.e2i.wemeet.security.token.JwtEnv;
import com.e2i.wemeet.security.token.Payload;
import com.e2i.wemeet.security.token.TokenInjector;
import com.e2i.wemeet.security.token.handler.AccessTokenHandler;
import com.e2i.wemeet.security.token.handler.RefreshTokenHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
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
    private final AccessTokenHandler accessTokenHandler;
    private final MemberRepository memberRepository;

    public RefreshTokenProcessingFilter(RedisTemplate<String, String> redisTemplate,
        RefreshTokenHandler refreshTokenHandler,
        TokenInjector tokenInjector, ObjectMapper objectMapper,
        AccessTokenHandler accessTokenHandler,
        MemberRepository memberRepository) {
        this.filterRequestMatcher = new AntPathRequestMatcher(REFRESH_REQUEST_URL, POST.name());
        this.redisTemplate = redisTemplate;
        this.refreshTokenHandler = refreshTokenHandler;
        this.tokenInjector = tokenInjector;
        this.objectMapper = objectMapper;
        this.accessTokenHandler = accessTokenHandler;
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        if (filterRequestMatcher.matches(request)) {
            reIssueToken(request, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    /* Refresh Token 재발급 로직 수행
     * - Request Body 에서 memberId, role 정보를 가져옴
     * - Cookie 에 담긴 RefreshToken 의 유효성을 검증함
     * -> 유효성 검증 이후 AccessToken, RefreshToken 재발급
     * */
    private void reIssueToken(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        Payload payload = getPayload(request);
        Role role = memberRepository.findRoleByMemberId(payload.getMemberId())
            .orElseThrow(MemberNotFoundException::new);

        validateRefreshToken(request, payload);
        Payload newPayloadForToken = new Payload(payload.getMemberId(), role.name());

        tokenInjector.injectToken(response, newPayloadForToken);
        writeResponse(response, newPayloadForToken);
    }

    private Payload getPayload(HttpServletRequest request) {
        String accessToken = request.getHeader(JwtEnv.ACCESS.getKey());
        return accessTokenHandler.extractTokenWithNoVerify(accessToken);
    }

    private void validateRefreshToken(HttpServletRequest request, Payload payload) {
        String refreshToken = request.getHeader(JwtEnv.REFRESH.getKey());

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

        // get Key from payload (Ex) "memberId-1"
        String redisKey = JwtEnv.getRedisKeyForRefresh(payload);
        String savedRefresh = operations.get(redisKey);

        if (!StringUtils.hasText(savedRefresh)) {
            throw new RefreshTokenNotExistException();
        }

        boolean tokenEquals = refreshToken.equals(savedRefresh);
        if (tokenEquals) {
            // Redis 에서 RefreshToken 삭제
            redisTemplate.delete(redisKey);
        }

        return tokenEquals;
    }

    private void writeResponse(HttpServletResponse response, Payload payload) throws IOException {
        log.info("RefreshToken has reIssued - memberId : {}", payload.getMemberId());

        ResponseDto<Void> result = ResponseDto.success("RefreshToken 을 재발급하는데 성공했습니다.");

        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getOutputStream().write(objectMapper.writeValueAsString(result).getBytes());
    }
}
