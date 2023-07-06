package com.e2i.wemeet.dto.request.credential;

import com.e2i.wemeet.util.validator.CustomFormatValidator;

public record MailCredentialRequestDto(
    String college,
    String mail) {

    // todo : 대학 이메일 확인 작업 추가
    public void validateMail() {
        CustomFormatValidator.validateEmailFormat(mail);
    }
}
