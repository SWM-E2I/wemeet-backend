package com.e2i.wemeet.util.validator.bean;

import com.e2i.wemeet.util.validator.module.GenderValidator;
import jakarta.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GenderValidator.class)
public @interface GenderValid {

    String message() default "{invalid.gender.value}";

    Class[] groups() default {};

    Class[] payload() default {};

}
