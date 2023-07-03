package com.e2i.wemeet.dto.request.credential;


import com.e2i.wemeet.util.validator.CustomFormatValidator;

public record MailRequestDto(String college, String mail) {

    public void validateEmail() {
        CustomFormatValidator.validateEmailFormat(mail);
    }
}
