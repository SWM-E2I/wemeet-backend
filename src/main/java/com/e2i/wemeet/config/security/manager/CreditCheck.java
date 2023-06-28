package com.e2i.wemeet.config.security.manager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
* Credit 검증 어노테이션
* */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CreditCheck {

    /**
     * @return required credit
     */
    int value();

}
