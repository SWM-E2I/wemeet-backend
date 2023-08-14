package com.e2i.wemeet.util.validator.module;

import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.util.validator.bean.RegionValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RegionValidator implements ConstraintValidator<RegionValid, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            Region.valueOf(value);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
