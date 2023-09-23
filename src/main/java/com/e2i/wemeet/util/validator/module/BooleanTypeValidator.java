package com.e2i.wemeet.util.validator.module;

import com.e2i.wemeet.util.validator.bean.BooleanTypeValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BooleanTypeValidator implements ConstraintValidator<BooleanTypeValid, Boolean> {

    @Override
    public boolean isValid(Boolean value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String valueString = value.toString();
        return valueString.equals("true") || valueString.equals("false");
    }
}
