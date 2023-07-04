package com.e2i.wemeet.controller.admin;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.support.config.AbstractIntegrationTest;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("관리자 token controller Test")
class TestTokenInjectionControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("회원을 등록하고 AccessToken 을 발급받는데 성공한다")
    @Test
    void registerAndReturnAccessToken() throws Exception {
        // given

        // when
        ResultActions perform = mvc.perform(
            post("/test/register")
                .queryParam("fixture", "KAI")
                .contentType("application/json"));

        // then
        perform.andExpectAll(
            status().isOk(),
            jsonPath("$.status").value("SUCCESS"),
            jsonPath("$.message").value("member register and access token injection success"),
            jsonPath("$.data").hasJsonPath());

        writeRestDocsRegisterAndReturnAccessToken(perform);
    }

    @DisplayName("AccessToken 을 재발급 받는데 성공한다")
    @Test
    void getAccessToken() throws Exception {
        // given
        Member member = memberRepository.save(AdminMemberFixture.KAI.create());

        // when
        ResultActions perform = mvc.perform(
            post("/test/access/{memberId}", member.getMemberId())
        );

        // then
        perform.andExpectAll(
            status().isOk(),
            jsonPath("$.status").value("SUCCESS"),
            jsonPath("$.message").value("Access Token Injection Success"),
            jsonPath("$.data").isEmpty());

        writeRestDocsGetAccessToken(perform);
    }

    private void writeRestDocsGetAccessToken(ResultActions perform) throws Exception {
        perform
            .andDo(MockMvcRestDocumentationWrapper.document("AccessToken 재발급 테스트",
                ResourceSnippetParameters.builder()
                    .tag("관리자 Controller")
                    .summary("AccessToken 을 재발급 받습니다.")
                    .description(
                        """
                                AccessToken 을 재발급 받습니다.
                                pathParameter 에 사용자 ID 를 넘겨주면, 해당 사용자의 AccessToken 을 재발급 받습니다.
                                ex) /test/access/1 -> memberId 가 1 인 사용자의 AccessToken 재발급. 
                            """
                    )
                    .pathParameters(
                        parameterWithName("memberId").description("사용자 ID")
                    )
                    .responseHeaders(
                        headerWithName("AccessToken").description("AccessToken")
                    )
                    .responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                            .description("data는 null 입니다.")
                    )
            ));
    }

    private void writeRestDocsRegisterAndReturnAccessToken(ResultActions perform) throws Exception {
        perform
            .andDo(print())
            .andDo(MockMvcRestDocumentationWrapper.document("test-token-injection",
                ResourceSnippetParameters.builder()
                    .tag("관리자 Controller")
                    .summary("Test 사용자를 생성하고 AccessToken과 RefreshToken을 발급 받습니다!")
                    .description(
                        """
                                Test 사용자를 생성하고 AccessToken과 RefreshToken을 발급 받습니다.
                                쿼리 파라미터 "fixture" 에 사용자 이름을 입력합니다.
                                ex) ?fixture=KAI, ?fixture=RIM, ?fixture=SEYUN                     
                            """
                    )
                    .queryParameters(
                        parameterWithName("fixture").description("사용자 이름")
                    )
                    .responseHeaders(
                        headerWithName("AccessToken").description("AccessToken")
                    )
                    .responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("사용자 데이터")
                    )
            ));
    }
}