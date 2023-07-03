package com.e2i.wemeet.controller.credential;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.dto.request.credential.CredentialRequestDto;
import com.e2i.wemeet.dto.request.credential.MailAuthRequestDto;
import com.e2i.wemeet.dto.request.credential.MailRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.ResponseStatus;
import com.e2i.wemeet.service.aws.ses.AwsSesService;
import com.e2i.wemeet.service.aws.sns.AwsSnsService;
import com.e2i.wemeet.service.credential.SmsCredentialService;
import com.e2i.wemeet.service.member.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    private final SmsCredentialService smsCredentialService;
    private final AwsSnsService awsSnsService;
    private final AwsSesService awsSesService;
    private final MemberService memberService;

    @PostMapping("/phone/issue")
    public ResponseEntity<ResponseDto> issueSmsCredential(
        @RequestBody CredentialRequestDto requestDto) {
        requestDto.validatePhone();

        final String target = requestDto.target();
        String credential = smsCredentialService.issue(target);
        awsSnsService.sendSms(target, credential);

        return ResponseEntity.ok()
            .body(new ResponseDto(ResponseStatus.SUCCESS, "휴대폰 인증 번호 발급 성공", null));
    }

    // todo: 대학 - 메일 연동 작업 필요
    @PostMapping("/mail/request")
    public ResponseEntity<ResponseDto> requestAuthMail(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @RequestBody MailRequestDto requestDto) throws JsonProcessingException {
        requestDto.validateEmail();

        final String target = requestDto.mail();
        String credential = smsCredentialService.issue(target);
        awsSesService.sendEmail(target, credential);

        return ResponseEntity.ok()
            .body(new ResponseDto(ResponseStatus.SUCCESS, "대학 메일 인증 번호 발급 성공", null));
    }

    @PostMapping("/mail/validate")
    public ResponseEntity<ResponseDto> validateAuthMail(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @RequestBody MailAuthRequestDto requestDto) {
        boolean result = smsCredentialService.matches(requestDto.mail(), requestDto.authCode());
        if (result) {
            memberService.saveMail(memberPrincipal.getMemberId(), requestDto.mail());
        }

        return ResponseEntity.ok()
            .body(new ResponseDto(ResponseStatus.SUCCESS, "대학 메일 인증 번호 확인 요청 성공", result));
    }
}
