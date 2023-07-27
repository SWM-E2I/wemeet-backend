package com.e2i.wemeet.service.member;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Mbti;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.Preference;
import com.e2i.wemeet.domain.member.Role;
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
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberInterestRepository memberInterestRepository;
    private final MemberPreferenceMeetingTypeRepository memberPreferenceMeetingTypeRepository;
    private final ProfileImageRepository profileImageRepository;

    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public Long createMember(CreateMemberRequestDto requestDto, List<Code> interestCode,
        List<Code> preferenceMeetingTypeCode) {
        memberRepository.findByPhoneNumber(requestDto.phoneNumber())
            .ifPresent(member -> {
                throw new DuplicatedPhoneNumberException();
            });

        String memberCode = createMemberCode();
        Member member = memberRepository.save(requestDto.toMemberEntity(memberCode));

        saveMemberInterest(member, interestCode);
        savePreferenceMeetingType(member, preferenceMeetingTypeCode);

        return member.getMemberId();
    }

    @Override
    @Transactional
    public void modifyMember(Long memberId, ModifyMemberRequestDto requestDto,
        List<Code> modifyCode) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);

        member.modifyNickname(requestDto.nickname());
        member.modifyIntroduction(requestDto.introduction());
        member.modifyMbti(Mbti.findBy(requestDto.mbti()));

        saveMemberInterest(member, modifyCode);
    }


    @Override
    @Transactional
    public void modifyPreference(Long memberId, ModifyMemberPreferenceRequestDto requestDto,
        List<Code> modifyCode) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);

        member.modifyPreference(Preference.builder()
            .drinkingOption(requestDto.drinkingOption())
            .startPreferenceAdmissionYear(requestDto.startPreferenceAdmissionYear())
            .endPreferenceAdmissionYear(requestDto.endPreferenceAdmissionYear())
            .sameCollegeState(requestDto.sameCollegeState())
            .isAvoidedFriends(requestDto.isAvoidedFriends())
            .preferenceMbti(requestDto.preferenceMbti())
            .build());

        savePreferenceMeetingType(member, modifyCode);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDetailResponseDto getMemberDetail(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);

        List<ProfileImage> profileImageList = profileImageRepository.findByMemberMemberId(memberId);
        List<MemberInterest> memberInterestList = memberInterestRepository.findByMemberMemberId(
            memberId);

        return new MemberDetailResponseDto(member, profileImageList, memberInterestList);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberInfoResponseDto getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);

        Optional<ProfileImage> mainProfileImage =
            profileImageRepository.findByMemberMemberIdAndIsMain(memberId, true);
        String profileImageUrl = mainProfileImage.map(ProfileImage::getLowResolutionBasicUrl)
            .orElse(null);

        boolean imageAuth = mainProfileImage.map(ProfileImage::getIsCertified).orElse(false);
        boolean univAuth = member.getCollegeInfo().getMail() != null;

        return MemberInfoResponseDto.builder()
            .nickname(member.getNickname())
            .memberCode(member.getMemberCode())
            .profileImage(profileImageUrl)
            .imageAuth(imageAuth)
            .univAuth(univAuth)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberPreferenceResponseDto getMemberPrefer(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);

        List<MemberPreferenceMeetingType> memberPreferenceMeetingTypeList
            = memberPreferenceMeetingTypeRepository.findByMemberMemberId(memberId);

        return new MemberPreferenceResponseDto(member, memberPreferenceMeetingTypeList);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponseDto getMemberRole(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);

        return RoleResponseDto.builder()
            .hasTeam(member.getTeam() != null)
            .isManager(member.getRole() == Role.MANAGER)
            .build();
    }

    private void savePreferenceMeetingType(Member member, List<Code> codeList) {
        List<MemberPreferenceMeetingType> preferenceMeetingTypeList = codeList.stream()
            .map(preferenceMeetingTypeCode -> MemberPreferenceMeetingType.builder()
                .member(member)
                .code(preferenceMeetingTypeCode)
                .build())
            .toList();

        memberPreferenceMeetingTypeRepository.deleteAllByMemberMemberId(member.getMemberId());
        memberPreferenceMeetingTypeRepository.saveAll(preferenceMeetingTypeList);
    }

    private void saveMemberInterest(Member member, List<Code> codeList) {
        List<MemberInterest> memberInterests = codeList.stream()
            .map(memberInterestCode -> MemberInterest.builder()
                .member(member)
                .code(memberInterestCode)
                .build())
            .toList();

        memberInterestRepository.deleteAllByMemberMemberId(member.getMemberId());
        memberInterestRepository.saveAll(memberInterests);
    }

    private String createMemberCode() {
        int code = random.nextInt(9000) + 1000;
        return String.valueOf(code);
    }
}