package com.e2i.wemeet.controller.team;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.e2i.wemeet.service.code.CodeService;
import com.e2i.wemeet.service.team.TeamService;
import com.e2i.wemeet.support.config.AbstractControllerUnitTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(TeamController.class)
class TeamControllerTest extends AbstractControllerUnitTest {

    @MockBean
    private TeamService teamService;

    @MockBean
    private CodeService codeService;

    // TODO :: service refactoring
    @DisplayName("팀 생성 성공")
    @WithCustomMockUser
    @Test
    void createTeam_Success() throws Exception {
        // given

        // when

        // then
    }

    // TODO :: service refactoring
    @DisplayName("팀 수정 성공")
    @WithCustomMockUser(role = "MANAGER")
    @Test
    void updateTeam_Success() throws Exception {
        // given

        // when

        // then

    }

    // TODO :: service refactoring
    @DisplayName("소속 팀이 있는 경우 팀 정보 조회 성공")
    @WithCustomMockUser
    @Test
    void getMyTeamDetailWithExistTeam_Success() throws Exception {
        // given

        // when

        // then
    }

    // TODO :: service refactoring
    @DisplayName("소속 팀이 없는 경우 null이 반환된다.")
    @WithCustomMockUser
    @Test
    void getMyTeamDetailWithNotExistTeam_Success() throws Exception {
        // when

        // then
    }

    // TODO :: service refactoring
    @DisplayName("팀 삭제 성공")
    @WithCustomMockUser(role = "MANAGER")
    @Test
    void deleteTeam_Success() throws Exception {
        // given

        // when

        // then
    }

    // TODO :: service refactoring
    private void createTeamWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("팀 생성",
                    ResourceSnippetParameters.builder()
                        .tag("팀 관련 API")
                        .summary("팀 생성 API 입니다.")
                        .description(
                            """
                                    팀 생성을 진행합니다.
                                """),
                    requestFields(
                        fieldWithPath("memberNum").type(JsonFieldType.NUMBER).description("인원수"),
                        fieldWithPath("region").type(JsonFieldType.STRING).description("선호 지역"),
                        fieldWithPath("drinkRate").type(JsonFieldType.NUMBER)
                            .description("음주 수치"),
                        fieldWithPath("drinkWithGame").type(JsonFieldType.STRING)
                            .description("술게임 여부"),
                        fieldWithPath("additionalActivity").type(JsonFieldType.STRING).optional()
                            .description("추가 활동"),
                        fieldWithPath("introduction").type(JsonFieldType.STRING)
                            .description("팀 소개"),
                        fieldWithPath("images").type(JsonFieldType.ARRAY).description("팀 이미지"),
                        fieldWithPath("members").type(JsonFieldType.ARRAY)
                            .description("팀원 정보")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                            .description("data에는 아무 값도 반환되지 않습니다")
                    )
                ));
    }

    private void updateTeamWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("팀 수정",
                    ResourceSnippetParameters.builder()
                        .tag("팀 관련 API")
                        .summary("팀 수정 API 입니다.")
                        .description(
                            """
                                    팀 정보 수정을 진행합니다.
                                """),
                    requestFields(
                        fieldWithPath("region").type(JsonFieldType.STRING).description("선호 지역"),
                        fieldWithPath("drinkRate").type(JsonFieldType.NUMBER)
                            .description("음주 수치"),
                        fieldWithPath("drinkWithGame").type(JsonFieldType.STRING)
                            .description("술게임 여부"),
                        fieldWithPath("additionalActivity").type(JsonFieldType.STRING).optional()
                            .description("추가 활동"),
                        fieldWithPath("introduction").type(JsonFieldType.STRING)
                            .description("팀 소개"),
                        fieldWithPath("images").type(JsonFieldType.ARRAY).description("팀 이미지"),
                        fieldWithPath("members").type(JsonFieldType.ARRAY)
                            .description("팀원 정보")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                            .description("data에는 아무 값도 반환되지 않습니다")
                    )
                ));
    }

    private void readTeamWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("마이 팀 조회",
                    ResourceSnippetParameters.builder()
                        .tag("팀 관련 API")
                        .summary("팀 조회 API 입니다.")
                        .description(
                            """
                                    팀 정보 조회 진행합니다.
                                    소속 팀이 없는 경우 data에는 아무 값도 반환되지 않습니다.
                                """),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.memberCount").type(JsonFieldType.NUMBER)
                            .description("인원수"),
                        fieldWithPath("data.region").type(JsonFieldType.STRING)
                            .description("선호 지역"),
                        fieldWithPath("data.drinkingOption").type(JsonFieldType.STRING)
                            .description("술자리 선호 여부"),
                        fieldWithPath("data.preferenceMeetingTypeList").type(
                                JsonFieldType.ARRAY)
                            .description("선호 미팅 유형"),
                        fieldWithPath("data.additionalActivity").type(JsonFieldType.STRING)
                            .description("추가 활동 (Nullable)"),
                        fieldWithPath("data.introduction").type(JsonFieldType.STRING)
                            .description("팀 소개"),
                        fieldWithPath("data.managerImageAuth").type(JsonFieldType.BOOLEAN)
                            .description("팀 대표자 프로필 인증 여부")
                    )
                ));
    }

    private void deleteTeamWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("팀 삭제",
                    ResourceSnippetParameters.builder()
                        .tag("팀 관련 API")
                        .summary("팀 삭제 API 입니다.")
                        .description(
                            """
                                    현재 속한 팀을 삭제합니다.
                                    팀과 관련된 모든 정보가 삭제됩니다.
                                  
                                """),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                            .description("data에는 아무 값도 반환되지 않습니다")
                    )
                ));
    }
}
