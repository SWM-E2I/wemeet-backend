package com.e2i.wemeet.util.validator.module;

import com.e2i.wemeet.util.validator.bean.PhoneValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<PhoneValid, String> {

    Pattern phoneNumberPattern = Pattern.compile("^\\+8210\\d{8}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return phoneNumberPattern.matcher(value).matches();
    }

}
