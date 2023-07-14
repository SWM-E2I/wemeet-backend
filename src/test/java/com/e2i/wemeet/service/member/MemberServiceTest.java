package com.e2i.wemeet.service.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.memberinterest.MemberInterest;
import com.e2i.wemeet.domain.memberinterest.MemberInterestRepository;
import com.e2i.wemeet.domain.memberpreferencemeetingtype.MemberPreferenceMeetingType;
import com.e2i.wemeet.domain.memberpreferencemeetingtype.MemberPreferenceMeetingTypeRepository;
import com.e2i.wemeet.domain.profileimage.ProfileImage;
import com.e2i.wemeet.domain.profileimage.ProfileImageRepository;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberPreferenceRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberRequestDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import com.e2i.wemeet.dto.response.member.MemberPreferenceResponseDto;
import com.e2i.wemeet.dto.response.member.RoleResponseDto;
import com.e2i.wemeet.exception.badrequest.DuplicatedPhoneNumberException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.support.fixture.MemberFixture;
import com.e2i.wemeet.support.fixture.PreferenceFixture;
import com.e2i.wemeet.support.fixture.ProfileImageFixture;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberInterestRepository memberInterestRepository;

    @Mock
    private MemberPreferenceMeetingTypeRepository memberPreferenceMeetingTypeRepository;

    @Mock
    private ProfileImageRepository profileImageRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private static final Long memberId = 1L;
    private static final Member member = MemberFixture.KAI.create();
    private static final List<Code> interestCode = new ArrayList<>();
    private static final List<Code> preferenceMeetingTypeCode = new ArrayList<>();

    @DisplayName("회원 생성에 성공한다.")
    @Test
    void createMember_Success() {
        // given
        CreateMemberRequestDto requestDto = MemberFixture.KAI.createMemberRequestDto();

        when(memberRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(memberInterestRepository.saveAll(anyList())).thenReturn(new ArrayList<>());
        when(memberPreferenceMeetingTypeRepository.saveAll(anyList())).thenReturn(
            new ArrayList<>());

        // when
        memberService.createMember(requestDto, interestCode,
            preferenceMeetingTypeCode);

        // then
        verify(memberRepository).findByPhoneNumber(anyString());
        verify(memberRepository).save(any(Member.class));
        verify(memberInterestRepository).saveAll(anyList());
        verify(memberPreferenceMeetingTypeRepository).saveAll(anyList());
    }

    @DisplayName("중복된 휴대폰 번호를 가진 회원이 있을 경우 DuplicatedPhoneNumberException이 발생한다.")
    @Test
    void createMember_DuplicatedPhoneNumber() {
        // given
        CreateMemberRequestDto requestDto = MemberFixture.KAI.createMemberRequestDto();

        when(memberRepository.findByPhoneNumber(anyString())).thenReturn(
            Optional.of(Member.builder()
                .build()));

        // when & then
        assertThrows(DuplicatedPhoneNumberException.class, () -> {
            memberService.createMember(requestDto, interestCode, preferenceMeetingTypeCode);
        });
        verify(memberRepository).findByPhoneNumber(anyString());
        verify(memberRepository, never()).save(any(Member.class));
        verify(memberInterestRepository, never()).saveAll(anyList());
        verify(memberPreferenceMeetingTypeRepository, never()).saveAll(anyList());
    }

    @DisplayName("회원 수정에 성공한다.")
    @Test
    void modifyMember_Success() {
        // given
        ModifyMemberRequestDto requestDto = MemberFixture.KAI.createModifyMemberRequestDto();

        when(memberRepository.findById(anyLong())).thenReturn(
            Optional.ofNullable(member));

        // when
        memberService.modifyMember(memberId, requestDto, interestCode);

        // then
        verify(memberRepository).findById(anyLong());
        verify(memberInterestRepository).saveAll(anyList());

        assertEquals(requestDto.nickname(), member.getNickname());
        assertEquals(requestDto.mbti(), member.getMbti().toString());
        assertEquals(requestDto.introduction(), member.getIntroduction());
    }

    @DisplayName("회원이 존재하지 않는 경우 회원 정보를 수정하면 MemberNotFoundException이 발생한다.")
    @Test
    void modifyMember_NotFoundMember() {
        // given
        ModifyMemberRequestDto requestDto = MemberFixture.KAI.createModifyMemberRequestDto();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(MemberNotFoundException.class, () -> {
            memberService.modifyMember(1L, requestDto, interestCode);
        });

        verify(memberRepository).findById(anyLong());
        verify(memberInterestRepository, never()).saveAll(anyList());

        assertNotEquals(requestDto.nickname(), member.getNickname());
        assertNotEquals(requestDto.mbti(), member.getMbti().toString());
        assertNotEquals(requestDto.introduction(), member.getIntroduction());
    }

    @DisplayName("회원 선호 정보 수정에 성공한다.")
    @Test
    void modifyMemberPreference_Success() {
        // given
        ModifyMemberPreferenceRequestDto requestDto
            = PreferenceFixture.GENERAL_PREFERENCE.createModifyMemberPreferenceDto();

        when(memberRepository.findById(anyLong())).thenReturn(
            Optional.ofNullable(member));

        // when
        memberService.modifyPreference(1L, requestDto, preferenceMeetingTypeCode);

        // then
        verify(memberRepository).findById(anyLong());
        verify(memberPreferenceMeetingTypeRepository).saveAll(anyList());

        assertEquals(requestDto.preferenceMbti(), member.getPreference().getPreferenceMbti());
        assertEquals(requestDto.startPreferenceAdmissionYear(),
            member.getPreference().getStartPreferenceAdmissionYear());
        assertEquals(requestDto.endPreferenceAdmissionYear(),
            member.getPreference().getEndPreferenceAdmissionYear());
        assertEquals(requestDto.sameCollegeState(), member.getPreference().getSameCollegeState());
        assertEquals(requestDto.drinkingOption(), member.getPreference().getDrinkingOption());
        assertEquals(requestDto.isAvoidedFriends(), member.getPreference().isAvoidedFriends());
    }

    @DisplayName("회원이 존재하지 않는 경우 선호 정보를 수정하면 MemberNotFoundException이 발생한다.")
    @Test
    void modifyMemberPreference_NotFoundMember() {
        // given
        ModifyMemberPreferenceRequestDto requestDto
            = PreferenceFixture.GENERAL_PREFERENCE.createModifyMemberPreferenceDto();

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(MemberNotFoundException.class, () -> {
            memberService.modifyPreference(memberId, requestDto, preferenceMeetingTypeCode);
        });
        verify(memberRepository).findById(anyLong());
        verify(memberPreferenceMeetingTypeRepository, never()).saveAll(anyList());

        assertNotEquals(requestDto.preferenceMbti(), member.getPreference().getPreferenceMbti());
        assertNotEquals(requestDto.startPreferenceAdmissionYear(),
            member.getPreference().getStartPreferenceAdmissionYear());
        assertNotEquals(requestDto.endPreferenceAdmissionYear(),
            member.getPreference().getEndPreferenceAdmissionYear());
        assertNotEquals(requestDto.sameCollegeState(),
            member.getPreference().getSameCollegeState());
        assertNotEquals(requestDto.drinkingOption(), member.getPreference().getDrinkingOption());
        assertNotEquals(requestDto.isAvoidedFriends(), member.getPreference().isAvoidedFriends());
    }

    @DisplayName("회원 정보 조회에 성공한다.")
    @Test
    void getMemberInfo_Success() {
        // given
        ProfileImage profileImage = ProfileImageFixture.MAIN_IMAGE.create();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(profileImageRepository.findByMemberMemberIdAndIsMain(memberId, true))
            .thenReturn(Optional.of(profileImage));

        // when
        MemberInfoResponseDto result = memberService.getMemberInfo(memberId);

        // then
        assertEquals(member.getNickname(), result.nickname());
        assertEquals(member.getMemberCode(), result.memberCode());
        assertEquals(profileImage.getLowResolutionBasicUrl(), result.profileImage());
        assertTrue(result.imageAuth());
        assertTrue(result.univAuth());
    }

    @DisplayName("회원이 존재하지 않는 경우 회원 정보 조회를 하면 MemberNotFoundException이 발생한다.")
    @Test
    void getMemberInfo_NotFoundMember() {
        // given
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(MemberNotFoundException.class, () -> {
            memberService.getMemberInfo(memberId);
        });

        verify(memberRepository).findById(anyLong());
        verify(profileImageRepository, never()).findByMemberMemberIdAndIsMain(anyLong(),
            anyBoolean());
    }

    @DisplayName("회원 상세 정보 조회에 성공한다.")
    @Test
    void getMemberDetail_Success() {
        // given
        List<ProfileImage> profileImages = List.of(ProfileImageFixture.MAIN_IMAGE.create());
        List<MemberInterest> memberInterests = new ArrayList<>();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(profileImageRepository.findByMemberMemberId(memberId))
            .thenReturn(profileImages);
        when(memberInterestRepository.findByMemberMemberId(memberId))
            .thenReturn(memberInterests);

        // when
        MemberDetailResponseDto result = memberService.getMemberDetail(memberId);

        // then
        assertEquals(member.getNickname(), result.nickname());
        assertEquals(member.getGender(), result.gender());
        assertEquals(member.getMbti(), result.mbti());
        assertEquals(member.getCollegeInfo().getCollege(), result.college());
        assertEquals(member.getCollegeInfo().getCollegeType(), result.collegeType());
        assertEquals(member.getCollegeInfo().getAdmissionYear(), result.admissionYear());
        assertEquals(member.getIntroduction(), result.introduction());
    }

    @DisplayName("회원이 존재하지 않는 경우 회원 상세 정보 조회를 하면 MemberNotFoundException이 발생한다.")
    @Test
    void getMemberDetail_NotFoundMember() {
        // given
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(MemberNotFoundException.class, () -> {
            memberService.getMemberDetail(memberId);
        });

        verify(memberRepository).findById(anyLong());
        verify(profileImageRepository, never()).findByMemberMemberId(anyLong());
        verify(memberInterestRepository, never()).findByMemberMemberId(anyLong());
    }

    @DisplayName("선호 상대 정보 조회에 성공한다.")
    @Test
    void getMemberPrefer_Success() {
        // given
        List<MemberPreferenceMeetingType> memberPreferenceMeetingTypeList = new ArrayList<>();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberPreferenceMeetingTypeRepository.findByMemberMemberId(memberId))
            .thenReturn(memberPreferenceMeetingTypeList);

        // when
        MemberPreferenceResponseDto result = memberService.getMemberPrefer(memberId);

        // then
        assertEquals(member.getPreference().getDrinkingOption(), result.drinkingOption());
        assertEquals(member.getPreference().getSameCollegeState(), result.sameCollegeState());
        assertEquals(member.getPreference().isAvoidedFriends(), result.isAvoidedFriends());
        assertEquals(member.getPreference().getStartPreferenceAdmissionYear(),
            result.startPreferenceAdmissionYear());
        assertEquals(member.getPreference().getEndPreferenceAdmissionYear(),
            result.endPreferenceAdmissionYear());
        assertEquals(member.getPreference().getPreferenceMbti(), result.preferenceMbti());
    }

    @DisplayName("회원이 존재하지 않는 경우 선호 상대 정보 조회를 하면 MemberNotFoundException이 발생한다.")
    @Test
    void getMemberPrefer_NotFoundMember() {
        // given
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(MemberNotFoundException.class, () -> {
            memberService.getMemberPrefer(memberId);
        });

        verify(memberRepository).findById(anyLong());
        verify(memberPreferenceMeetingTypeRepository, never()).findByMemberMemberId(anyLong());
    }

    @DisplayName("회원 Role 정보 조회에 성공한다.")
    @Test
    void getMemberRole_Success() {
        // given
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        RoleResponseDto result = memberService.getMemberRole(memberId);

        // then
        assertFalse(result.isManager());
        assertFalse(result.hasTeam());
    }

    @DisplayName("회원이 존재하지 않는 경우 회원 Role 정보 조회를 하면 MemberNotFoundException이 발생한다.")
    @Test
    void getMemberRole_NotFoundMember() {
        // given
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(MemberNotFoundException.class, () -> {
            memberService.getMemberRole(memberId);
        });

        verify(memberRepository).findById(anyLong());
    }
}
