package com.e2i.wemeet.config.resolver.member;

import com.e2i.wemeet.exception.unauthorized.AccessTokenNotFoundException;
import com.e2i.wemeet.security.model.MemberPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/*
 * MemberId 를 가져오는 ArgumentResolver
 * */
@Slf4j
@Component
public class MemberIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasMemberIdAnnotation = parameter.hasParameterAnnotation(MemberId.class);
        boolean hasLongType = Long.class.isAssignableFrom(parameter.getParameterType());

        return hasLongType && hasMemberIdAnnotation;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Object principal = SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        if (!(principal instanceof MemberPrincipal memberPrincipal)) {
            throw new AccessTokenNotFoundException();
        }

        return memberPrincipal.getMemberId();
    }
}
