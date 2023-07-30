package com.e2i.wemeet.support.config;

import com.e2i.wemeet.security.model.MemberPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithCustomMockUserSecurityContext implements WithSecurityContextFactory<WithCustomMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        MemberPrincipal principal = new MemberPrincipal(Long.valueOf(annotation.id()), annotation.role());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            principal, null, principal.getAuthorities());

        securityContext.setAuthentication(authentication);
        return securityContext;
    }
}
