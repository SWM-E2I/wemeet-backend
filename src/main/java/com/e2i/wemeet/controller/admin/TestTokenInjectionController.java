package com.e2i.wemeet.controller.admin;

import com.e2i.wemeet.config.security.token.Payload;
import com.e2i.wemeet.config.security.token.TokenInjector;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.ResponseStatus;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
* profile 이 prod 가 아닐 때만 동작하는 Controller
* */
@RequiredArgsConstructor
@Transactional
@Profile("!prod")
@RestController
public class TestTokenInjectionController {

    private final TokenInjector tokenInjector;
    private final MemberRepository memberRepository;

    @PostMapping("/test/register")
    public ResponseEntity<ResponseDto> registerAndReturnAccessToken(HttpServletResponse response, @RequestParam String fixture) {
        Member request = AdminMemberFixture.getFixture(fixture).create();
        Member member = memberRepository.save(request);

        tokenInjector.injectAccessToken(response, new Payload(member.getMemberId(), member.getRole().name()));

        return ResponseEntity.ok(
            new ResponseDto(
                ResponseStatus.SUCCESS,
                "member register and access token injection success",
                member)
        );
    }

    // 특정 유저의
    @PostMapping("/test/access/{memberId}")
    public ResponseEntity<ResponseDto> getAccessToken(@PathVariable Long memberId, HttpServletResponse response) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);

        tokenInjector.injectAccessToken(response, new Payload(member.getMemberId(), member.getRole().name()));

        return ResponseEntity.ok(
            new ResponseDto(ResponseStatus.SUCCESS, "Access Token Injection Success", null)
        );
    }
}
