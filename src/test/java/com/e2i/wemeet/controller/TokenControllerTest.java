package com.e2i.wemeet.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.dto.response.persist.PersistResponseDto;
import com.e2i.wemeet.support.config.AbstractControllerUnitTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
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
            RestDocumentationRequestBuilders.get("/v1/auth/persist")
                .header("Authorization", "Bearer " + "token")
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
                        .tag("Persist Login API")
                        .summary("AccessToken 을 통해 유저의 상태 정보를 반환합니다.")
                        .description(
                            """
                                   AccessToken 을 통해 유저의 상태 정보를 반환합니다.
                                """),
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
                            .description("팀의 존재 여부를 반환합니다."),
                        fieldWithPath("data.lowProfileImage").type(JsonFieldType.STRING)
                            .description("팀의 존재 여부를 반환합니다.")
                    )
                ));
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
            .build();
    }
}