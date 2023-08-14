package com.e2i.wemeet.util.validator.module;

import com.e2i.wemeet.domain.team.data.AdditionalActivity;
import com.e2i.wemeet.util.validator.bean.AdditionalActivityValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AdditionalActivityValidator implements
    ConstraintValidator<AdditionalActivityValid, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            AdditionalActivity.valueOf(value);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}