package com.e2i.wemeet.util.validator.module;

import com.e2i.wemeet.util.validator.bean.CollegeCodeValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class CollegeCodeValidator implements ConstraintValidator<CollegeCodeValid, String> {

    final Pattern collegeCodePattern = Pattern.compile("^[A-Z]{2}-\\d{3}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (null == value) {
            return true;
        }
        return collegeCodePattern.matcher(value).matches();
    }

}
