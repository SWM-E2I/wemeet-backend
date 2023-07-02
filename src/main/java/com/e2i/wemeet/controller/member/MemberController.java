package com.e2i.wemeet.controller.member;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.ResponseStatus;
import com.e2i.wemeet.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/member")
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ResponseDto> createMember(
        @RequestBody CreateMemberRequestDto requestDto) {
        Member savedMember = memberService.createMember(requestDto);

        return ResponseEntity.ok(
            new ResponseDto(ResponseStatus.SUCCESS, "Create Member Success",
                savedMember.getMemberId())
        );
    }
}
