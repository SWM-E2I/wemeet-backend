package com.e2i.wemeet.util.validator.module;


import com.e2i.wemeet.exception.badrequest.InvalidDataFormatException;
import com.e2i.wemeet.util.validator.CustomFormatValidator;
import com.e2i.wemeet.util.validator.bean.KakaoOpenChatLinkValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class KakaoOpenChatLinkValidator implements ConstraintValidator<KakaoOpenChatLinkValid, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            CustomFormatValidator.validateOpenChatLinkFormat(value);
            return true;
        } catch (InvalidDataFormatException e) {
            return false;
        }
    }

}
