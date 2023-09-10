package com.e2i.wemeet.util.aspect;

import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.exception.badrequest.TeamNotExistsException;
import com.e2i.wemeet.exception.token.NotEqualRoleToTokenException;
import com.e2i.wemeet.security.model.MemberPrincipal;
import com.e2i.wemeet.service.admin.TokenAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Aspect
public class TokenValidationAspect {

    private final TokenAuthorizationService tokenAuthorizationService;

    @Before("@annotation(com.e2i.wemeet.security.manager.IsManager)")
    public void checkCustomAuthorization() {
        MemberPrincipal principal = (MemberPrincipal) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        // MANAGER 권한이 없는 경우
        if (!principal.hasManagerRole()) {
            Long memberId = principal.getMemberId();

            Role memberRole = tokenAuthorizationService.getMemberRoleByMemberId(memberId);
            if (memberRole.name().equals(Role.MANAGER.name())) {
                throw new NotEqualRoleToTokenException();
            } else {
                throw new TeamNotExistsException();
            }
        }
    }
}
