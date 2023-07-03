package com.e2i.wemeet.controller.credential;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.dto.request.credential.CredentialRequestDto;
import com.e2i.wemeet.support.config.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class CredentialControllerTest extends AbstractIntegrationTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @DisplayName("인증 번호 발급에 성공한다")
    @Test
    void issueSmsCredential() throws Exception {
        final String phone = "+821088990011";
        CredentialRequestDto requestDto = new CredentialRequestDto(phone);

        ResultActions perform = mvc.perform(
            post("/v1/auth/phone/issue")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson(requestDto))
        );

        perform.andExpectAll(
            status().isOk(),
            jsonPath("$.status").value("SUCCESS"),
            jsonPath("$.message").value("휴대폰 인증 번호 발급 성공"),
            jsonPath("$.data").isEmpty()
        );

        Integer find = Integer.valueOf(redisTemplate.opsForValue().get(phone));
        assertThat(find).isGreaterThan(100000);
    }
}