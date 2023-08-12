package com.e2i.wemeet.util.validator.module;

import com.e2i.wemeet.domain.team.data.DrinkRate;
import com.e2i.wemeet.util.validator.bean.DrinkRateValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class DrinkRateValidator implements ConstraintValidator<DrinkRateValid, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            DrinkRate.valueOf(value);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}