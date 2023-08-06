package com.e2i.wemeet.controller.member;

import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import com.e2i.wemeet.dto.response.member.RoleResponseDto;
import com.e2i.wemeet.security.model.MemberPrincipal;
import com.e2i.wemeet.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/v1/member")
@RestController
public class MemberController {

    private final MemberService memberService;


    @PostMapping
    public ResponseDto<Void> createMember(@RequestBody @Valid CreateMemberRequestDto requestDto) {
        memberService.createMember(requestDto);

        return ResponseDto.success("Create Member Success");
    }

    @GetMapping
    public ResponseDto<MemberDetailResponseDto> getMemberDetail(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        Long memberId = memberPrincipal.getMemberId();
        MemberDetailResponseDto result = memberService.getMemberDetail(memberId);

        return ResponseDto.success("Get Member-detail Success", result);
    }

    @GetMapping("/info")
    public ResponseDto<MemberInfoResponseDto> getMemberInfo(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        Long memberId = memberPrincipal.getMemberId();
        MemberInfoResponseDto result = memberService.getMemberInfo(memberId);

        return ResponseDto.success("Get Member-Info Success", result);
    }

    @PutMapping
    public ResponseDto<Void> modifyMember(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @RequestBody @Valid ModifyMemberRequestDto requestDto) {
        Long memberId = memberPrincipal.getMemberId();
        memberService.modifyMember(memberId, requestDto);

        return ResponseDto.success("Modify Member Success");
    }

    @GetMapping("/role")
    public ResponseDto<RoleResponseDto> getMemberRole(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        RoleResponseDto result = memberService.getMemberRole(memberPrincipal.getMemberId());

        return ResponseDto.success("Get Member Role Success", result);
    }

    @DeleteMapping
    public ResponseDto<Void> deleteMember(@MemberId Long memberId) {
        memberService.deleteMember(memberId);

        return ResponseDto.success("Delete Member Success");
    }

    @PostMapping("/profile_image")
    public ResponseDto<Void> uploadProfileImage(@MemberId Long memberId,
        @RequestPart("file") MultipartFile file) {
        memberService.uploadProfileImage(memberId, file);

        return ResponseDto.success("Upload Profile Image Success");
    }
}
