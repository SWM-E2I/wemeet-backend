package com.e2i.wemeet.config.security.filter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.config.security.token.JwtEnv;
import com.e2i.wemeet.config.security.token.Payload;
import com.e2i.wemeet.config.security.token.handler.RefreshTokenHandler;
import com.e2i.wemeet.domain.member.Role;
import com.e2i.wemeet.exception.token.RefreshTokenMismatchException;
import com.e2i.wemeet.support.config.AbstractIntegrationTest;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("Token 재발급 테스트")
class RefreshTokenProcessingFilterTest extends AbstractIntegrationTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RefreshTokenHandler refreshTokenHandler;

    @DisplayName("refresh token을 이용하여 access token을 재발급한다.")
    @Test
    void refresh() throws Exception {
        // set
        Payload payload = new Payload(100L, Role.USER.name());
        String refreshToken = refreshTokenHandler.createToken(payload);

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String redisKeyForRefresh = JwtEnv.getRedisKeyForRefresh(payload);
        operations.set(redisKeyForRefresh, refreshToken);

        // given
        ResultActions perform = mvc.perform(
            RestDocumentationRequestBuilders.post("/v1/auth/refresh")
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

        writeRestDocs(perform);
    }

    @DisplayName("refresh token 이 다를 경우 재발급에 실패한다.")
    //@Test
    void refreshFail() {
        // set
        Payload payload = new Payload(100L, Role.USER.name());
        String refreshToken = refreshTokenHandler.createToken(payload);

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String redisKeyForRefresh = JwtEnv.getRedisKeyForRefresh(payload);
        operations.set(redisKeyForRefresh, refreshToken);

        final String invalid = refreshTokenHandler.createToken(payload);
        // given

        assertThatThrownBy(() -> mvc.perform(
            post("/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson(payload))
                .cookie(new Cookie(JwtEnv.REFRESH.getKey(), invalid))
        )).isExactlyInstanceOf(RefreshTokenMismatchException.class);
    }

    private void writeRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("Refresh Token",
                    ResourceSnippetParameters.builder()
                        .tag("토큰 갱신 요청")
                        .summary("RefreshToken 을 사용하여 Access, Refresh Token을 갱신합니다.")
                        .description(
                            """
                                RefreshToken 을 사용하여 Access, Refresh Token을 갱신합니다. \n
                                현재 사용자의 memberId와 role, Cookie에 RefreshToken 값을 넘겨주어야합니다.
                            """),
                    requestFields(
                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                        fieldWithPath("role").type(JsonFieldType.STRING).description("사용자 권한")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL).description("data 는 null 입니다.")
                    )
                ));
    }
}