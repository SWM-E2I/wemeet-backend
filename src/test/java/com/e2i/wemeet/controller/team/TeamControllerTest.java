package com.e2i.wemeet.controller.team;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.team_member.TeamMember;
import com.e2i.wemeet.dto.dsl.TeamInformationDto;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.UpdateTeamRequestDto;
import com.e2i.wemeet.dto.response.LeaderResponseDto;
import com.e2i.wemeet.dto.response.team.MyTeamResponseDto;
import com.e2i.wemeet.dto.response.team.TeamDetailResponseDto;
import com.e2i.wemeet.support.config.AbstractControllerUnitTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
import com.e2i.wemeet.support.fixture.TeamFixture;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

class TeamControllerTest extends AbstractControllerUnitTest {

    @DisplayName("팀을 생성할 수 있다.")
    @WithCustomMockUser
    @Test
    void createTeam_Success() throws Exception {
        // given
        CreateTeamRequestDto requestDto = TeamFixture.WOMAN_TEAM.createTeamRequestDto_2_members();
        String dtoToJson = mapper.writeValueAsString(requestDto);
        MockMultipartFile data = new MockMultipartFile("data", "data", "application/json",
            dtoToJson.getBytes(
                StandardCharsets.UTF_8));

        MockMultipartFile images = new MockMultipartFile("images", "test.png", "image/png",
            "test".getBytes());

        doNothing().when(teamService)
            .createTeam(anyLong(), any(CreateTeamRequestDto.class), anyList());

        // when
        ResultActions perform = mockMvc.perform(
            multipart("/v1/team")
                .file(images)
                .file(data)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Create Team Success"),
                jsonPath("$.data").doesNotExist()
            );
        verify(teamService).createTeam(anyLong(), any(CreateTeamRequestDto.class), anyList());

        createTeamWriteRestDocs(perform);
    }

    @DisplayName("팀을 수정할 수 있다.")
    @WithCustomMockUser(role = "MANAGER")
    @Test
    void updateTeam_Success() throws Exception {
        // given
        UpdateTeamRequestDto requestDto = TeamFixture.WOMAN_TEAM.updateTeamRequestDto_2_members();
        String dtoToJson = mapper.writeValueAsString(requestDto);
        MockMultipartFile data = new MockMultipartFile("data", "data", "application/json",
            dtoToJson.getBytes(
                StandardCharsets.UTF_8));

        MockMultipartFile images = new MockMultipartFile("images", "test.png", "image/png",
            "test".getBytes());

        doNothing().when(teamService)
            .updateTeam(anyLong(), any(UpdateTeamRequestDto.class), anyList());

        // when
        ResultActions perform = mockMvc.perform(
            multipart(
                "/v1/team")
                .file(images)
                .file(data)
                .with(csrf())
                .with(
                    request -> {
                        request.setMethod("PUT");
                        return request;
                    })
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Update Team Success"),
                jsonPath("$.data").doesNotExist()
            );
        verify(teamService).updateTeam(anyLong(), any(UpdateTeamRequestDto.class), anyList());

        updateTeamWriteRestDocs(perform);
    }

    @DisplayName("팀을 삭제할 수 있다.")
    @WithCustomMockUser(role = "MANAGER")
    @Test
    void deleteTeam_Success() throws Exception {
        // given
        doNothing().when(teamService).deleteTeam(anyLong());

        // when
        ResultActions perform = mockMvc.perform(delete("/v1/team"));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Delete Team Success"),
                jsonPath("$.data").doesNotExist()
            );
        verify(teamService).deleteTeam(anyLong());

        deleteTeamWriteRestDocs(perform);
    }

    @DisplayName("팀을 조회할 수 있다.")
    @WithCustomMockUser(role = "MANAGER")
    @Test
    void readTeam_Success() throws Exception {
        // given
        MyTeamResponseDto response = MyTeamResponseDto.of(true,
            TeamFixture.WOMAN_TEAM.createMyTeamDetailResponseDto());
        when(teamService.readTeam(anyLong())).thenReturn(response);

        // when
        ResultActions perform = mockMvc.perform(get("/v1/team"));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Get My Team Detail Success"),
                jsonPath("$.data.team.memberNum").value(response.team().memberNum()),
                jsonPath("$.data.team.region").value(response.team().region()),
                jsonPath("$.data.team.drinkRate").value(response.team().drinkRate()),
                jsonPath("$.data.team.drinkWithGame").value(
                    response.team().drinkWithGame()),
                jsonPath("$.data.team.introduction").value(
                    response.team().introduction()),
                jsonPath("$.data.team.chatLink").value(
                    response.team().chatLink())
            );
        verify(teamService).readTeam(anyLong());

        readTeamWriteRestDocs(perform);
    }

    @DisplayName("팀을 조회할 수 있다.")
    @WithCustomMockUser(role = "MANAGER")
    @Test
    void readTeamById() throws Exception {
        // given
        Member kai = KAI.create_with_id(1L);
        List<TeamMember> teamMembers = create_3_man();
        TeamInformationDto teamInformation = TeamInformationDto.of(
            HONGDAE_TEAM_1.create_with_id(kai, teamMembers, 1L));
        LeaderResponseDto leader = LeaderResponseDto.of(kai);
        List<String> imageUrls = List.of("/v1/test1", "/v1/test2", "/v1/test3");

        final TeamDetailResponseDto response = TeamDetailResponseDto.of(teamInformation, leader, imageUrls);
        given(teamService.readByTeamId(anyLong(), anyLong()))
            .willReturn(response);

        // when
        ResultActions perform = mockMvc.perform(
            get("/v1/team/{teamId}", 1));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Get Team Detail Success"),
                jsonPath("$.data.teamId").value(1L),
                jsonPath("$.data.isDeleted").value(false),
                jsonPath("$.data.isLiked").value(true),
                jsonPath("$.data.meetingRequestStatus").value("PENDING"),
                jsonPath("$.data.memberHasTeam").value(true),
                jsonPath("$.data.memberNum").value(4),
                jsonPath("$.data.region").value("HONGDAE"),
                jsonPath("$.data.drinkRate").value("LOW"),
                jsonPath("$.data.drinkWithGame").value("ANY"),
                jsonPath("$.data.additionalActivity").value("CAFE"),
                jsonPath("$.data.introduction").value("안녕하세요! 반가워요! 홍대팀 1입니다!!"),
                jsonPath("$.data.teamImageUrls").isArray(),
                jsonPath("$.data.teamMembers").isArray(),
                jsonPath("$.data.leader.leaderId").value(1L),
                jsonPath("$.data.leader.nickname").value("카이"),
                jsonPath("$.data.leader.mbti").value("INFJ"),
                jsonPath("$.data.leader.collegeName").value("안양대"),
                jsonPath("$.data.leader.collegeType").value("SOCIAL"),
                jsonPath("$.data.leader.admissionYear").value("17"),
                jsonPath("$.data.leader.leaderLowProfileImageUrl").value("/v1/kai"),
                jsonPath("$.data.leader.imageAuth").value(false)
            );
        verify(teamService).readByTeamId(anyLong(), anyLong());

        readByTeamIdWriteRestDocs(perform);
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
                                    팀을 생성합니다.
                                    multipart/form-data 데이터로 보내주어야함 (data는 json!)
                                    part - 'images': 사진 파일
                                    part - 'data': 팀 수정 요청 데이터
                                    
                                    성공 시 AccessToken & RefreshToken 재발급되어 Header에 함께 전송됨
                                    사용자 권한 변경 USER -> MANAGER
                                    
                                    "data" : {
                                    	"region" : String, // HONGDAE, GANGNAM, SINCHON, GUNDAE 중 하나
                                    	"drinkRate" : String, // ZERO, LOW, MIDDLE, HIGH
                                    	"drinkWithGame" : String, // ANY, MASTER, BEGINNER, HATER 중 하나
                                    	"additionalActivity" : String, // nullable // SHOW, SPORTS, UNIQUE_EXPERIENCE, OUTDOOR_ACTIVITY, CAFE 중 하나
                                    	"introduction" : String, // 150 제한\s
                                    	"members" : [ // 사이즈 최소 1, 최대 3
                                    							{
                                    								"collegeInfo": {
                                    									"collegeCode": String, // College CODE 값을 전달 ex) CE-001\s
                                    									"collegeType": String, // ETC, SOCIAL, ENGINEERING, ARTS, EDUCATION, MEDICINE 중 하나
                                    									"admissionYear" : String,\s
                                    								},
                                    								"mbti" : String // 잘 모를 경우 "XXXX"
                                    							},\s
                                    							{
                                    								"collegeInfo": {
                                    									"collegeCode": String, // College CODE 값을 전달 ex) CE-001\s
                                    									"collegeType": String, // ETC, SOCIAL, ENGINEERING, ARTS, EDUCATION, MEDICINE 중 하나
                                    									"admissionYear" : String,\s
                                    								},
                                    								"mbti" : String // 잘 모를 경우 "XXXX"
                                    							}	
                                    					],
                                    	"chatLink" : String, //not null
                                    },
                                    
                                    "images": File[], // 최소 1장, 최대 10장
                                """)
                        .requestFields(
                            fieldWithPath("region").type(JsonFieldType.STRING).description("선호 지역"),
                            fieldWithPath("drinkRate").type(JsonFieldType.STRING)
                                .description("음주 수치"),
                            fieldWithPath("drinkWithGame").type(JsonFieldType.STRING)
                                .description("술게임 여부"),
                            fieldWithPath("additionalActivity").type(JsonFieldType.STRING).optional()
                                .description("추가 활동"),
                            fieldWithPath("introduction").type(JsonFieldType.STRING)
                                .description("팀 소개"),
                            fieldWithPath("chatLink").type(JsonFieldType.STRING)
                                .description("카카오톡 오픈 채팅방 링크"),
                            fieldWithPath("members").type(JsonFieldType.ARRAY)
                                .description("팀원 정보"),
                            fieldWithPath("members[].collegeInfo.collegeCode").type(
                                    JsonFieldType.STRING)
                                .description("대학 코드"),
                            fieldWithPath("members[].collegeInfo.collegeType").type(
                                    JsonFieldType.STRING)
                                .description("팀원 학과 타입"),
                            fieldWithPath("members[].collegeInfo.admissionYear").type(
                                    JsonFieldType.STRING)
                                .description("팀원 학번"),
                            fieldWithPath("members[].mbti").type(JsonFieldType.STRING)
                                .description("팀원 MBTI")
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
                                    나의 팀 정보를 수정합니다.
                                    multipart/form-data 데이터로 보내주어야함 (data는 json!)
                                    part - 'images': 사진 파일
                                    part - 'data': 팀 수정 요청 데이터
                                    
                                    :: 팀 생성과 동일!
                                    
                                    "data" : {
                                    	"region" : String, // HONGDAE, GANGNAM, SINCHON, GUNDAE 중 하나
                                    	"drinkRate" : String, // ZERO, LOW, MIDDLE, HIGH
                                    	"drinkWithGame" : String, // ANY, MASTER, BEGINNER, HATER 중 하나
                                    	"additionalActivity" : String, // nullable // SHOW, SPORTS, UNIQUE_EXPERIENCE, OUTDOOR_ACTIVITY, CAFE 중 하나
                                    	"introduction" : String, // 150 제한\s
                                    	"members" : [ // 사이즈 최소 1, 최대 3
                                    							{
                                    								"collegeInfo": {
                                    									"collegeCode": String, // College CODE 값을 전달 ex) CE-001\s
                                    									"collegeType": String, // ETC, SOCIAL, ENGINEERING, ARTS, EDUCATION, MEDICINE 중 하나
                                    									"admissionYear" : String,\s
                                    								},
                                    								"mbti" : String // 잘 모를 경우 "XXXX"
                                    							},\s
                                    							{
                                    								"collegeInfo": {
                                    									"collegeCode": String, // College CODE 값을 전달 ex) CE-001\s
                                    									"collegeType": String, // ETC, SOCIAL, ENGINEERING, ARTS, EDUCATION, MEDICINE 중 하나
                                    									"admissionYear" : String,\s
                                    								},
                                    								"mbti" : String // 잘 모를 경우 "XXXX"
                                    							}	
                                    					],
                                    	"chatLink" : String, //not null
                                    },
                                    
                                    "images": File[], // 최소 1장, 최대 10장
                                """)
                        .requestFields(
                            fieldWithPath("images").description("사진 파일"),
                            fieldWithPath("data.region").type(JsonFieldType.STRING).description("선호 지역"),
                            fieldWithPath("data.drinkRate").type(JsonFieldType.STRING)
                                .description("음주 수치"),
                            fieldWithPath("data.drinkWithGame").type(JsonFieldType.STRING)
                                .description("술게임 여부"),
                            fieldWithPath("data.additionalActivity").type(JsonFieldType.STRING).optional()
                                .description("추가 활동"),
                            fieldWithPath("data.introduction").type(JsonFieldType.STRING)
                                .description("팀 소개"),
                            fieldWithPath("data.chatLink").type(JsonFieldType.STRING)
                                .description("카카오톡 오픈 채팅방 링크"),
                            fieldWithPath("data.members").type(JsonFieldType.ARRAY)
                                .description("팀원 정보"),
                            fieldWithPath("data.members[].collegeInfo.collegeCode").type(
                                    JsonFieldType.STRING)
                                .description("대학 코드"),
                            fieldWithPath("data.members[].collegeInfo.collegeType").type(
                                    JsonFieldType.STRING)
                                .description("팀원 학과 타입"),
                            fieldWithPath("data.members[].collegeInfo.admissionYear").type(
                                    JsonFieldType.STRING)
                                .description("팀원 학번"),
                            fieldWithPath("data.members[].mbti").type(JsonFieldType.STRING)
                                .description("팀원 MBTI")
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
                        .summary("내 팀 정보를 조회합니다.")
                        .description(
                            """
                                    내 팀 정보를 조회합니다.
                                """),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.hasTeam").type(JsonFieldType.BOOLEAN)
                            .description("팀 소속 여부"),
                        fieldWithPath("data.team.memberNum").type(JsonFieldType.NUMBER)
                            .description("팀 인원수"),
                        fieldWithPath("data.team.region").type(JsonFieldType.STRING)
                            .description("선호 지역"),
                        fieldWithPath("data.team.drinkRate").type(JsonFieldType.STRING)
                            .description("음주 수치"),
                        fieldWithPath("data.team.drinkWithGame").type(JsonFieldType.STRING)
                            .description("술게임 여부"),
                        fieldWithPath("data.team.additionalActivity").type(JsonFieldType.STRING)
                            .optional()
                            .description("추가 활동"),
                        fieldWithPath("data.team.introduction").type(JsonFieldType.STRING)
                            .description("팀 소개"),
                        fieldWithPath("data.team.chatLink").type(JsonFieldType.STRING)
                            .description("카카오톡 오픈 채팅방 링크"),
                        fieldWithPath("data.team.members").type(JsonFieldType.ARRAY)
                            .description("팀원 정보"),
                        fieldWithPath("data.team.members[].college").type(
                                JsonFieldType.STRING)
                            .description("팀원 대학교"),
                        fieldWithPath("data.team.members[]..collegeType").type(
                                JsonFieldType.STRING)
                            .description("팀원 학과 타입"),
                        fieldWithPath("data.team.members[].admissionYear").type(
                                JsonFieldType.STRING)
                            .description("팀원 학번"),
                        fieldWithPath("data.team.members[].mbti").type(JsonFieldType.STRING)
                            .description("팀원 MBTI"),
                        fieldWithPath("data.team.images").type(JsonFieldType.ARRAY)
                            .description("팀 사진 정보"),
                        fieldWithPath("data.team.images[].url").type(JsonFieldType.STRING)
                            .description("팀 사진 URL"),
                        fieldWithPath("data.team.profileImageURL").type(JsonFieldType.STRING)
                            .description("팀장 프로필 사진"),
                        fieldWithPath("data.team.leader.nickname").type(JsonFieldType.STRING)
                            .description("팀장 닉네임"),
                        fieldWithPath("data.team.leader.mbti").type(JsonFieldType.STRING)
                            .description("팀장 MBTI"),
                        fieldWithPath("data.team.leader.college").type(JsonFieldType.STRING)
                            .description("팀장 대학교 정보")
                    )
                ));
    }

    private void readByTeamIdWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("팀 상세 정보 조회",
                    ResourceSnippetParameters.builder()
                        .tag("팀 관련 API")
                        .summary("팀 상세 정보 조회 API 입니다.")
                        .description(
                            """
                                    팀 ID로 상세정보를 조회합니다.
                                """),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.teamId").type(JsonFieldType.NUMBER).description("팀 ID"),
                        fieldWithPath("data.isDeleted").type(JsonFieldType.BOOLEAN)
                            .description("팀 삭제 여부"),
                        fieldWithPath("data.isLiked").type(JsonFieldType.BOOLEAN)
                            .description("팀 좋아요 여부"),
                        fieldWithPath("data.meetingRequestStatus").type(JsonFieldType.STRING)
                            .description(
                                """
                                        미팅 요청 상태
                                        ACCEPT (미팅 성사됨),
                                        PENDING (요청 대기중),
                                        REJECT (요청 거절당함),
                                        EXPIRED (요청 만료됨),
                                        null (요청 내역 없음) 중 하나
                                    """),
                        fieldWithPath("data.memberHasTeam").type(JsonFieldType.BOOLEAN)
                            .description("요청을 한 유저가 팀에 속해있는지 여부를 반환합니다."),
                        fieldWithPath("data.memberNum").type(JsonFieldType.NUMBER)
                            .description("팀 인원수"),
                        fieldWithPath("data.region").type(JsonFieldType.STRING)
                            .description("선호 지역"),
                        fieldWithPath("data.drinkRate").type(JsonFieldType.STRING)
                            .description("음주 수치"),
                        fieldWithPath("data.drinkWithGame").type(JsonFieldType.STRING)
                            .description("술게임 여부"),
                        fieldWithPath("data.additionalActivity").type(JsonFieldType.STRING)
                            .description("취미 및 관심사"),
                        fieldWithPath("data.introduction").type(JsonFieldType.STRING)
                            .description("팀 소개"),
                        fieldWithPath("data.teamImageUrls").type(JsonFieldType.ARRAY)
                            .description("팀 사진 URL"),
                        fieldWithPath("data.teamMembers[].college").type(JsonFieldType.STRING)
                            .description("대학교 명"),
                        fieldWithPath("data.teamMembers[].collegeType").type(JsonFieldType.STRING)
                            .description("학과"),
                        fieldWithPath("data.teamMembers[].admissionYear").type(JsonFieldType.STRING)
                            .description("학번"),
                        fieldWithPath("data.teamMembers[].mbti").type(JsonFieldType.STRING)
                            .description("MBTI"),
                        fieldWithPath("data.leader.leaderId").type(JsonFieldType.NUMBER)
                            .description("팀장 ID"),
                        fieldWithPath("data.leader.nickname").type(JsonFieldType.STRING)
                            .description("팀장 닉네임"),
                        fieldWithPath("data.leader.mbti").type(JsonFieldType.STRING)
                            .description("팀장 MBTI"),
                        fieldWithPath("data.leader.collegeName").type(JsonFieldType.STRING)
                            .description("팀장 대학교"),
                        fieldWithPath("data.leader.collegeType").type(JsonFieldType.STRING)
                            .description("팀장 학과"),
                        fieldWithPath("data.leader.admissionYear").type(JsonFieldType.STRING)
                            .description("팀장 학번"),
                        fieldWithPath("data.leader.leaderLowProfileImageUrl").type(
                                JsonFieldType.STRING)
                            .description("팀장 프로필 사진"),
                        fieldWithPath("data.leader.imageAuth").type(JsonFieldType.BOOLEAN)
                            .description("팀장 프로필 사진 인증 여부")
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
                                    성공 시 AccessToken & RefershToken 재발급되어 Header에 함께 전송됨
                                    사용자 권한 변경 MANAGER -> USER
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
