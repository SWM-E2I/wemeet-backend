package com.e2i.wemeet.util.validator.module;


import com.e2i.wemeet.util.validator.bean.KakaoOpenChatLinkValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class KakaoOpenChatLinkValidator implements ConstraintValidator<KakaoOpenChatLinkValid, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.contains("open.kakao.com/o/");
    }

}
