package com.e2i.wemeet.controller.credential;

import com.e2i.wemeet.dto.request.credential.MailCredentialCheckRequestDto;
import com.e2i.wemeet.dto.request.credential.MailCredentialRequestDto;
import com.e2i.wemeet.dto.request.credential.SmsCredentialRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.security.model.MemberPrincipal;
import com.e2i.wemeet.service.credential.email.EmailCredentialService;
import com.e2i.wemeet.service.credential.sms.SmsCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * SMS 인증 번호 발급 요청 Controller
 * */
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@RestController
public class CredentialController {

    private final EmailCredentialService emailCredentialService;
    private final SmsCredentialService smsCredentialService;

    @PostMapping("/phone/issue")
    public ResponseDto<Void> issueSmsCredential(@RequestBody SmsCredentialRequestDto requestDto) {
        requestDto.validatePhone();

        final String target = requestDto.target();
        smsCredentialService.issue(target);

        return ResponseDto.success("휴대폰 인증 번호 발급 성공");
    }

    @PostMapping("/mail/request")
    public ResponseDto<Void> requestAuthMail(@RequestBody MailCredentialRequestDto requestDto) {
        requestDto.validateMail();

        final String target = requestDto.mail();
        emailCredentialService.issue(target);

        return ResponseDto.success("대학 메일 인증 번호 발급 성공");
    }

    @PostMapping("/mail/validate")
    public ResponseDto<Boolean> validateAuthMail(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @RequestBody MailCredentialCheckRequestDto requestDto) {
        requestDto.validateDataFormat();

        boolean result = emailCredentialService.matches(requestDto.mail(), requestDto.authCode(),
            memberPrincipal.getMemberId());

        return ResponseDto.success("대학 메일 인증 번호 확인 성공", result);
    }
}
