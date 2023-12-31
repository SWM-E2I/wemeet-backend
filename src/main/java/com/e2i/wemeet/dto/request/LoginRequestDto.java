package com.e2i.wemeet.dto.request;

import com.e2i.wemeet.util.validator.CustomFormatValidator;

public record LoginRequestDto(String phone, String credential) {
    public void validateDataFormat() {
        CustomFormatValidator.validatePhoneFormat(this.phone);
        CustomFormatValidator.validateSmsCredentialFormat(Integer.parseInt(this.credential));
    }
}
