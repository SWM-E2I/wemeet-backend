package com.e2i.wemeet.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberRequestDto;
import com.e2i.wemeet.dto.response.member.RoleResponseDto;
import com.e2i.wemeet.exception.badrequest.DuplicatedPhoneNumberException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.service.aws.s3.S3Service;
import com.e2i.wemeet.support.fixture.MemberFixture;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private S3Service s3Service;
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

        assertThatThrownBy(() -> memberService.createMember(requestDto))
            .isInstanceOf(DuplicatedPhoneNumberException.class);

        verify(memberRepository).findByPhoneNumber(anyString());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @DisplayName("회원 수정에 성공한다.")
    @Test
    void modifyMember_Success() {
        // given

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
    }

    @DisplayName("회원 선호 정보 수정에 성공한다.")
    @Test
    void modifyMemberPreference_Success() {
        // given

    }

    @DisplayName("회원이 존재하지 않는 경우 선호 정보를 수정하면 MemberNotFoundException이 발생한다.")
    @Test
    void modifyMemberPreference_NotFoundMember() {
        // given

    }

    @DisplayName("회원 정보 조회에 성공한다.")
    @Test
    void getMemberInfo_Success() {
        // given

    }

    @DisplayName("회원이 존재하지 않는 경우 회원 정보 조회를 하면 MemberNotFoundException이 발생한다.")
    @Test
    void getMemberInfo_NotFoundMember() {
        // given

    }

    @DisplayName("회원 상세 정보 조회에 성공한다.")
    @Test
    void getMemberDetail_Success() {
        // given
    }

    @DisplayName("회원이 존재하지 않는 경우 회원 상세 정보 조회를 하면 MemberNotFoundException이 발생한다.")
    @Test
    void getMemberDetail_NotFoundMember() {
        // given
    }

    @DisplayName("선호 상대 정보 조회에 성공한다.")
    @Test
    void getMemberPrefer_Success() {
        // given
    }

    @DisplayName("회원이 존재하지 않는 경우 선호 상대 정보 조회를 하면 MemberNotFoundException이 발생한다.")
    @Test
    void getMemberPrefer_NotFoundMember() {
        // given

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

    @DisplayName("회원의 프로필 이미지를 등록할 수 있다.")
    @Test
    void uploadProfileImage_Success() {
        // given
        Member kai = MemberFixture.KAI.create();
        MultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpg",
            "test data".getBytes());

        when(memberRepository.findById(memberId))
            .thenReturn(Optional.of(kai));
        doNothing().when(s3Service).upload(any(MultipartFile.class), anyString());

        // when
        memberService.uploadProfileImage(memberId, multipartFile);

        // then
        assertThat(kai.getProfileImage().getBasicUrl())
            .isNotNull()
            .containsPattern("v1/profileImage/.*.-basic.jpg");
        assertThat(kai.getProfileImage().getLowUrl())
            .isNotNull()
            .containsPattern("v1/profileImage/.*.-low.jpg");
    }
}
