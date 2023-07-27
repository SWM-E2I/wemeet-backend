package com.e2i.wemeet.util.aspect;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.config.security.token.TokenInjector;
import com.e2i.wemeet.domain.member.Member;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Aspect
public class TokenInjectionAspect {

    private final TokenInjector tokenInjector;

    @AfterReturning(
        pointcut = "execution(* com.e2i.wemeet.service.member.MemberService.createMember(..))",
        returning = "member"
    )
    public void injectTokenAdvice(JoinPoint joinPoint, Member member) {
        HttpServletResponse response = getHttpServletResponseFromJoinPointArgs(joinPoint.getArgs());

        tokenInjector.injectToken(response, new MemberPrincipal(member));
    }

    private HttpServletResponse getHttpServletResponseFromJoinPointArgs(Object[] args) {
        if (args.length >= 2 && args[1] instanceof HttpServletResponse response) {
            return response;
        }
        return null;
    }
}
