package com.e2i.wemeet.controller.team;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import com.e2i.wemeet.service.code.CodeService;
import com.e2i.wemeet.service.team.TeamService;
import com.e2i.wemeet.support.config.AbstractUnitTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
import com.e2i.wemeet.support.fixture.TeamFixture;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
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
        when(teamService.createTeam(anyLong(), any(CreateTeamRequestDto.class), anyList()))
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
        verify(teamService).createTeam(1L, request, List.of());

        createTeamWriteRestDocs(perform);
    }

    @DisplayName("팀 생성 성공")
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

    private void createTeamWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("팀 생성",
                    ResourceSnippetParameters.builder()
                        .tag("팀 생성")
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
                        .tag("팀 수정")
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
}
