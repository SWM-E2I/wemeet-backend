package com.e2i.wemeet.controller;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 인증 테스트용 Controller
@RestController
public class TestController {

    @GetMapping("/test")
    public MemberPrincipal ok(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        System.out.println(memberPrincipal.getMemberId());

        Collection<? extends GrantedAuthority> authorities = memberPrincipal.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            System.out.println(authority.getAuthority());
        }

        return memberPrincipal;
    }
}
