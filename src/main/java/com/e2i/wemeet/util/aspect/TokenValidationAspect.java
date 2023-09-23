package com.e2i.wemeet.util.aspect;

import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.exception.badrequest.TeamNotExistsException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.exception.token.NotEqualRoleToTokenException;
import com.e2i.wemeet.security.model.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Aspect
public class TokenValidationAspect {

    private final MemberRepository memberRepository;

    @Before("@annotation(com.e2i.wemeet.security.manager.IsManager)")
    public void checkCustomAuthorization() {
        MemberPrincipal principal = (MemberPrincipal) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        // MANAGER 권한이 없는 경우
        if (!principal.hasManagerRole()) {
            Long memberId = principal.getMemberId();

            Role memberRole = memberRepository.findRoleByMemberId(memberId)
                .orElseThrow(MemberNotFoundException::new);
            if (memberRole.name().equals(Role.MANAGER.name())) {
                throw new NotEqualRoleToTokenException();
            } else {
                throw new TeamNotExistsException();
            }
        }
    }
}
