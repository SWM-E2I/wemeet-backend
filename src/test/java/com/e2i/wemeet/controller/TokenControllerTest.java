package com.e2i.wemeet.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.dto.request.token.PushTokenRequestDto;
import com.e2i.wemeet.dto.response.persist.PersistResponseDto;
import com.e2i.wemeet.security.token.JwtEnv;
import com.e2i.wemeet.support.config.AbstractControllerUnitTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

class TokenControllerTest extends AbstractControllerUnitTest {

    @DisplayName("ACCESS TOKEN을 통해 유저의 상태 정보를 조회할 수 있다.")
    @WithCustomMockUser
    @Test
    void persist() throws Exception {
        // given
        given(tokenService.persistLogin(1L))
            .willReturn(createPersistResponseDto());

        // when
        ResultActions perform = mockMvc.perform(
            get("/v1/auth/persist")
                .header("AccessToken", "Bearer " + "token")
        );

        // then
        perform.andExpectAll(
            status().isOk(),
            jsonPath("$.status").value("SUCCESS"),
            jsonPath("$.message").value("Persist Login Success"),
            jsonPath("$.data").isNotEmpty()
        );

        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("PERSIST LOGIN 요청",
                    ResourceSnippetParameters.builder()
                        .tag("토큰 관련 API")
                        .summary("AccessToken 을 통해 유저의 상태 정보를 반환합니다.")
                        .description(
                            """
                                   AccessToken 을 통해 유저의 상태 정보를 반환합니다.
                                """),
                    requestHeaders(
                        headerWithName(JwtEnv.ACCESS.getKey()).description("Access Token")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                            .description("사용자의 닉네임을 반환합니다."),
                        fieldWithPath("data.emailAuthenticated").type(JsonFieldType.BOOLEAN)
                            .description("이메일 인증 여부를 반환합니다."),
                        fieldWithPath("data.profileImageAuthenticated").type(JsonFieldType.BOOLEAN)
                            .description("선호도 조사 여부를 반환합니다."),
                        fieldWithPath("data.hasTeam").type(JsonFieldType.BOOLEAN)
                            .description("프로필 이미지의 등록 여부를 반환합니다."),
                        fieldWithPath("data.hasMainProfileImage").type(JsonFieldType.BOOLEAN)
                            .description("프로필 이미지 인증 여부를 반환합니다."),
                        fieldWithPath("data.basicProfileImage").type(JsonFieldType.STRING)
                            .description("일반 프로필 이미지 사진 경로를 반환합니다."),
                        fieldWithPath("data.lowProfileImage").type(JsonFieldType.STRING)
                            .description("작은 프로필 이미지 사진 경로를 반환합니다."),
                        fieldWithPath("data.pushTokens").type(JsonFieldType.ARRAY)
                            .description("사용자의 푸시 토큰들을 반환합니다.")
                    )
                ));
    }

    @DisplayName("푸시 토큰을 저장할 수 있다.")
    @Test
    void savePushToken() throws Exception {
        //given
        given(accessTokenHandler.extractMemberId("token"))
            .willReturn(Optional.of(1L));
        PushTokenRequestDto request = new PushTokenRequestDto("token");

        // when
        ResultActions perform = mockMvc.perform(
            post("/v1/push")
                .contentType(APPLICATION_JSON)
                .header("AccessToken", "Bearer " + "token")
                .content(toJson(request))
        );

        // then
        perform.andExpectAll(
            status().isOk(),
            jsonPath("$.status").value("SUCCESS"),
            jsonPath("$.message").value("Push Token Save Success"),
            jsonPath("$.data").isEmpty()
        );

        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("PUSH TOKEN 저장 요청",
                    ResourceSnippetParameters.builder()
                        .tag("PUSH TOKEN 저장")
                        .summary("PUSH TOKEN을 저장합니다.")
                        .description(
                            """
                                   PUSH 토큰을 저장합니다. AccessToken 헤더를 전달하지 않으면 memberId가 null 로 저장됩니다.
                                   이미 DB 저장되어있는 토큰에 memberId를 업데이트하려면
                                   body에 token, AccessToken 헤더에 해당 User에 AccessToken을 전달하면 됩니다.
                                """),
                    requestHeaders(
                        headerWithName(JwtEnv.ACCESS.getKey()).description("Access Token")
                    ),
                    requestFields(
                        fieldWithPath("pushToken").type(JsonFieldType.STRING).description("푸시 토큰")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
                    )
                ));
    }

    @DisplayName("액세스 토큰을 전달하지 않을 경우에도 푸시 토큰을 저장할 수 있다.")
    @Test
    void savePushToken_withNoAccessToken() throws Exception {
        //given
        given(accessTokenHandler.extractMemberId(""))
            .willReturn(Optional.empty());
        PushTokenRequestDto request = new PushTokenRequestDto("token");

        // when
        ResultActions perform = mockMvc.perform(
            post("/v1/push")
                .contentType(APPLICATION_JSON)
                .content(toJson(request))
        );

        // then
        perform.andExpectAll(
            status().isOk(),
            jsonPath("$.status").value("SUCCESS"),
            jsonPath("$.message").value("Push Token Save Success"),
            jsonPath("$.data").isEmpty()
        );
    }

    private PersistResponseDto createPersistResponseDto() {
        return PersistResponseDto.builder()
            .nickname("닉네임")
            .emailAuthenticated(true)
            .profileImageAuthenticated(true)
            .hasTeam(true)
            .hasMainProfileImage(true)
            .lowProfileImage("lowUrl")
            .basicProfileImage("basicUrl")
            .pushTokens(List.of("expo token1", "expo token2"))
            .build();
    }
}