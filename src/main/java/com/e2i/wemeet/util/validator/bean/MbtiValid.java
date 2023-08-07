package com.e2i.wemeet.util.validator.bean;


import com.e2i.wemeet.util.validator.module.MbtiValidator;
import jakarta.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MbtiValidator.class)
public @interface MbtiValid {

    String message() default "{mbti.valid}";

    Class[] groups() default {};

    Class[] payload() default {};

}
