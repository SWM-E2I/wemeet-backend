package com.e2i.wemeet.controller.member;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberPreferenceRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.ResponseStatus;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import com.e2i.wemeet.dto.response.member.MemberPreferenceResponseDto;
import com.e2i.wemeet.service.code.CodeService;
import com.e2i.wemeet.service.member.MemberService;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final CodeService codeService;
    
    @PostMapping
    public ResponseDto<Long> createMember(
        @RequestBody @Valid CreateMemberRequestDto requestDto) {
        List<Code> interestCode = new ArrayList<>();
        if (requestDto.memberInterestList() != null) {
            interestCode = findCode(requestDto.memberInterestList());
        }

        List<Code> preferenceMeetingTypeCode
            = findCode(requestDto.preferenceMeetingTypeList());

        Member savedMember = memberService.createMember(requestDto, interestCode,
            preferenceMeetingTypeCode);

        return new ResponseDto(ResponseStatus.SUCCESS, "Create Member Success",
            savedMember.getMemberId());

    }

    @GetMapping
    public ResponseDto<MemberDetailResponseDto> getMemberDetail(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        Long memberId = memberPrincipal.getMemberId();
        MemberDetailResponseDto result = memberService.getMemberDetail(memberId);

        return
            new ResponseDto(ResponseStatus.SUCCESS, "Get Member-detail Success", result);
    }

    @GetMapping("/info")
    public ResponseDto<MemberInfoResponseDto> getMemberInfo(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        Long memberId = memberPrincipal.getMemberId();
        MemberInfoResponseDto result = memberService.getMemberIndo(memberId);
        return new ResponseDto(ResponseStatus.SUCCESS, "Get Member-Info Success", result);
    }

    @GetMapping("/prefer")
    public ResponseDto<MemberPreferenceResponseDto> getMemberPreference(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        Long memberId = memberPrincipal.getMemberId();
        MemberPreferenceResponseDto result = memberService.getMemberPrefer(memberId);

        return new ResponseDto(ResponseStatus.SUCCESS, "Get Member-Prefer Success", result);
    }

    @PutMapping
    public ResponseDto<Void> modifyMember(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @RequestBody @Valid ModifyMemberRequestDto requestDto) {
        Long memberId = memberPrincipal.getMemberId();

        List<Code> modifyCode = findCode(requestDto.memberInterestList());
        memberService.modifyMember(memberId, requestDto, modifyCode);

        return new ResponseDto(ResponseStatus.SUCCESS, "Modify Member Success", null);
    }

    @PutMapping("/prefer")
    public ResponseDto<Void> modifyMemberPreference(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @RequestBody @Valid ModifyMemberPreferenceRequestDto requestDto) {
        Long memberId = memberPrincipal.getMemberId();

        List<Code> modifyCode = findCode(requestDto.preferenceMeetingTypeList());
        memberService.modifyPreference(memberId, requestDto, modifyCode);

        return new ResponseDto(ResponseStatus.SUCCESS, "Modify Member Preference Success", null);
    }

    private List<Code> findCode(List<String> codeList) {
        List<Code> findCodeList = new ArrayList<>();
        for (String code : codeList) {
            findCodeList.add(codeService.findCode(code));
        }

        return findCodeList;
    }
}
