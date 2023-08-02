package com.e2i.wemeet.controller.member;

import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberPreferenceRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import com.e2i.wemeet.dto.response.member.MemberPreferenceResponseDto;
import com.e2i.wemeet.dto.response.member.RoleResponseDto;
import com.e2i.wemeet.security.model.MemberPrincipal;
import com.e2i.wemeet.service.code.CodeService;
import com.e2i.wemeet.service.member.MemberService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public ResponseDto<Void> createMember(@RequestBody @Valid CreateMemberRequestDto requestDto) {
        memberService.createMember(requestDto);

        return ResponseDto.success("Create Member Success");
    }

    @GetMapping
    public ResponseDto<MemberDetailResponseDto> getMemberDetail(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        Long memberId = memberPrincipal.getMemberId();
        MemberDetailResponseDto result = memberService.getMemberDetail(memberId);

        return ResponseDto.success("Get Member-detail Success", result);
    }

    @GetMapping("/info")
    public ResponseDto<MemberInfoResponseDto> getMemberInfo(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        Long memberId = memberPrincipal.getMemberId();
        MemberInfoResponseDto result = memberService.getMemberInfo(memberId);

        return ResponseDto.success("Get Member-Info Success", result);
    }

    @GetMapping("/prefer")
    public ResponseDto<MemberPreferenceResponseDto> getMemberPreference(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        Long memberId = memberPrincipal.getMemberId();
        MemberPreferenceResponseDto result = memberService.getMemberPrefer(memberId);

        return ResponseDto.success("Get Member-Prefer Success", result);
    }

    @PutMapping
    public ResponseDto<Void> modifyMember(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @RequestBody @Valid ModifyMemberRequestDto requestDto) {
        Long memberId = memberPrincipal.getMemberId();
        memberService.modifyMember(memberId, requestDto);

        return ResponseDto.success("Modify Member Success");
    }

    @PutMapping("/prefer")
    public ResponseDto<Void> modifyMemberPreference(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @RequestBody @Valid ModifyMemberPreferenceRequestDto requestDto) {
        Long memberId = memberPrincipal.getMemberId();

        List<Code> modifyCode = codeService.findCodeList(requestDto.preferenceMeetingTypeList());
        memberService.modifyPreference(memberId, requestDto, modifyCode);

        return ResponseDto.success("Modify Member Preference Success");
    }

    @GetMapping("/role")
    public ResponseDto<RoleResponseDto> getMemberRole(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        RoleResponseDto result = memberService.getMemberRole(memberPrincipal.getMemberId());

        return ResponseDto.success("Get Member Role Success", result);
    }

    @DeleteMapping
    public ResponseDto<Void> deleteMember(@MemberId Long memberId) {
        memberService.deleteMember(memberId);

        return ResponseDto.success("Delete Member Success");
    }
}
