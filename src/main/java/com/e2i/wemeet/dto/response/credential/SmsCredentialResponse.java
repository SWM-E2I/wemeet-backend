package com.e2i.wemeet.dto.response.credential;

import com.e2i.wemeet.security.model.MemberPrincipal;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

// TODO :: service refactoring
// SMS 인증 응답
public record SmsCredentialResponse(
    Long memberId,
    Collection<? extends GrantedAuthority> role
) {

    public static SmsCredentialResponse of(final MemberPrincipal principal) {
        return new SmsCredentialResponse(
            principal.getMemberId(),
            principal.getAuthorities()
        );
    }
}
