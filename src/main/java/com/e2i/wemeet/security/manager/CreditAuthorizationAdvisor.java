package com.e2i.wemeet.security.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Aspect
public class CreditAuthorizationAdvisor {

    private final CreditAuthorizationManager creditAuthorizationManager;

    @Before("@annotation(com.e2i.wemeet.security.manager.CreditAuthorize)")
    public void verify(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        CreditAuthorize creditAuthorize = signature.getMethod().getAnnotation(CreditAuthorize.class);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        creditAuthorizationManager.verify(authentication, creditAuthorize);
    }

}
