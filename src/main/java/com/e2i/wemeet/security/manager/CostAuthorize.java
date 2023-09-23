package com.e2i.wemeet.security.manager;

import com.e2i.wemeet.domain.cost.Spent;
import com.e2i.wemeet.domain.member.data.Role;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Credit 검증 어노테이션
 * */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CostAuthorize {

    /**
     * @return required credit
     */
    int value() default -1;

    /*
     * @return spent type
     * */
    Spent type() default Spent.DEFAULT;

    /**
     * @return required member's role
     */
    Role role() default Role.USER;

}
