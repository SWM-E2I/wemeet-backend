package com.e2i.wemeet.controller.member;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.UpdateMemberRequestDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberRoleResponseDto;
import com.e2i.wemeet.security.model.MemberPrincipal;
import com.e2i.wemeet.service.member.MemberService;
import com.e2i.wemeet.service.member_image.MemberImageService;
import com.e2i.wemeet.support.config.AbstractControllerUnitTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(MemberController.class)
class MemberControllerTest extends AbstractControllerUnitTest {

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberImageService memberImageService;

    @DisplayName("회원 가입을 할 수 있다.")
    @WithCustomMockUser
    @Test
    void createMember_Success() throws Exception {
        // given
        CreateMemberRequestDto request = KAI.createMemberRequestDto();

        when(memberService.createMember(any(CreateMemberRequestDto.class)))
            .thenReturn(KAI.create_with_id(1L).getMemberId());

        // when
        ResultActions perform = mockMvc.perform(post("/v1/member")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(request)));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Create Member Success"),
                jsonPath("$.data").doesNotExist()
            );
        verify(memberService).createMember(any(CreateMemberRequestDto.class));

        createMemberWriteRestDocs(perform);
    }

    @DisplayName("회원 상세 정보를 조회할 수 있다.")
    @WithCustomMockUser
    @Test
    void getMemberDetail_Success() throws Exception {
        // given
        MemberDetailResponseDto response = KAI.createMemberDetailResponseDto();
        when(memberService.readMemberDetail(anyLong())).thenReturn(response);

        // when
        ResultActions perform = mockMvc.perform(get("/v1/member"));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Get Member-detail Success"),
                jsonPath("$.data.nickname").value(KAI.getNickname()),
                jsonPath("$.data.gender").value(KAI.getGender().name()),
                jsonPath("$.data.mbti").value(KAI.getMbti().name()),
                jsonPath("$.data.college").value(KAI.getCollegeInfo().getCollegeCode().getCodeValue()),
                jsonPath("$.data.collegeType").value(KAI.getCollegeInfo().getCollegeType().getDescription()),
                jsonPath("$.data.admissionYear").value(KAI.getCollegeInfo().getAdmissionYear()),
                jsonPath("$.data.profileImage.basicUrl").value(KAI.getBasicUrl()),
                jsonPath("$.data.profileImage.lowUrl").value(KAI.getLowUrl())
            );
        verify(memberService).readMemberDetail(1L);

        getMemberDetailWriteRestDocs(perform);
    }

    @DisplayName("회원 정보를 수정할 수 있다.")
    @WithCustomMockUser
    @Test
    void modifyMember_Success() throws Exception {
        // given
        UpdateMemberRequestDto request = KAI.createUpdateMemberRequestDto("기우미우", "ESTJ");
        doNothing().when(memberService).updateMember(any(Long.class), any(UpdateMemberRequestDto.class));

        // when
        ResultActions perform = mockMvc.perform(patch("/v1/member")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(request)));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Update Member Success"),
                jsonPath("$.data").doesNotExist()
            );
        verify(memberService).updateMember(any(Long.class), any(UpdateMemberRequestDto.class));

        updateMemberWriteRestDocs(perform);
    }

    @DisplayName("회원 역할 정보를 조회할 수 있다.")
    @WithCustomMockUser
    @Test
    void getMemberRole_Success() throws Exception {
        // given
        MemberPrincipal memberPrincipal = new MemberPrincipal(1L, "MANAGER");
        MemberRoleResponseDto response = MemberRoleResponseDto.of(memberPrincipal);

        when(memberService.readMemberRole(any(MemberPrincipal.class)))
            .thenReturn(response);

        // when
        ResultActions perform = mockMvc.perform(get("/v1/member/role"));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Get Member Role Success"),
                jsonPath("$.data.isManager").value(true),
                jsonPath("$.data.hasTeam").value(true)
            );
        verify(memberService).readMemberRole(any(MemberPrincipal.class));

        getMemberRoleWriteRestDocs(perform);
    }

    @DisplayName("회원 탈퇴를 할 수 있다.")
    @WithCustomMockUser
    @Test
    void delete() throws Exception {
        // given
        doNothing().when(memberService).deleteMember(any(Long.class), any(LocalDateTime.class));

        // when
        ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.delete("/v1/member"));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Delete Member Success"),
                jsonPath("$.data").doesNotExist()
            );
        verify(memberService).deleteMember(any(Long.class), any(LocalDateTime.class));

        deleteMemberWriteRestDocs(perform);
    }

    private void deleteMemberWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("회원 탈퇴",
                    ResourceSnippetParameters.builder()
                        .tag("회원 탈퇴 API")
                        .summary("회원 탈퇴 API 입니다.")
                        .description(
                            """
                                    회원 탈퇴를 수행합니다. (deleteAt)
                                """),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                            .description("data에는 아무 값도 반환되지 않습니다")
                    )));
    }

    @DisplayName("회원 프로필 이미지를 등록할 수 있다.")
    @WithCustomMockUser
    @Test
    void uploadProfileImage() throws Exception {
        // given
        doNothing().when(memberImageService)
            .uploadProfileImage(anyLong(), any(MultipartFile.class));

        // when
        ResultActions perform = mockMvc.perform(
            multipart("/v1/member/profile-image").file("file", "test".getBytes())
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        perform
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Upload Profile Image Success"))
            .andExpect(jsonPath("$.data").doesNotExist());

        // then
        verify(memberImageService).uploadProfileImage(anyLong(), any(MultipartFile.class));

        uploadProfileImageWriteRestDocs(perform);
    }

    private void createMemberWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("회원가입",
                    ResourceSnippetParameters.builder()
                        .tag("회원 관련 API")
                        .summary("회원가입 API 입니다.")
                        .description(
                            """
                                    회원 정보를 통해 회원가입을 진행합니다.
                                """),
                    requestFields(
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                        fieldWithPath("gender").type(JsonFieldType.STRING).description("성별"),
                        fieldWithPath("phoneNumber").type(JsonFieldType.STRING)
                            .description("핸드폰 번호"),
                        fieldWithPath("collegeInfo.collegeCode").type(JsonFieldType.STRING)
                            .description("대학교 코드 (CE-001)"),
                        fieldWithPath("collegeInfo.collegeType").type(JsonFieldType.STRING)
                            .description("학과 (ENGINEERING)"),
                        fieldWithPath("collegeInfo.admissionYear").type(JsonFieldType.STRING)
                            .description("학번 (17)"),
                        fieldWithPath("mbti").type(JsonFieldType.STRING).description("본인 MBTI")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                            .description("data에는 아무 값도 반환되지 않습니다")
                    )
                ));
    }

    private void getMemberDetailWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("회원 상세 정보 조회",
                    ResourceSnippetParameters.builder()
                        .tag("회원 관련 API")
                        .summary("회원 상세 정보 조회 API 입니다.")
                        .description(
                            """
                                    회원에 대한 상세 정보를 조회합니다.
                                """),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                            .description("닉네임"),
                        fieldWithPath("data.gender").type(JsonFieldType.STRING).description("성별"),
                        fieldWithPath("data.mbti").type(JsonFieldType.STRING)
                            .description("본인 MBTI"),
                        fieldWithPath("data.college").type(JsonFieldType.STRING)
                            .description("대학교명"),
                        fieldWithPath("data.collegeType").type(JsonFieldType.STRING)
                            .description("학과 정보"),
                        fieldWithPath("data.admissionYear").type(JsonFieldType.STRING)
                            .description("학번"),
                        fieldWithPath("data.profileImage.basicUrl").type(JsonFieldType.STRING)
                            .description("회원 개인 프로필 사진 원본"),
                        fieldWithPath("data.profileImage.lowUrl").type(JsonFieldType.STRING)
                            .description("회원 개인 프로필 사진 저해상도")
                    )
                ));
    }

    private void updateMemberWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("회원 상세 정보 수정",
                    ResourceSnippetParameters.builder()
                        .tag("회원 관련 API")
                        .summary("회원 상세 정보 수정 API 입니다.")
                        .description(
                            """
                                    회원의 상세 정보를 수정합니다.
                                """),
                    requestFields(
                        fieldWithPath("nickname").type(JsonFieldType.STRING)
                            .description("닉네임"),
                        fieldWithPath("mbti").type(JsonFieldType.STRING)
                            .description("본인 MBTI")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                            .description("data에는 아무 값도 반환되지 않습니다")
                    )));
    }

    private void getMemberRoleWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("회원 Role 조회",
                    ResourceSnippetParameters.builder()
                        .tag("회원 관련 API")
                        .summary("회원 Role 정보 조회 API 입니다.")
                        .description(
                            """
                                    회원의 Role 정보를 조회합니다.
                                    팀장 여부와 팀 소속 여부를 조회할 수 있습니다.
                                """),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.isManager").type(JsonFieldType.BOOLEAN)
                            .description("팀장 여부"),
                        fieldWithPath("data.hasTeam").type(JsonFieldType.BOOLEAN)
                            .description("팀 소속 여부")
                    )
                ));
    }

    void uploadProfileImageWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("회원 프로필 사진 등록",
                    ResourceSnippetParameters.builder()
                        .tag("회원 관련 API")
                        .summary("회원 프로필 등록 API 입니다.")
                        .description(
                            """
                                   회원의 프로필 사진을 등록합니다.
                                """),
                    requestParts(
                        partWithName("file").description("프로필 이미지 파일")),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                            .description("data에는 아무 값도 반환되지 않습니다")
                    )));
    }
}
