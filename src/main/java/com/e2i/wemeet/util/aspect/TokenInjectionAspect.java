package com.e2i.wemeet.util.aspect;

import com.e2i.wemeet.domain.member.Role;
import com.e2i.wemeet.security.model.MemberPrincipal;
import com.e2i.wemeet.security.token.Payload;
import com.e2i.wemeet.security.token.TokenInjector;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
        returning = "memberId"
    )
    public void injectTokenAdvice(Long memberId) {
        tokenInjector.injectToken(response, new MemberPrincipal(memberId, Role.USER.name()));
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
}
