package com.e2i.wemeet.controller.member;

import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.UpdateMemberRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberRoleResponseDto;
import com.e2i.wemeet.security.model.MemberPrincipal;
import com.e2i.wemeet.service.member.MemberService;
import com.e2i.wemeet.service.member_image.MemberImageService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    private final MemberImageService memberImageService;


    @PostMapping
    public ResponseDto<Void> createMember(@RequestBody @Valid CreateMemberRequestDto requestDto) {
        memberService.createMember(requestDto);

        return ResponseDto.success("Create Member Success");
    }

    @GetMapping
    public ResponseDto<MemberDetailResponseDto> getMemberDetail(@MemberId Long memberId) {
        MemberDetailResponseDto result = memberService.readMemberDetail(memberId);

        return ResponseDto.success("Get Member-detail Success", result);
    }

    @PatchMapping
    public ResponseDto<Void> update(@MemberId Long memberId,
        @Valid @RequestBody UpdateMemberRequestDto requestDto) {
        memberService.updateMember(memberId, requestDto);

        return ResponseDto.success("Update Member Success");
    }

    @GetMapping("/role")
    public ResponseDto<MemberRoleResponseDto> getMemberRole(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        MemberRoleResponseDto result = memberService.readMemberRole(memberPrincipal);

        return ResponseDto.success("Get Member Role Success", result);
    }

    @PostMapping("/profile-image")
    public ResponseDto<Void> uploadProfileImage(@MemberId Long memberId,
        @RequestPart("file") MultipartFile file) {
        memberImageService.uploadProfileImage(memberId, file);

        return ResponseDto.success("Upload Profile Image Success");
    }

    @DeleteMapping
    public ResponseDto<Void> delete(@MemberId Long memberId) {
        memberService.deleteMember(memberId, LocalDateTime.now());

        return ResponseDto.success("Delete Member Success");
    }
}
