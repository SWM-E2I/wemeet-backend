package com.e2i.wemeet.util.validator.bean;

import com.e2i.wemeet.util.validator.module.RegionValidator;
import jakarta.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegionValidator.class)
public @interface RegionValid {

    String message() default "{region.valid}";

    Class[] groups() default {};

    Class[] payload() default {};
}
