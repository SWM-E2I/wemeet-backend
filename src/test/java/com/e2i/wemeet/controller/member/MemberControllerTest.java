package com.e2i.wemeet.controller.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
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

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Gender;
import com.e2i.wemeet.domain.member.Mbti;
import com.e2i.wemeet.dto.request.member.CollegeInfoRequestDto;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberPreferenceRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberRequestDto;
import com.e2i.wemeet.dto.request.member.PreferenceRequestDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import com.e2i.wemeet.dto.response.member.MemberPreferenceResponseDto;
import com.e2i.wemeet.service.code.CodeService;
import com.e2i.wemeet.service.member.MemberService;
import com.e2i.wemeet.support.config.AbstractUnitTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
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

@WebMvcTest(MemberController.class)
class MemberControllerTest extends AbstractUnitTest {

    @MockBean
    private MemberService memberService;

    @MockBean
    private CodeService codeService;

    @DisplayName("회원 생성 성공")
    @WithCustomMockUser
    @Test
    void createMember_Success() throws Exception {
        // given
        CreateMemberRequestDto request = CreateMemberRequestDto.builder()
            .nickname("nickname")
            .gender("MALE")
            .phoneNumber("+821012341234")
            .collegeInfo(CollegeInfoRequestDto.builder()
                .college("collegeName")
                .collegeType("majorName")
                .admissionYear("18")
                .build())
            .preference(PreferenceRequestDto.builder()
                .startPreferenceAdmissionYear("21")
                .endPreferenceAdmissionYear("23")
                .sameCollegeState("0")
                .drinkingOption("1")
                .isAvoidedFriends(true)
                .preferenceMbti("XXXX")
                .build())
            .preferenceMeetingTypeList(List.of())
            .memberInterestList(List.of())
            .mbti("ESTJ")
            .build();

        when(codeService.findCode(anyString())).thenReturn(Code.builder().build());
        when(memberService.createMember(any(CreateMemberRequestDto.class), anyList(), anyList()))
            .thenReturn(1L);

        // when
        ResultActions perform = mockMvc.perform(post("/v1/member")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(request)));

        perform
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Create Member Success"))
            .andExpect(jsonPath("$.data").exists());

        // then
        verify(memberService, times(1)).createMember(request, List.of(), List.of());

        createMemberWriteRestDocs(perform);
    }

    @DisplayName("회원 상세 정보 조회 성공")
    @WithCustomMockUser
    @Test
    void getMemberDetail_Success() throws Exception {
        // given
        MemberDetailResponseDto response = MemberDetailResponseDto.builder()
            .nickname("nickname")
            .gender(Gender.FEMALE)
            .mbti(Mbti.ENFP)
            .college("college")
            .collegeType("collegeType")
            .admissionYear("18")
            .introduction("hi")
            .profileImageList(List.of())
            .memberInterestList(List.of())
            .build();
        when(memberService.getMemberDetail(anyLong())).thenReturn(response);

        // when
        ResultActions perform = mockMvc.perform(get("/v1/member"));

        perform
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Get Member-detail Success"))
            .andExpect(jsonPath("$.data").exists());

        // then
        verify(memberService, times(1)).getMemberDetail(1L);

        getMemberDetailWriteRestDocs(perform);
    }

    @DisplayName("회원 정보 조회 성공")
    @WithCustomMockUser
    @Test
    void getMemberInfo_Success() throws Exception {
        // given
        MemberInfoResponseDto response = MemberInfoResponseDto.builder()
            .nickname("nickname")
            .memberCode("#1234")
            .profileImage("profileImage-key")
            .univAuth(true)
            .imageAuth(false)
            .build();
        when(memberService.getMemberInfo(anyLong())).thenReturn(response);

        // when
        ResultActions perform = mockMvc.perform(get("/v1/member/info"));

        perform
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Get Member-Info Success"))
            .andExpect(jsonPath("$.data").exists());

        // then
        verify(memberService, times(1)).getMemberInfo(1L);

        getMemberInfoWriteRestDocs(perform);
    }

    @DisplayName("선호 상대 정보 조회 성공")
    @WithCustomMockUser
    @Test
    void getMemberPrefer_Success() throws Exception {
        // given
        MemberPreferenceResponseDto response = MemberPreferenceResponseDto.builder()
            .sameCollegeState("0")
            .drinkingOption("1")
            .isAvoidedFriends(true)
            .startPreferenceAdmissionYear("19")
            .endPreferenceAdmissionYear("21")
            .preferenceMbti("XXXX")
            .preferenceMeetingTypeList(List.of())
            .build();
        when(memberService.getMemberPrefer(anyLong())).thenReturn(response);

        // when
        ResultActions perform = mockMvc.perform(get("/v1/member/prefer"));

        perform
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Get Member-Prefer Success"))
            .andExpect(jsonPath("$.data").exists());

        // then
        verify(memberService, times(1)).getMemberPrefer(1L);

        getMemberPreferWriteRestDocs(perform);
    }

    @DisplayName("회원 정보 수정 성공")
    @WithCustomMockUser
    @Test
    void modifyMember_Success() throws Exception {
        // given
        ModifyMemberRequestDto request = ModifyMemberRequestDto.builder()
            .nickname("test_nickname")
            .introduction("hi")
            .mbti("ESTJ")
            .memberInterestList(List.of())
            .build();

        when(codeService.findCode(anyString())).thenReturn(Code.builder().build());

        // when
        ResultActions perform = mockMvc.perform(put("/v1/member")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(request)));

        perform
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Modify Member Success"))
            .andExpect(jsonPath("$.data").doesNotExist());

        // then
        verify(memberService, times(1)).modifyMember(1L, request, List.of());
        modifyMemberWriteRestDocs(perform);
    }

    @DisplayName("선호 상대 정보 수정 성공")
    @WithCustomMockUser
    @Test
    void modifyMemberPrefer_Success() throws Exception {
        // given
        ModifyMemberPreferenceRequestDto request = ModifyMemberPreferenceRequestDto
            .builder()
            .drinkingOption("0")
            .startPreferenceAdmissionYear("21")
            .endPreferenceAdmissionYear("23")
            .sameCollegeState("0")
            .isAvoidedFriends(true)
            .preferenceMbti("XXXX")
            .preferenceMeetingTypeList(List.of())
            .build();

        when(codeService.findCode(anyString())).thenReturn(Code.builder().build());

        // when
        ResultActions perform = mockMvc.perform(put("/v1/member/prefer")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(request)));

        perform
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Modify Member Preference Success"))
            .andExpect(jsonPath("$.data").doesNotExist());

        // then
        verify(memberService, times(1)).modifyPreference(1L, request, List.of());

        modifyMemberPreferWriteRestDocs(perform);
    }

    private void createMemberWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("회원가입",
                    ResourceSnippetParameters.builder()
                        .tag("회원 가입")
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
                        fieldWithPath("collegeInfo.college").type(JsonFieldType.STRING)
                            .description("대학교"),
                        fieldWithPath("collegeInfo.collegeType").type(JsonFieldType.STRING)
                            .description("대학교 유형"),
                        fieldWithPath("collegeInfo.admissionYear").type(JsonFieldType.STRING)
                            .description("학번"),
                        fieldWithPath("mbti").type(JsonFieldType.STRING).description("본인 MBTI"),
                        fieldWithPath("preference.startPreferenceAdmissionYear").type(
                                JsonFieldType.STRING)
                            .description("선호 시작 학번"),
                        fieldWithPath("preference.endPreferenceAdmissionYear").type(
                                JsonFieldType.STRING)
                            .description("선호 마지막 학번"),
                        fieldWithPath("preference.sameCollegeState").type(JsonFieldType.STRING)
                            .description("같은 학교 선호 여부"),
                        fieldWithPath("preference.drinkingOption").type(JsonFieldType.STRING)
                            .description("술자리 여부"),
                        fieldWithPath("preference.isAvoidedFriends").type(JsonFieldType.BOOLEAN)
                            .description("아는 사람 피하기 여부"),
                        fieldWithPath("preference.preferenceMbti").type(JsonFieldType.STRING)
                            .description("선호 MBTI"),
                        fieldWithPath("preferenceMeetingTypeList").type(
                            JsonFieldType.ARRAY).description("선호 미팅 유형"),
                        fieldWithPath("introduction").type(JsonFieldType.STRING).optional()
                            .description("자기 소개"),
                        fieldWithPath("memberInterestList").type(JsonFieldType.ARRAY).optional()
                            .description("취미 및 관심사")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NUMBER)
                            .description("생성된 회원의 memberId")
                    )
                ));
    }

    private void getMemberDetailWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("회원 상세 정보 조회",
                    ResourceSnippetParameters.builder()
                        .tag("회원 상세 정보 조회")
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
                            .description("대학교"),
                        fieldWithPath("data.collegeType").type(JsonFieldType.STRING)
                            .description("대학교 유형"),
                        fieldWithPath("data.admissionYear").type(JsonFieldType.STRING)
                            .description("학번"),
                        fieldWithPath("data.introduction").type(JsonFieldType.STRING)
                            .description("자기 소개"),
                        fieldWithPath("data.profileImageList").type(
                            JsonFieldType.ARRAY).description("프로필 사진 리스트"),
                        fieldWithPath("data.memberInterestList").type(JsonFieldType.ARRAY)
                            .description("취미 및 관심사")
                    )
                ));
    }

    private void getMemberInfoWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("회원 정보 조회",
                    ResourceSnippetParameters.builder()
                        .tag("회원 정보 조회")
                        .summary("회원 정보 조회 API 입니다.")
                        .description(
                            """
                                    회원에 대한 정보를 조회합니다.
                                """),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                            .description("닉네임"),
                        fieldWithPath("data.memberCode").type(JsonFieldType.STRING)
                            .description("회원 코드"),
                        fieldWithPath("data.profileImage").type(JsonFieldType.STRING)
                            .description("프로필 이미지"),
                        fieldWithPath("data.univAuth").type(JsonFieldType.BOOLEAN)
                            .description("대학 인증 여부"),
                        fieldWithPath("data.imageAuth").type(JsonFieldType.BOOLEAN)
                            .description("사진 인증 여부")
                    )
                ));
    }

    private void getMemberPreferWriteRestDocs(ResultActions perform) throws Exception {

        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("회원 선호 정보 조회",
                    ResourceSnippetParameters.builder()
                        .tag("회원 선호 정보 조회")
                        .summary("회원 선호 정보 조회 API 입니다.")
                        .description(
                            """
                                    회원이 선호하는 상대에 대한 정보를 조회합니다.
                                """),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.sameCollegeState").type(JsonFieldType.STRING)
                            .description("같은 학교 선호 여부"),
                        fieldWithPath("data.drinkingOption").type(JsonFieldType.STRING)
                            .description("술자리 선호 여부"),
                        fieldWithPath("data.isAvoidedFriends").type(JsonFieldType.BOOLEAN)
                            .description("아는 사람 피하기 여부"),
                        fieldWithPath("data.startPreferenceAdmissionYear").type(
                                JsonFieldType.STRING)
                            .description("선호 시작 학번"),
                        fieldWithPath("data.endPreferenceAdmissionYear").type(JsonFieldType.STRING)
                            .description("선호 끝 학번"),
                        fieldWithPath("data.preferenceMbti").type(JsonFieldType.STRING)
                            .description("선호 MBTI"),
                        fieldWithPath("data.preferenceMeetingTypeList").type(JsonFieldType.ARRAY)
                            .description("선호하는 미팅 타입")
                    )
                ));
    }

    private void modifyMemberWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("회원 상세 정보 수정",
                    ResourceSnippetParameters.builder()
                        .tag("회원 상세 정보 수정")
                        .summary("회원 상세 정보 수정 API 입니다.")
                        .description(
                            """
                                    회원의 상세 정보를 수정합니다.
                                """),
                    requestFields(
                        fieldWithPath("nickname").type(JsonFieldType.STRING)
                            .description("닉네임"),
                        fieldWithPath("mbti").type(JsonFieldType.STRING)
                            .description("본인 MBTI"),
                        fieldWithPath("introduction").type(JsonFieldType.STRING)
                            .description("자기 소개"),
                        fieldWithPath("memberInterestList").type(
                                JsonFieldType.ARRAY)
                            .description("취미 및 관심사 (없으면 빈 값)")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                            .description("data에는 아무 값도 반환되지 않습니다")
                    )));
    }

    private void modifyMemberPreferWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("회원 선호 정보 수정",
                    ResourceSnippetParameters.builder()
                        .tag("회원 선호 정보 수정")
                        .summary("회원 선호 정보 수정 API 입니다.")
                        .description(
                            """
                                    회원이 선호하는 상대에 대한 정보를 수정합니다.
                                """),
                    requestFields(
                        fieldWithPath("sameCollegeState").type(JsonFieldType.STRING)
                            .description("같은 학교 선호 여부"),
                        fieldWithPath("drinkingOption").type(JsonFieldType.STRING)
                            .description("술자리 선호 여부"),
                        fieldWithPath("isAvoidedFriends").type(JsonFieldType.BOOLEAN)
                            .description("아는 사람 피하기 여부"),
                        fieldWithPath("startPreferenceAdmissionYear").type(
                                JsonFieldType.STRING)
                            .description("선호 시작 학번"),
                        fieldWithPath("endPreferenceAdmissionYear").type(JsonFieldType.STRING)
                            .description("선호 끝 학번"),
                        fieldWithPath("preferenceMbti").type(JsonFieldType.STRING)
                            .description("선호 MBTI"),
                        fieldWithPath("preferenceMeetingTypeList").type(JsonFieldType.ARRAY)
                            .description("선호하는 미팅 타입")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                            .description("data에는 아무 값도 반환되지 않습니다")
                    )));
    }
}