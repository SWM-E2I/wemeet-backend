package com.e2i.wemeet.util.validator.bean;

import com.e2i.wemeet.util.validator.module.PhoneNumberValidator;
import jakarta.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface PhoneValid {

    String message() default "{invalid.format.phone.number}";

    Class[] groups() default {};

    Class[] payload() default {};

}
