package com.e2i.wemeet.dto.request.credential;

import com.e2i.wemeet.util.validator.CustomFormatValidator;

public record MailCredentialCheckRequestDto(
    String mail,
    String authCode
) {

    public void validateDataFormat() {
        CustomFormatValidator.validateEmailFormat(this.mail);
        CustomFormatValidator.validateEmailCredentialFormat(Integer.parseInt(this.authCode));
    }
}
