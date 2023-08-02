package com.e2i.wemeet.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    private MemberPreferenceMeetingTypeRepository memberPreferenceMeetingTypeRepository;

    @Mock
    private ProfileImageRepository profileImageRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private static final Long memberId = 1L;
    private static final Member member = MemberFixture.KAI.create();

    private static final List<Code> preferenceMeetingTypeCode = new ArrayList<>();

    @DisplayName("회원 생성에 성공한다.")
    @Test
    void createMember_Success() {
        // given
        CreateMemberRequestDto requestDto = MemberFixture.KAI.createMemberRequestDto();

        when(memberRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // when
        memberService.createMember(requestDto);

        // then
        verify(memberRepository).findByPhoneNumber(anyString());
        verify(memberRepository).save(any(Member.class));
    }

    @DisplayName("중복된 휴대폰 번호를 가진 회원이 있을 경우 DuplicatedPhoneNumberException이 발생한다.")
    @Test
    void createMember_DuplicatedPhoneNumber() {
        // given
        CreateMemberRequestDto requestDto = MemberFixture.KAI.createMemberRequestDto();

        when(memberRepository.findByPhoneNumber(anyString())).thenReturn(
            Optional.of(Member.builder()
                .build()));
      
        assertThatThrownBy(() -> memberService.createMember(requestDto, response))
               .isInstanceOf(DuplicatedPhoneNumberException.class);

        verify(memberRepository).findByPhoneNumber(anyString());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @DisplayName("회원 수정에 성공한다.")
    @Test
    void modifyMember_Success() {
        // given
        ModifyMemberRequestDto requestDto = MemberFixture.KAI.createModifyMemberRequestDto();

        when(memberRepository.findById(anyLong())).thenReturn(
            Optional.ofNullable(member));

        // when
        memberService.modifyMember(memberId, requestDto);

        // then
        verify(memberRepository).findById(anyLong());

        assertThat(member.getNickname()).isEqualTo(requestDto.nickname());
        assertThat(member.getMbti()).hasToString(requestDto.mbti());
        assertThat(member.getIntroduction()).isEqualTo(requestDto.introduction());
    }

    @DisplayName("회원이 존재하지 않는 경우 회원 정보를 수정하면 MemberNotFoundException이 발생한다.")
    @Test
    void modifyMember_NotFoundMember() {
        // given
        ModifyMemberRequestDto requestDto = MemberFixture.KAI.createModifyMemberRequestDto();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.modifyMember(1L, requestDto)).isInstanceOf(
            MemberNotFoundException.class);

        verify(memberRepository).findById(anyLong());

        assertThat(member.getNickname()).isNotEqualTo(requestDto.nickname());
        assertThat(member.getMbti().toString()).isNotEqualTo(requestDto.mbti());
        assertThat(member.getIntroduction()).isNotEqualTo(requestDto.introduction());
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

        assertThat(member.getPreference().getPreferenceMbti()).isEqualTo(
            requestDto.preferenceMbti());
        assertThat(member.getPreference().getStartPreferenceAdmissionYear()).isEqualTo(
            requestDto.startPreferenceAdmissionYear());
        assertThat(member.getPreference().getEndPreferenceAdmissionYear()).isEqualTo(
            requestDto.endPreferenceAdmissionYear());
        assertThat(member.getPreference().getSameCollegeState()).isEqualTo(
            requestDto.sameCollegeState());
        assertThat(member.getPreference().getDrinkingOption()).isEqualTo(
            requestDto.drinkingOption());
        assertThat(member.getPreference().getIsAvoidedFriends()).isEqualTo(
            requestDto.isAvoidedFriends());
    }

    @DisplayName("회원이 존재하지 않는 경우 선호 정보를 수정하면 MemberNotFoundException이 발생한다.")
    @Test
    void modifyMemberPreference_NotFoundMember() {
        // given
        ModifyMemberPreferenceRequestDto requestDto
            = PreferenceFixture.GENERAL_PREFERENCE.createModifyMemberPreferenceDto();

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.modifyPreference(memberId, requestDto,
            preferenceMeetingTypeCode)).isInstanceOf(
            MemberNotFoundException.class);

        verify(memberRepository).findById(anyLong());
        verify(memberPreferenceMeetingTypeRepository, never()).saveAll(anyList());

        assertThat(member.getPreference().getPreferenceMbti()).isNotEqualTo(
            requestDto.preferenceMbti());
        assertThat(member.getPreference().getStartPreferenceAdmissionYear()).isNotEqualTo(
            requestDto.startPreferenceAdmissionYear());
        assertThat(member.getPreference().getEndPreferenceAdmissionYear()).isNotEqualTo(
            requestDto.endPreferenceAdmissionYear());
        assertThat(member.getPreference().getSameCollegeState()).isNotEqualTo(
            requestDto.sameCollegeState());
        assertThat(member.getPreference().getDrinkingOption()).isNotEqualTo(
            requestDto.drinkingOption());
        assertThat(member.getPreference().getIsAvoidedFriends()).isNotEqualTo(
            requestDto.isAvoidedFriends());
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
        assertThat(member.getNickname()).isEqualTo(result.nickname());
        assertThat(member.getMemberCode()).isEqualTo(result.memberCode());
        assertThat(profileImage.getLowResolutionBasicUrl()).isEqualTo(result.profileImage());
        assertThat(result.imageAuth()).isTrue();
        assertThat(result.univAuth()).isTrue();
    }

    @DisplayName("회원이 존재하지 않는 경우 회원 정보 조회를 하면 MemberNotFoundException이 발생한다.")
    @Test
    void getMemberInfo_NotFoundMember() {
        // given
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getMemberInfo(memberId)).isInstanceOf(
            MemberNotFoundException.class);

        verify(memberRepository).findById(anyLong());
        verify(profileImageRepository, never()).findByMemberMemberIdAndIsMain(anyLong(),
            anyBoolean());
    }

    @DisplayName("회원 상세 정보 조회에 성공한다.")
    @Test
    void getMemberDetail_Success() {
        // given
        List<ProfileImage> profileImages = List.of(ProfileImageFixture.MAIN_IMAGE.create());

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(profileImageRepository.findByMemberMemberId(memberId))
            .thenReturn(profileImages);

        // when
        MemberDetailResponseDto result = memberService.getMemberDetail(memberId);

        // then
        assertThat(member.getNickname()).isEqualTo(result.nickname());
        assertThat(member.getGender()).isEqualTo(result.gender());
        assertThat(member.getMbti()).isEqualTo(result.mbti());
        assertThat(member.getCollegeInfo().getCollege()).isEqualTo(result.college());
        assertThat(member.getCollegeInfo().getCollegeType()).isEqualTo(result.collegeType());
        assertThat(member.getCollegeInfo().getAdmissionYear()).isEqualTo(
            result.admissionYear());
        assertThat(member.getIntroduction()).isEqualTo(result.introduction());
    }

    @DisplayName("회원이 존재하지 않는 경우 회원 상세 정보 조회를 하면 MemberNotFoundException이 발생한다.")
    @Test
    void getMemberDetail_NotFoundMember() {
        // given
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getMemberDetail(memberId)).isInstanceOf(
            MemberNotFoundException.class);

        verify(memberRepository).findById(anyLong());
        verify(profileImageRepository, never()).findByMemberMemberId(anyLong());
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
        assertThat(member.getPreference().getDrinkingOption()).isEqualTo(
            result.drinkingOption());
        assertThat(member.getPreference().getSameCollegeState()).isEqualTo(
            result.sameCollegeState());
        assertThat(member.getPreference().getIsAvoidedFriends()).isEqualTo(
            result.isAvoidedFriends());
        assertThat(member.getPreference().getStartPreferenceAdmissionYear()).isEqualTo(
            result.startPreferenceAdmissionYear());
        assertThat(member.getPreference().getEndPreferenceAdmissionYear()).isEqualTo(
            result.endPreferenceAdmissionYear());
        assertThat(member.getPreference().getPreferenceMbti()).isEqualTo(
            result.preferenceMbti());
    }

    @DisplayName("회원이 존재하지 않는 경우 선호 상대 정보 조회를 하면 MemberNotFoundException이 발생한다.")
    @Test
    void getMemberPrefer_NotFoundMember() {
        // given
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getMemberPrefer(memberId)).isInstanceOf(
            MemberNotFoundException.class);

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
        assertThat(result.isManager()).isFalse();
        assertThat(result.hasTeam()).isFalse();
    }

    @DisplayName("회원이 존재하지 않는 경우 회원 Role 정보 조회를 하면 MemberNotFoundException이 발생한다.")
    @Test
    void getMemberRole_NotFoundMember() {
        // given
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getMemberRole(memberId)).isInstanceOf(
            MemberNotFoundException.class);

        verify(memberRepository).findById(anyLong());
    }

    @DisplayName("회원 삭제에 성공한다.")
    @Test
    void delete() {
        // given
        Member kai = MemberFixture.KAI.create();
        when(memberRepository.findById(memberId))
            .thenReturn(Optional.of(kai));

        // when
        memberService.deleteMember(memberId);

        // then
        assertThat(kai.getDeletedAt())
            .isNotNull()
            .isExactlyInstanceOf(java.time.LocalDateTime.class);
    }
}
