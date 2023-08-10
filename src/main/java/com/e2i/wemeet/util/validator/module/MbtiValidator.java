package com.e2i.wemeet.util.validator.module;

import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.util.validator.bean.MbtiValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MbtiValidator implements ConstraintValidator<MbtiValid, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (null == value) {
            return true;
        }

        try {
            Mbti.valueOf(value);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
