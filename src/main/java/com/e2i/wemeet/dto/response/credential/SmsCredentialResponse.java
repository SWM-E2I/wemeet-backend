package com.e2i.wemeet.dto.response.credential;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.domain.member.RegistrationType;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

// SMS 인증 응답
public record SmsCredentialResponse(
    RegistrationType registrationType,
    boolean emailAuthenticated,
    Long memberId,
    Collection<? extends GrantedAuthority> role
) {

    public static SmsCredentialResponse of(final MemberPrincipal principal,
        final boolean emailAuthenticated) {
        return new SmsCredentialResponse(
            principal.getRegistrationType(),
            emailAuthenticated,
            principal.getMemberId(),
            principal.getAuthorities()
        );
    }
}
