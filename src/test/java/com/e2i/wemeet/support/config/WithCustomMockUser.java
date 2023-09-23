package com.e2i.wemeet.support.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.test.context.support.WithSecurityContext;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WithSecurityContext(factory = WithCustomMockUserSecurityContext.class)
public @interface WithCustomMockUser {

    // memberId
    String id() default "1";

    String role() default "USER";

}
