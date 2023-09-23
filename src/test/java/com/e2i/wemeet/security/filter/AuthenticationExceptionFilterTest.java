package com.e2i.wemeet.security.filter;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.dto.response.ResponseStatus;
import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.security.token.JwtEnv;
import com.e2i.wemeet.support.module.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

class AuthenticationExceptionFilterTest extends AbstractIntegrationTest {

    @DisplayName("존재하지 않는 API 에 대해 요청을 보내면 404 응답을 받는다.")
    @Test
    void error404() throws Exception {
        // given
        final String invalidUrl = "/invalid/url";

        // when
        ResultActions perform = mvc.perform(
            get(invalidUrl)
        );

        // then
        perform.andExpectAll(
            status().isNotFound()
        );
    }

    @DisplayName("유효하지 않은 AccessToken 을 가지고 요청을 보내면 401 응답을 받는다.")
    @Test
    void invalidAccessToken() throws Exception {
        // given
        final String invalidAccessToken = "Bearer invalidAccessToken";

        // when
        ResultActions perform = mvc.perform(
            post("/v1/member")
                .header(JwtEnv.ACCESS.getKey(), invalidAccessToken)
        );

        // then
        perform.andExpectAll(
            status().isUnauthorized(),
            jsonPath("$.status").value(ResponseStatus.FAIL.name()),
            jsonPath("$.code").value(ErrorCode.JWT_DECODE.getCode()),
            jsonPath("$.message").value("토큰을 파싱하는데 실패했습니다.")
        );
    }
}