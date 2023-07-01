package com.e2i.wemeet.dto.response.credential;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

// SMS 인증 응답
public record SmsCredentialResponse(
    boolean registered,
    Long memberId,
    Collection<? extends GrantedAuthority> role
) {
}
