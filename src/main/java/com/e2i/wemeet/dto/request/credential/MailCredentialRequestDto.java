package com.e2i.wemeet.dto.request.credential;

import com.e2i.wemeet.util.validator.CustomFormatValidator;

public record MailCredentialRequestDto(
    String college,
    String mail
) {

    public void validateMail() {
        CustomFormatValidator.validateEmailFormat(mail);
    }
}
