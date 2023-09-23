package com.e2i.wemeet.util.validator.bean;

import com.e2i.wemeet.util.validator.module.AcceptStatusValidator;
import jakarta.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AcceptStatusValidator.class)
public @interface AcceptStatusValid {

    String message() default "{accept.status.valid}";

    Class[] groups() default {};

    Class[] payload() default {};

}
