package com.e2i.wemeet.config.security.manager;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedCreditException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;

@Slf4j
@RequiredArgsConstructor
public class CreditAuthorizationManager {

    private final MemberRepository memberRepository;

    public void verify(final Authentication authentication, final CreditCheck object) {
        if (!check(authentication, object).isGranted()) {
            log.info("HAS NOT ENOUGH CREDIT");
            throw new UnAuthorizedCreditException();
        }
    }

    private AuthorizationDecision check(final Authentication authentication, final CreditCheck object) {
        int requiredCredit = object.value();
        int memberCredit = getMemberCredit(authentication);

        boolean canAccess = compare(requiredCredit, memberCredit);
        return new AuthorizationDecision(canAccess);
    }

    private boolean compare(final int requiredCredit, final int memberCredit) {
        log.info("CREDIT : requiredCredit = {}, memberCredit = {}", requiredCredit, memberCredit);
        return memberCredit >= requiredCredit;
    }

    private int getMemberCredit(final Authentication authentication) {
        MemberPrincipal principal = (MemberPrincipal) authentication.getPrincipal();
        Long memberId = principal.getMemberId();

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND_BY_ID));
        return member.getCredit();
    }
}
