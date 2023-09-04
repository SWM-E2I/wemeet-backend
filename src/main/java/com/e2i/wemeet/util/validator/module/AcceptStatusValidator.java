package com.e2i.wemeet.util.validator.module;

import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.util.validator.bean.AcceptStatusValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AcceptStatusValidator implements ConstraintValidator<AcceptStatusValid, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            AcceptStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
