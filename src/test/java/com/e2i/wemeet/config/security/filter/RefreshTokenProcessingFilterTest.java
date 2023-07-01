package com.e2i.wemeet.config.security.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.config.security.token.JwtEnv;
import com.e2i.wemeet.config.security.token.Payload;
import com.e2i.wemeet.config.security.token.handler.RefreshTokenHandler;
import com.e2i.wemeet.domain.member.Role;
import com.e2i.wemeet.support.config.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("Token 재발급 테스트")
class RefreshTokenProcessingFilterTest extends AbstractIntegrationTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RefreshTokenHandler refreshTokenHandler;

    private Payload payload;
    private String refreshToken;

    @BeforeEach
    public void setUpRefreshToken() {
        payload = new Payload(100L, Role.USER.name());
        refreshToken = refreshTokenHandler.createToken(payload);

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String redisKeyForRefresh = JwtEnv.getRedisKeyForRefresh(payload);
        operations.set(redisKeyForRefresh, refreshToken);
    }

    //@AfterEach
    public void tearDownRefreshToken() {
        redisTemplate.delete(JwtEnv.getRedisKeyForRefresh(payload));
    }

    @DisplayName("refresh token을 이용하여 access token을 재발급한다.")
    //@Test
    void refresh() throws Exception {
        // given
        ResultActions perform = mvc.perform(
            post("/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson(payload))
                .cookie(new Cookie(JwtEnv.REFRESH.getKey(), refreshToken))
        );

        // when
        perform.andExpectAll(
            status().isOk(),
            header().exists(JwtEnv.ACCESS.getKey()),
            cookie().exists(JwtEnv.REFRESH.getKey())
        );
    }

    @DisplayName("refresh token 이 다를 경우 재발급에 실패한다.")
    //@Test
    void refreshFail() throws Exception {
        final String invalid = refreshTokenHandler.createToken(payload);
        // given
        ResultActions perform = mvc.perform(
            post("/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson(payload))
                .cookie(new Cookie(JwtEnv.REFRESH.getKey(), invalid))
        );

        // when
        perform.andExpect(status().isUnauthorized());
    }
}