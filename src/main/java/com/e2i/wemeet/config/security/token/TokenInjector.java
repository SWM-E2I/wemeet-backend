package com.e2i.wemeet.config.security.token;


import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.config.security.token.handler.AccessTokenHandler;
import com.e2i.wemeet.config.security.token.handler.RefreshTokenHandler;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/*
* JWT 를 생성하고 응답에 넣어주는 역할
* */
@RequiredArgsConstructor
@Component
public class TokenInjector {

    private final AccessTokenHandler accessTokenHandler;
    private final RefreshTokenHandler refreshTokenHandler;
    private final RedisTemplate<String, String> redisTemplate;

    /*
    * Token 발급 - 로그인, 회원가입
    * */
    public void injectToken(HttpServletResponse response, final MemberPrincipal memberPrincipal) {
        Payload payload = new Payload(memberPrincipal);
        injectToken(response, payload);
    }

    /*
    * AccessToken, RefreshToken 을 응답에 삽입
    * */
    public void injectToken(HttpServletResponse response, final Payload payload) {
        String accessToken = accessTokenHandler.createToken(payload);
        String refreshToken = refreshTokenHandler.createToken(payload);
        Cookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);

        saveRefreshTokenInRedis(payload, refreshToken);
        response.setHeader(JwtEnv.ACCESS.getKey(), accessToken);
        response.addCookie(refreshTokenCookie);
    }

    // Redis 에 RefreshToken 저장
    private void saveRefreshTokenInRedis(Payload payload, String refreshToken) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        // get Key from payload (Ex) "memberId-1-USER"
        String redisKey = JwtEnv.getRedisKeyForRefresh(payload);
        operations.set(redisKey, refreshToken);
    }

    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie refreshTokenCookie = new Cookie(JwtEnv.REFRESH.getKey(), refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge((int) Duration.ofMinutes(10).toSeconds());
        return refreshTokenCookie;
    }
}
