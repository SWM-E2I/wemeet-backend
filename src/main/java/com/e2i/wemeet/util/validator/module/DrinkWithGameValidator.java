package com.e2i.wemeet.util.validator.module;

import com.e2i.wemeet.domain.team.data.DrinkWithGame;
import com.e2i.wemeet.util.validator.bean.DrinkWithGameValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DrinkWithGameValidator implements ConstraintValidator<DrinkWithGameValid, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        
        try {
            DrinkWithGame.valueOf(value);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}