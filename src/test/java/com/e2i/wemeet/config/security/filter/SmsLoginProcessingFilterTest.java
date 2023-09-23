package com.e2i.wemeet.config.security.filter;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.dto.request.LoginRequestDto;
import com.e2i.wemeet.dto.request.credential.SmsCredentialRequestDto;
import com.e2i.wemeet.security.token.JwtEnv;
import com.e2i.wemeet.support.module.AbstractIntegrationTest;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@Profile("prod")
@DisplayName("SMS 인증 테스트")
class SmsLoginProcessingFilterTest extends AbstractIntegrationTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("인증 번호를 발급하고 인증하는 데에 성공한다.")
    @TestFactory
    Collection<DynamicTest> smsLoginProcessDynamic() {
        // given
        final ValueOperations<String, String> operations = redisTemplate.opsForValue();
        final String phone = "+821088990011";

        return List.of(
            DynamicTest.dynamicTest("인증 번호를 발급 받을 수 있다.", () -> {
                // given
                SmsCredentialRequestDto credentialRequestDto = new SmsCredentialRequestDto(phone);

                // when
                mvc.perform(
                    post("/v1/auth/phone/issue")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJson(credentialRequestDto))
                );

                // then
                String credential = operations.get(phone);
                assertThat(credential).hasSize(6);
            }),
            DynamicTest.dynamicTest("올바른 인증 번호를 입력 하면 로그인에 성공한다.", () -> {
                // given
                String credential = operations.get(phone);
                LoginRequestDto loginRequestDto = new LoginRequestDto(phone, credential);

                // when
                ResultActions perform = mvc.perform(
                    RestDocumentationRequestBuilders.post("/v1/auth/phone/validate")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJson(loginRequestDto))
                );

                perform.andDo(MockMvcResultHandlers.print());

                // then
                perform.andExpectAll(
                    status().isOk(),
                    jsonPath("$.status").value("SUCCESS"),
                    jsonPath("$.message").value("인증에 성공하였습니다."),
                    jsonPath("$.data").isNotEmpty());

                writeRestDocs(perform);
            })
        );
    }

    @DisplayName("SMS 인증에 성공한 사용자가 이미 가입된 사용자라면 AccessToken 과 RefreshToken이 반환된다.")
    @TestFactory
    Collection<DynamicTest> smsLoginProcessWithRegisteredMemberDynamic() {
        // given
        final ValueOperations<String, String> operations = redisTemplate.opsForValue();
        final Member savedMember = memberRepository.save(KAI.create());
        final String savedMembersPhone = savedMember.getPhoneNumber();

        return List.of(
            DynamicTest.dynamicTest("인증 번호를 발급 받을 수 있다.", () -> {
                // given
                SmsCredentialRequestDto credentialRequestDto = new SmsCredentialRequestDto(
                    savedMembersPhone);

                // when
                mvc.perform(
                    post("/v1/auth/phone/issue")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJson(credentialRequestDto))
                );

                // then
                String credential = operations.get(savedMembersPhone);
                assertThat(credential).hasSize(6);
            }),
            DynamicTest.dynamicTest("인증 성공 후 AccessToken 과 RefreshToken 이 반환된다.", () -> {
                String credential = operations.get(savedMembersPhone);
                LoginRequestDto loginRequestDto = new LoginRequestDto(savedMembersPhone,
                    credential);

                ResultActions perform = mvc.perform(
                    post("/v1/auth/phone/validate")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJson(loginRequestDto))
                );

                // then
                perform.andExpectAll(
                    status().isOk(),
                    header().exists(JwtEnv.ACCESS.getKey()),
                    header().exists(JwtEnv.REFRESH.getKey()),
                    jsonPath("$.status").value("SUCCESS"),
                    jsonPath("$.message").value("인증에 성공하였습니다."),
                    jsonPath("$.data").isNotEmpty()
                );
            })
        );
    }

    @DisplayName("인증번호가 일치하지 않으면 SMS 인증에 실패한다.")
        //@Test
    void smsLoginFail() throws Exception {
        // given - 인증 번호 발급
        final String phone = "+821088990011";
        SmsCredentialRequestDto credentialRequestDto = new SmsCredentialRequestDto(phone);

        mvc.perform(
            post("/v1/auth/phone/issue")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson(credentialRequestDto))
        );

        // when / then - 로그인 요청
        LoginRequestDto loginRequestDto = new LoginRequestDto(phone, "100000");

        ResultActions perform = mvc.perform(
            post("/v1/auth/phone/validate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson(loginRequestDto))
        );

        perform.andExpect(status().isUnauthorized());
    }

    private void writeRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("휴대폰 인증번호 검증",
                    ResourceSnippetParameters.builder()
                        .tag("인증 관련 API")
                        .summary("휴대폰 인증번호가 일치하는지 검증하는 API 입니다.")
                        .description(
                            """
                                    SMS 인증 번호를 확인하는 API 입니다 \n
                                    인증 번호가 일치하면 AccessToken 과 RefreshToken을 반환합니다 \n
                                    인증 번호가 일치하지만 회원가입 되어있지 않은 사용자라면 Token을 반환하지 않습니다 
                                """),
                    requestFields(
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                        fieldWithPath("credential").type(JsonFieldType.STRING).description("인증 번호")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("회원 가입이 되어있지 않은 사용자의 경우 null로 채워서 반환됨"),
                        fieldWithPath("data.memberId")
                            .description("회원 아이디"),
                        fieldWithPath("data.isRegistered")
                            .description("회원 가입 여부"),
                        fieldWithPath("data.withdrawal")
                            .description("회원 탈퇴 여부"),
                        fieldWithPath("data.role[].authority").type(JsonFieldType.STRING)
                            .description("회원 권한")
                    )
                ));
    }
}