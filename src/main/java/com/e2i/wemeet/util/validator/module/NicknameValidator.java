package com.e2i.wemeet.util.validator.module;

import com.e2i.wemeet.exception.badrequest.InvalidDataFormatException;
import com.e2i.wemeet.util.validator.CustomFormatValidator;
import com.e2i.wemeet.util.validator.bean.NicknameValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NicknameValidator implements ConstraintValidator<NicknameValid, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            CustomFormatValidator.validateNicknameFormat(value);
        } catch (InvalidDataFormatException e) {
            return false;
        }
        return true;
    }
}
