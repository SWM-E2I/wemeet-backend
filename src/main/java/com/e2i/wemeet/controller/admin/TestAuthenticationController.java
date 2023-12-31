package com.e2i.wemeet.controller.admin;

import com.e2i.wemeet.security.model.MemberPrincipal;
import com.e2i.wemeet.service.admin.TestAuthorizationService;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 인증 테스트용 Controller
@Slf4j
@RequiredArgsConstructor
@RestController
public class TestAuthenticationController {

    private final TestAuthorizationService testAuthorizationService;

    @GetMapping("/test")
    public MemberPrincipal ok(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        log.info("memberId : {}", memberPrincipal.getMemberId());

        Collection<? extends GrantedAuthority> authorities = memberPrincipal.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            log.info("role : {}", authority.getAuthority());
        }

        return memberPrincipal;
    }

    @GetMapping("/test/role")
    public String methodRoleTest(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        testAuthorizationService.requireManagerRole();
        return memberPrincipal.toString();
    }

    @GetMapping("/test/credit")
    public String methodCreditTest(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        testAuthorizationService.requireCredit();
        return memberPrincipal.toString();
    }

    @GetMapping("/test/credit/admin")
    public String methodCreditAdminTest(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        testAuthorizationService.requireCreditAndAdmin();
        return memberPrincipal.toString();
    }
}
