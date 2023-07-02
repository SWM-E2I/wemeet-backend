package com.e2i.wemeet.controller.member;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.memberinterest.MemberInterest;
import com.e2i.wemeet.domain.memberpreferencemeetingtype.MemberPreferenceMeetingType;
import com.e2i.wemeet.domain.profileimage.ProfileImage;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.ResponseStatus;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import com.e2i.wemeet.dto.response.member.MemberPreferenceResponseDto;
import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedException;
import com.e2i.wemeet.service.member.MemberService;
import com.e2i.wemeet.service.memberinterest.MemberInterestService;
import com.e2i.wemeet.service.memberpreferencemeetingtype.MemberPreferenceMeetingTypeService;
import com.e2i.wemeet.service.profileimage.ProfileImageService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/member")
@RestController
public class MemberController {

    private final MemberService memberService;
    private final ProfileImageService profileImageService;
    private final MemberInterestService memberInterestService;
    private final MemberPreferenceMeetingTypeService memberPreferenceMeetingTypeService;


    @PostMapping
    public ResponseEntity<ResponseDto> createMember(
        @RequestBody CreateMemberRequestDto requestDto) {
        Member savedMember = memberService.createMember(requestDto);

        return ResponseEntity.ok(
            new ResponseDto(ResponseStatus.SUCCESS, "Create Member Success",
                savedMember.getMemberId())
        );
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<ResponseDto> getMemberDetail(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @PathVariable("memberId") Long memberId) {
        if (!memberId.equals(memberPrincipal.getMemberId())) {
            throw new UnAuthorizedException(ErrorCode.UNAUTHORIZED_MEMBER_PROFILE);
        }

        Member member = memberService.findMemberById(memberId);
        List<ProfileImage> profileImageList = profileImageService.findProfileImageByMemberId(
            memberId);
        List<MemberInterest> memberInterestList =
            memberInterestService.findMemberInterestByMemberId(memberId);

        MemberDetailResponseDto result = MemberDetailResponseDto.builder()
            .nickname(member.getNickname())
            .gender(member.getGender())
            .college(member.getCollegeInfo().getCollege())
            .collegeType(member.getCollegeInfo().getCollegeType())
            .admissionYear(member.getCollegeInfo().getAdmissionYear())
            .introduction(member.getIntroduction())
            .profileImageList(profileImageList)
            .memberInterestList(memberInterestList)
            .build();

        return ResponseEntity.ok(
            new ResponseDto(ResponseStatus.SUCCESS, "Get Member-detail Success", result)
        );
    }

    @GetMapping("/{memberId}/info")
    public ResponseEntity<ResponseDto> getMemberInfo(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @PathVariable("memberId") Long memberId) {
        if (!memberId.equals(memberPrincipal.getMemberId())) {
            throw new UnAuthorizedException(ErrorCode.UNAUTHORIZED_MEMBER_PROFILE);
        }
        Member member = memberService.findMemberById(memberId);
        Optional<ProfileImage> mainProfileImage = profileImageService
            .findProfileImageByMemberIdWithIsMain(memberId, true);

        String profileImageUrl = mainProfileImage.map(ProfileImage::getLowResolutionBasicUrl)
            .orElse(null);
        boolean imageAuth = mainProfileImage.map(ProfileImage::isCertified).orElse(false);
        boolean univAuth = member.getCollegeInfo().getMail() != null;

        MemberInfoResponseDto result = MemberInfoResponseDto.builder()
            .nickname(member.getNickname())
            .memberCode(member.getMemberCode())
            .profileImage(profileImageUrl)
            .imageAuth(imageAuth)
            .univAuth(univAuth)
            .build();

        return ResponseEntity.ok(
            new ResponseDto(ResponseStatus.SUCCESS, "Get Member-Info Success", result)
        );
    }

    @GetMapping("/{memberId}/prefer")
    public ResponseEntity<ResponseDto> getMemberPrefernece(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @PathVariable("memberId") Long memberId) {
        if (!memberId.equals(memberPrincipal.getMemberId())) {
            throw new UnAuthorizedException(ErrorCode.UNAUTHORIZED_MEMBER_PROFILE);
        }

        Member member = memberService.findMemberById(memberId);
        List<MemberPreferenceMeetingType> memberPreferenceMeetingTypeList
            = memberPreferenceMeetingTypeService.findMemberPreferenceMeetingType(memberId);

        MemberPreferenceResponseDto result = new MemberPreferenceResponseDto(member,
            memberPreferenceMeetingTypeList);

        return ResponseEntity.ok(
            new ResponseDto(ResponseStatus.SUCCESS, "Get Member-Prefer Success", result)
        );
    }
}
