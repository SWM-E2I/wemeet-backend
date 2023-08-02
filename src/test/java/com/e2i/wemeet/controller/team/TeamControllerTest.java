package com.e2i.wemeet.controller.team;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.InviteTeamRequestDto;
import com.e2i.wemeet.dto.request.team.ModifyTeamRequestDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import com.e2i.wemeet.dto.response.team.TeamManagementResponseDto;
import com.e2i.wemeet.dto.response.team.TeamMemberResponseDto;
import com.e2i.wemeet.service.code.CodeService;
import com.e2i.wemeet.service.team.TeamService;
import com.e2i.wemeet.support.config.AbstractControllerUnitTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
import com.e2i.wemeet.support.fixture.MemberFixture;
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
class TeamControllerTest extends AbstractControllerUnitTest {

    @MockBean
    private TeamService teamService;

    @MockBean
    private TeamInvitationService teamInvitationService;

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
            .andExpect(jsonPath("$.message").value("Get My Team Detail Success"))
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
            .andExpect(jsonPath("$.message").value("Get My Team Detail Success"))
            .andExpect(jsonPath("$.data").doesNotExist());

        // then
        verify(teamService).getMyTeamDetail(1L);
    }

    @DisplayName("팀원 초대 요청 성공")
    @WithCustomMockUser(role = "MANAGER")
    @Test
    void inviteTeamMember_Success() throws Exception {
        // given
        InviteTeamRequestDto request = MemberFixture.JEONGYEOL.inviteTeamRequestDto();
        doNothing().when(teamInvitationService)
            .inviteTeam(anyLong(), any(InviteTeamRequestDto.class));

        // when
        ResultActions perform = mockMvc.perform(post("/v1/team/invitation")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(request)));

        perform
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Invite Team Member Success"))
            .andExpect(jsonPath("$.data").doesNotExist());

        // then
        verify(teamInvitationService).inviteTeam(anyLong(), any(InviteTeamRequestDto.class));
        inviteTeamMemberWriteRestDocs(perform);
    }

    @DisplayName("팀 초대 요청 응답 성공")
    @WithCustomMockUser
    @Test
    void setInvitationStatus_Success() throws Exception {
        // given
        doNothing().when(teamInvitationService)
            .takeAcceptStatus(anyLong(), anyLong(), anyBoolean());

        // when
        ResultActions perform = mockMvc.perform(
            put("/v1/team/invitation/{invitationId}", 1).queryParam("accepted", "true"));

        perform
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Set Invitation Success"))
            .andExpect(jsonPath("$.data").doesNotExist());

        // then
        verify(teamInvitationService).takeAcceptStatus(anyLong(), anyLong(), anyBoolean());
        setInvitationStatusWriteRestDocs(perform);
    }

    @DisplayName("팀원 조회 성공")
    @WithCustomMockUser(role = "MANAGER")
    @Test
    void getTeamMemberList_Success() throws Exception {
        // given
        TeamMemberResponseDto member = TeamMemberResponseDto.builder()
            .memberId(1L)
            .nickname("짱구")
            .memberCode("5372")
            .profileImage("profile")
            .isAccepted(true)
            .build();
        TeamManagementResponseDto result = TeamManagementResponseDto.builder()
            .teamCode("1@ds739d")
            .members(List.of(member))
            .build();

        when(teamService.getTeamMemberList(anyLong())).thenReturn(result);

        // when
        ResultActions perform = mockMvc.perform(get("/v1/team/member"));

        perform
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Get Team Member List Success"))
            .andExpect(jsonPath("$.data").exists());

        // then
        verify(teamService).getTeamMemberList(anyLong());
        getTeamMemberListWriteRestDocs(perform);
    }

    @DisplayName("팀 삭제 성공")
    @WithCustomMockUser(role = "MANAGER")
    @Test
    void deleteTeam_Success() throws Exception {
        // given
        doNothing().when(teamService).deleteTeam(anyLong());

        // when
        ResultActions perform = mockMvc.perform(delete("/v1/team"));

        perform
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Delete Team Success"))
            .andExpect(jsonPath("$.data").doesNotExist());

        // then
        verify(teamService).deleteTeam(anyLong());
        deleteTeamWriteRestDocs(perform);
    }

    @DisplayName("팀원 삭제 성공")
    @WithCustomMockUser(role = "MANAGER")
    @Test
    void deleteTeamMember_Success() throws Exception {
        // given
        doNothing().when(teamService).deleteTeamMember(anyLong(), anyLong());

        // when
        ResultActions perform = mockMvc.perform(delete("/v1/team/member/{memberId}", 2));

        perform
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Delete TeamMember Success"))
            .andExpect(jsonPath("$.data").doesNotExist());

        // then
        verify(teamService).deleteTeamMember(anyLong(), anyLong());
        deleteTeamMemberWriteRestDocs(perform);
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

    private void inviteTeamMemberWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("팀원 초대",
                    ResourceSnippetParameters.builder()
                        .tag("팀 관련 API")
                        .summary("팀원 초대 API 입니다.")
                        .description(
                            """
                                    팀원에게 팀 초대 요청을 보냅니다.
                                """),
                    requestFields(
                        fieldWithPath("nickname").type(JsonFieldType.STRING)
                            .description("닉네임"),
                        fieldWithPath("memberCode").type(JsonFieldType.STRING)
                            .description("멤버 코드")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                            .description("data에는 아무 값도 반환되지 않습니다")
                    )
                ));
    }

    private void setInvitationStatusWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("팀 초대에 대한 응답",
                    ResourceSnippetParameters.builder()
                        .tag("팀 관련 API")
                        .summary("팀 초대 신청에 대한 응답 API 입니다.")
                        .description(
                            """
                                    팀 초대 신청에 대한 응답을 보냅니다.
                                    accepted=true인 경우 팀원으로 가입됩니다.
                                    accepted=false인 경우 팀원으로 가입되지 않습니다.                           
                                """),
                    pathParameters(
                        parameterWithName("invitationId").description("팀 초대 신청 아이디")
                    ),
                    queryParameters(
                        parameterWithName("accepted").description("초대 승낙 여부")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                            .description("data에는 아무 값도 반환되지 않습니다")
                    )
                ));
    }

    private void getTeamMemberListWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("팀원 조회",
                    ResourceSnippetParameters.builder()
                        .tag("팀 관련 API")
                        .summary("팀원 조회 API 입니다.")
                        .description(
                            """
                                    팀원에 대한 정보를 조회합니다.
                                    현재 속해있는 팀원(isAccepted=true)의 정보와 수락 대기 중인 사용자(isAccepted=false)의 정보를 표시합니다.
                                  
                                """),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.teamCode").type(JsonFieldType.STRING)
                            .description("팀 코드"),
                        fieldWithPath("data.members[].memberId").type(JsonFieldType.NUMBER)
                            .description("팀원 아이디"),
                        fieldWithPath("data.members[].nickname").type(JsonFieldType.STRING)
                            .description("팀원 닉네임"),
                        fieldWithPath("data.members[].memberCode").type(JsonFieldType.STRING)
                            .description("팀원 코드"),
                        fieldWithPath("data.members[].profileImage").type(JsonFieldType.STRING)
                            .description("팀원 프로필 이미지"),
                        fieldWithPath("data.members[].isAccepted").type(JsonFieldType.BOOLEAN)
                            .description("팀 초대 수락 여부")
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

    private void deleteTeamMemberWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("팀원 삭제",
                    ResourceSnippetParameters.builder()
                        .tag("팀 관련 API")
                        .summary("팀원 삭제 API 입니다.")
                        .description(
                            """
                                팀에 소속된 팀원을 삭제합니다.
                                  
                                """),
                    pathParameters(
                        parameterWithName("memberId").description("삭제할 팀원 아이디")
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
