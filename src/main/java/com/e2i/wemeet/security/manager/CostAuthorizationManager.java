package com.e2i.wemeet.security.manager;

import com.e2i.wemeet.domain.cost.CostRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.exception.unauthorized.CreditNotEnoughException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedRoleException;
import com.e2i.wemeet.security.model.MemberPrincipal;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@Slf4j
@RequiredArgsConstructor
public class CostAuthorizationManager {

    private final MemberRepository memberRepository;
    private final CostRepository costRepository;
    private final RoleHierarchy roleHierarchy;

    public void verify(final Authentication authentication, final CostAuthorize object) {
        AuthorizationDecision decision = check(authentication, object);
        if (!decision.hasCredit) {
            throw new CreditNotEnoughException();
        }
        if (!decision.hasRole) {
            throw new UnAuthorizedRoleException();
        }
    }

    private AuthorizationDecision check(final Authentication authentication, final CostAuthorize object) {
        int requiredCredit = object.value();
        int memberCredit = getMemberCredit(authentication);

        if (requiredCredit == -1) {
            costRepository.findValueByType(object.type().name());
        }
        boolean hasCredit = verifyCredit(requiredCredit, memberCredit);
        boolean hasRole = verifyRole(authentication, object.role());

        return new AuthorizationDecision(hasCredit, hasRole);
    }

    // check user has enough credit
    private boolean verifyCredit(final int requiredCredit, final int memberCredit) {
        log.info("CREDIT : requiredCredit = {}, memberCredit = {}", requiredCredit, memberCredit);
        return memberCredit >= requiredCredit;
    }

    // check user role
    private boolean verifyRole(final Authentication authentication, final Role role) {
        if (role == Role.USER && authentication.isAuthenticated()) {
            return true;
        }

        // 권한 계층에 따라 사용자가 접근할 수 있는 모든 권한을 Collection 형태로 가져온다
        Collection<? extends GrantedAuthority> memberRole = roleHierarchy.getReachableGrantedAuthorities(
            authentication.getAuthorities());

        return memberRole.stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(authority -> authority.equals(role.getRoleAttachedPrefix()));
    }

    private int getMemberCredit(final Authentication authentication) {
        MemberPrincipal principal = (MemberPrincipal) authentication.getPrincipal();
        Long memberId = principal.getMemberId();

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND_BY_ID));
        return member.getCredit();
    }

    private record AuthorizationDecision(boolean hasCredit, boolean hasRole) {

    }
}
