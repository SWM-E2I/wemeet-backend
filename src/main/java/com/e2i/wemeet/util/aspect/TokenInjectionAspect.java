package com.e2i.wemeet.util.aspect;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.config.security.token.Payload;
import com.e2i.wemeet.config.security.token.TokenInjector;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.Role;
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
    private final HttpServletResponse response;

    @AfterReturning(
        pointcut = "execution(* com.e2i.wemeet.service.member.MemberService.createMember(..))",
        returning = "member"
    )
    public void injectUserTokenAdvice(JoinPoint joinPoint, Member member) {
        HttpServletResponse response = getHttpServletResponseFromJoinPointArgs(joinPoint.getArgs());

        tokenInjector.injectToken(response, new MemberPrincipal(member));
    }

    @AfterReturning(
        pointcut = "execution(* com.e2i.wemeet.service.team.TeamService.deleteTeam(..)) && args(memberId, ..)"
    )
    public void injectUserTokenAdvice(final Long memberId) {
        Payload tokenPayload = new Payload(memberId, Role.USER.name());
        tokenInjector.injectToken(response, tokenPayload);
    }

    @AfterReturning(
        pointcut = "execution(* com.e2i.wemeet.service.team.TeamService.createTeam(..)) && args(memberId, ..)"
    )
    public void injectManagerTokenAdvice(final Long memberId) {
        Payload tokenPayload = new Payload(memberId, Role.MANAGER.name());
        tokenInjector.injectToken(response, tokenPayload);
    }

    private HttpServletResponse getHttpServletResponseFromJoinPointArgs(Object[] args) {
        if (args.length >= 2 && args[1] instanceof HttpServletResponse response) {
            return response;
        }
        return null;
    }
}
