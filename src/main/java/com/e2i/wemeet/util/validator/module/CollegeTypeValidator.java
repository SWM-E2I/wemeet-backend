package com.e2i.wemeet.util.validator.module;

import com.e2i.wemeet.domain.member.data.CollegeType;
import com.e2i.wemeet.util.validator.bean.CollegeTypeValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CollegeTypeValidator implements ConstraintValidator<CollegeTypeValid, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            CollegeType.valueOf(value);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
