package com.e2i.wemeet.controller.credential;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.dto.request.credential.MailCredentialCheckRequestDto;
import com.e2i.wemeet.dto.request.credential.MailCredentialRequestDto;
import com.e2i.wemeet.dto.request.credential.SmsCredentialRequestDto;
import com.e2i.wemeet.support.config.AbstractIntegrationTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
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
        SmsCredentialRequestDto requestDto = new SmsCredentialRequestDto(phone);

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

        smsCredentialWriteRestDocs(perform);
    }

    @DisplayName("이메일 인증 번호 발급에 성공한다")
    @Test
    void issueMailCredential() throws Exception {
        final String mail = "ghkdalsgus0809@swu.ac.kr";
        final String college = "서울여자대학교";
        MailCredentialRequestDto requestDto = new MailCredentialRequestDto(college, mail);

        ResultActions perform = mvc.perform(
            RestDocumentationRequestBuilders.post("/v1/auth/mail/request")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson(requestDto))
        );

        perform.andExpectAll(
            status().isOk(),
            jsonPath("$.status").value("SUCCESS"),
            jsonPath("$.message").value("대학 메일 인증 번호 발급 성공"),
            jsonPath("$.data").isEmpty()
        );

        Integer find = Integer.valueOf(redisTemplate.opsForValue().get(mail));
        assertThat(find).isGreaterThan(100000);

        mailCredentialWriteRestDocs(perform);
    }

    @DisplayName("올바른 인증번호를 입력하면 이메일 인증에 성공한다.")
    @WithCustomMockUser
    @Test
    void matchMailCredential() throws Exception {
        // given - 인증 번호 발급
        final String mail = "ghkdalsgus0809@swu.ac.kr";
        final String college = "서울여자대학교";
        MailCredentialRequestDto credentialRequestDto = new MailCredentialRequestDto(college, mail);
        mvc.perform(
            RestDocumentationRequestBuilders.post("/v1/auth/mail/request")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson(credentialRequestDto))
        );
        String credential = redisTemplate.opsForValue().get(mail);

        // when - 인증 확인 요청
        MailCredentialCheckRequestDto mailCredentialCheckRequestDto = new MailCredentialCheckRequestDto(
            mail, credential);

        ResultActions perform = mvc.perform(
            RestDocumentationRequestBuilders.post("/v1/auth/mail/validate")
                .header("AccessToken", "access_token")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson(mailCredentialCheckRequestDto))
        );

        // then
        perform.andExpectAll(
            status().isOk(),
            jsonPath("$.status").value("SUCCESS"),
            jsonPath("$.message").value("대학 메일 인증 번호 확인 요청 성공"),
            jsonPath("$.data").isNotEmpty());

        mailCredentialCheckWriteRestDocs(perform);
    }

    private void smsCredentialWriteRestDocs(ResultActions perform) throws Exception {
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
                        fieldWithPath("data").type(JsonFieldType.NULL)
                            .description("data에는 아무 값도 반환되지 않습니다")
                    )
                ));
    }

    private void mailCredentialWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("이메일 인증번호 발급",
                    ResourceSnippetParameters.builder()
                        .tag("이메일 인증번호 발급")
                        .summary("이메일 인증번호를 발급하는 API 입니다.")
                        .description(
                            """
                                    mail 값으로 넘어온 이메일에 인증 번호를 발송합니다.
                                """),
                    requestFields(
                        fieldWithPath("mail").type(JsonFieldType.STRING).description("메일 주소"),
                        fieldWithPath("college").type(JsonFieldType.STRING).description("대학교 이름")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                            .description("data에는 아무 값도 반환되지 않습니다")
                    )
                ));
    }

    private void mailCredentialCheckWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("이메일 인증번호 검증",
                    ResourceSnippetParameters.builder()
                        .tag("이메일 인증번호 검증")
                        .summary("이메일 인증번호가 일치하는지 검증하는 API 입니다.")
                        .description(
                            """
                                    Mail 인증 번호를 확인하는 API 입니다.
                                    Header에 AccessToken이 필요합니다.
                                """),
                    requestHeaders(
                        headerWithName("AccessToken").description("액세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("mail").type(JsonFieldType.STRING).description("메일 주소"),
                        fieldWithPath("authCode").type(JsonFieldType.STRING).description("인증 번호")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.BOOLEAN)
                            .description("인증번호 일치 여부")
                    )
                ));
    }
}
