package com.e2i.wemeet.controller.team;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.ModifyTeamRequestDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import com.e2i.wemeet.service.code.CodeService;
import com.e2i.wemeet.service.team.TeamService;
import com.e2i.wemeet.support.config.AbstractUnitTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
import com.e2i.wemeet.support.fixture.TeamFixture;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(TeamController.class)
class TeamControllerTest extends AbstractUnitTest {

    @MockBean
    private TeamService teamService;

    @MockBean
    private CodeService codeService;


    @DisplayName("팀 생성 성공")
    @WithCustomMockUser
    @Test
    void createTeam_Success() throws Exception {
        // given
        CreateTeamRequestDto request = TeamFixture.TEST_TEAM.createTeamRequestDto();

        when(codeService.findCodeList(anyList())).thenReturn(List.of());
        when(teamService.createTeam(anyLong(), any(CreateTeamRequestDto.class), anyList(),
            any(HttpServletResponse.class)))
            .thenReturn(1L);

        // when
        ResultActions perform = mockMvc.perform(post("/v1/team")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(request)));

        perform
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Create Team Success"))
            .andExpect(jsonPath("$.data").exists());

        // then
        verify(teamService).createTeam(anyLong(), any(CreateTeamRequestDto.class), anyList(),
            any(HttpServletResponse.class));
        createTeamWriteRestDocs(perform);
    }

    @DisplayName("팀 수정 성공")
    @WithCustomMockUser(role = "MANAGER")
    @Test
    void modifyTeam_Success() throws Exception {
        // given
        ModifyTeamRequestDto request = TeamFixture.TEST_TEAM.modifyTeamRequestDto();
        when(codeService.findCodeList(anyList())).thenReturn(List.of());

        // when
        ResultActions perform = mockMvc.perform(put("/v1/team")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(request)));

        perform
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Modify Team Success"))
            .andExpect(jsonPath("$.data").doesNotExist());

        // then
        verify(teamService).modifyTeam(1L, request, List.of());

        modifyTeamWriteRestDocs(perform);
    }

    @DisplayName("소속 팀이 있는 경우 팀 정보 조회 성공")
    @WithCustomMockUser
    @Test
    void getMyTeamDetailWithExistTeam_Success() throws Exception {
        // given
        MyTeamDetailResponseDto response = TeamFixture.TEST_TEAM.myTeamDetailResponseDto();
        when(teamService.getMyTeamDetail(anyLong())).thenReturn(response);

        // when
        ResultActions perform = mockMvc.perform(get("/v1/team"));

        perform
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Get  My Team Detail Success"))
            .andExpect(jsonPath("$.data").exists());

        // then
        verify(teamService).getMyTeamDetail(1L);

        getMyTeamDetailWriteRestDocs(perform);
    }

    @DisplayName("소속 팀이 없는 경우 null이 반환된다.")
    @WithCustomMockUser
    @Test
    void getMyTeamDetailWithNotExistTeam_Success() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(get("/v1/team"));

        perform
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Get  My Team Detail Success"))
            .andExpect(jsonPath("$.data").doesNotExist());

        // then
        verify(teamService).getMyTeamDetail(1L);
    }

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
                        fieldWithPath("memberCount").type(JsonFieldType.NUMBER).description("인원수"),
                        fieldWithPath("region").type(JsonFieldType.STRING).description("선호 지역"),
                        fieldWithPath("drinkingOption").type(JsonFieldType.STRING)
                            .description("술자리 여부"),
                        fieldWithPath("preferenceMeetingTypeList").type(JsonFieldType.ARRAY)
                            .description("선호 미팅 유형"),
                        fieldWithPath("additionalActivity").type(JsonFieldType.STRING).optional()
                            .description("추가 활동"),
                        fieldWithPath("introduction").type(JsonFieldType.STRING)
                            .description("팀 소개")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NUMBER)
                            .description("생성된 팀의 teamId")
                    )
                ));
    }

    private void modifyTeamWriteRestDocs(ResultActions perform) throws Exception {
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
                        fieldWithPath("drinkingOption").type(JsonFieldType.STRING)
                            .description("술자리 여부"),
                        fieldWithPath("preferenceMeetingTypeList").type(JsonFieldType.ARRAY)
                            .description("선호 미팅 유형"),
                        fieldWithPath("additionalActivity").type(JsonFieldType.STRING).optional()
                            .description("추가 활동"),
                        fieldWithPath("introduction").type(JsonFieldType.STRING)
                            .description("팀 소개")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                            .description("data에는 아무 값도 반환되지 않습니다")
                    )
                ));
    }

    private void getMyTeamDetailWriteRestDocs(ResultActions perform) throws Exception {
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


}