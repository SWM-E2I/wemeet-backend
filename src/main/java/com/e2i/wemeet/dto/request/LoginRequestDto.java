package com.e2i.wemeet.dto.request;

import com.e2i.wemeet.util.validator.CustomFormatValidator;

public record LoginRequestDto(String phone, int credential) {
    public void validateDataFormat() {
        CustomFormatValidator.validatePhoneFormat(this.phone);
        CustomFormatValidator.validateSmsCredentialFormat(this.credential);
    }
}
