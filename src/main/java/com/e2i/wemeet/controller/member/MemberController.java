package com.e2i.wemeet.controller.member;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.memberinterest.MemberInterest;
import com.e2i.wemeet.domain.memberpreferencemeetingtype.MemberPreferenceMeetingType;
import com.e2i.wemeet.domain.profileimage.ProfileImage;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberPreferenceRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.ResponseStatus;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import com.e2i.wemeet.dto.response.member.MemberPreferenceResponseDto;
import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedException;
import com.e2i.wemeet.service.code.CodeService;
import com.e2i.wemeet.service.member.MemberService;
import com.e2i.wemeet.service.memberinterest.MemberInterestService;
import com.e2i.wemeet.service.memberpreferencemeetingtype.MemberPreferenceMeetingTypeService;
import com.e2i.wemeet.service.profileimage.ProfileImageService;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    private final CodeService codeService;


    @PostMapping
    public ResponseEntity<ResponseDto> createMember(
        @RequestBody @Valid CreateMemberRequestDto requestDto) {
        List<Code> interestCode = new ArrayList<>();
        if (requestDto.memberInterestList() != null) {
            interestCode = findCode(requestDto.memberInterestList(), "G003");
        }

        List<Code> preferenceMeetingTypeCode
            = findCode(requestDto.preferenceMeetingTypeList(), "G004");

        Member savedMember = memberService.createMember(requestDto, interestCode,
            preferenceMeetingTypeCode);

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

        MemberDetailResponseDto result = new MemberDetailResponseDto(member, profileImageList,
            memberInterestList);

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
    public ResponseEntity<ResponseDto> getMemberPreference(
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

    @PutMapping("/{memberId}")
    public ResponseEntity<ResponseDto> modifyMember(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @PathVariable("memberId") Long memberId,
        @RequestBody @Valid ModifyMemberRequestDto requestDto) {
        if (!memberId.equals(memberPrincipal.getMemberId())) {
            throw new UnAuthorizedException(ErrorCode.UNAUTHORIZED_MEMBER_PROFILE);
        }

        List<Code> modifyCode = findCode(requestDto.memberInterestList(), "G003");
        memberService.modifyMember(memberId, requestDto, modifyCode);

        return ResponseEntity.ok(
            new ResponseDto(ResponseStatus.SUCCESS, "Modify Member Success", null)
        );
    }

    @PutMapping("/{memberId}/prefer")
    public ResponseEntity<ResponseDto> modifyMemberPreference(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @PathVariable("memberId") Long memberId,
        @RequestBody @Valid ModifyMemberPreferenceRequestDto requestDto) {
        if (!memberId.equals(memberPrincipal.getMemberId())) {
            throw new UnAuthorizedException(ErrorCode.UNAUTHORIZED_MEMBER_PROFILE);
        }

        List<Code> modifyCode = findCode(requestDto.preferenceMeetingTypeList(), "G002");
        memberService.modifyPreference(memberId, requestDto, modifyCode);

        return ResponseEntity.ok(
            new ResponseDto(ResponseStatus.SUCCESS, "Modify Member Preference Success", null)
        );
    }

    private List<Code> findCode(List<String> codeList, String groupCodeId) {
        List<Code> findCodeList = new ArrayList<>();
        for (String code : codeList) {
            findCodeList.add(codeService.findCode(code, groupCodeId));
        }

        return findCodeList;
    }
}
