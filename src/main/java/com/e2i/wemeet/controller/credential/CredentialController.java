package com.e2i.wemeet.controller.credential;

import com.e2i.wemeet.dto.request.credential.CredentialRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.ResponseStatus;
import com.e2i.wemeet.service.aws.sns.AwsSnsService;
import com.e2i.wemeet.service.credential.SmsCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/*
 * SMS 인증 번호 발급 요청 Controller
 * */
@RequiredArgsConstructor
@RestController
public class CredentialController {

    private final SmsCredentialService smsCredentialService;
    private final AwsSnsService awsSnsService;

    @PostMapping("/v1/auth/phone/issue")
    public ResponseEntity<ResponseDto> issueSmsCredential(
        @RequestBody CredentialRequestDto requestDto) {
        requestDto.validatePhone();

        final String target = requestDto.target();
        String credential = smsCredentialService.issue(target);
        awsSnsService.sendSms(target, credential);

        return ResponseEntity.ok()
            .body(new ResponseDto(ResponseStatus.SUCCESS, "인증 번호 발급 성공", null));
    }
}
