package com.e2i.wemeet.dto.request.credential;

import com.e2i.wemeet.util.validator.CustomFormatValidator;

/*
* 인증 번호 요청
* target = phone, email
* */
public record CredentialRequestDto(String target) {

    // TODO refactoring
    public void validatePhone() {
        CustomFormatValidator.validatePhoneFormat(target);
    }

    // TODO refactoring
    public void validateEmail() {
        CustomFormatValidator.validateEmailFormat(target);
    }
}
