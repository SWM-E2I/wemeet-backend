package com.e2i.wemeet.util.validator.bean;

import com.e2i.wemeet.util.validator.module.DrinkRateValidator;
import jakarta.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DrinkRateValidator.class)
public @interface DrinkRateValid {

    String message() default "{drink.rate.valid}";

    Class[] groups() default {};

    Class[] payload() default {};
}
