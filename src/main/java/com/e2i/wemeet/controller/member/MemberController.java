package com.e2i.wemeet.controller.member;

import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import com.e2i.wemeet.dto.response.member.MemberRoleResponseDto;
import com.e2i.wemeet.security.model.MemberPrincipal;
import com.e2i.wemeet.service.code.CodeService;
import com.e2i.wemeet.service.member.MemberService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
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
    private final CodeService codeService;

    // TODO: REFACTOR
    @PostMapping
    public ResponseDto<Void> createMember(@RequestBody @Valid CreateMemberRequestDto requestDto) {

        return ResponseDto.success("Create Member Success");
    }

    // TODO: REFACTOR
    @GetMapping
    public ResponseDto<MemberDetailResponseDto> getMemberDetail(@MemberId Long memberId) {
        MemberDetailResponseDto result = memberService.readMemberDetail(memberId);

        return ResponseDto.success("Get Member-detail Success", result);
    }

    // TODO: REFACTOR
    @GetMapping("/info")
    public ResponseDto<MemberInfoResponseDto> getMemberInfo(@MemberId Long memberId) {
        MemberInfoResponseDto result = memberService.readMemberInfo(memberId);

        return ResponseDto.success("Get Member-Info Success", result);
    }

    // TODO: REFACTOR
    @PutMapping
    public ResponseDto<Void> modifyNickname(@MemberId Long memberId) {

        return ResponseDto.success("Modify Member::nickname Success");
    }

    // TODO: REFACTOR
    @PutMapping
    public ResponseDto<Void> modifyMbti(@MemberId Long memberId) {

        return ResponseDto.success("Modify Member::mbti Success");
    }

    @GetMapping("/role")
    public ResponseDto<MemberRoleResponseDto> getMemberRole(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        MemberRoleResponseDto result = memberService.readMemberRole(memberPrincipal);

        return ResponseDto.success("Get Member Role Success", result);
    }

    @DeleteMapping
    public ResponseDto<Void> deleteMember(@MemberId Long memberId) {
        memberService.deleteMember(memberId, LocalDateTime.now());

        return ResponseDto.success("Delete Member Success");
    }

    @PostMapping("/profile_image")
    public ResponseDto<Void> uploadProfileImage(@MemberId Long memberId,
        @RequestPart("file") MultipartFile file) {
        memberService.uploadProfileImage(memberId, file);

        return ResponseDto.success("Upload Profile Image Success");
    }
}
