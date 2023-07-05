package com.e2i.wemeet.controller.credential;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.dto.request.credential.CredentialRequestDto;
import com.e2i.wemeet.support.config.AbstractIntegrationTest;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
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
            RestDocumentationRequestBuilders.post("/v1/auth/phone/issue")
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

        writeRestDocs(perform);
    }

    private void writeRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("휴대폰 인증번호 발급",
                    ResourceSnippetParameters.builder()
                        .tag("휴대폰 인증번호 발급")
                        .summary("휴대폰 인증번호를 발급하는 API 입니다.")
                        .description(
                            """
                                target 값으로 넘어온 휴대폰 번호에 SMS 인증 번호를 발송합니다.
                            """),
                    requestFields(
                        fieldWithPath("target").type(JsonFieldType.STRING).description("휴대폰 번호")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL).description("data에는 아무 값도 반환되지 않습니다")
                    )
                ));
    }
}