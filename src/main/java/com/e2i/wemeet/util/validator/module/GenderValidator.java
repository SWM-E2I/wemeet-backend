package com.e2i.wemeet.util.validator.module;

import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.exception.badrequest.InvalidValueException;
import com.e2i.wemeet.util.validator.bean.GenderValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GenderValidator implements ConstraintValidator<GenderValid, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            Gender.findBy(value);
        } catch (InvalidValueException e) {
            return false;
        }
        return true;
    }
}
