package com.e2i.wemeet.util.validator.bean;

import com.e2i.wemeet.util.validator.module.NicknameValidator;
import jakarta.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NicknameValidator.class)
public @interface NicknameValid {

    String message() default "{invalid.nickname.format}";

    Class[] groups() default {};

    Class[] payload() default {};

}
