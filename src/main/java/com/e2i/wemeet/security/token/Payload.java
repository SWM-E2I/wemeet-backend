package com.e2i.wemeet.security.token;

import com.auth0.jwt.interfaces.Claim;
import com.e2i.wemeet.domain.member.Role;
import com.e2i.wemeet.security.model.MemberPrincipal;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

/*
 * Jwt - Payload 에 필요한 값을 저장하는 객체
 * */
@Getter
public class Payload {

    // Claim Key 값
    public static final String ID = "id";
    public static final String ROLE = "role";

    // Claim 에 넣어줄 값
    private final Long memberId;
    private final String role;

    public Payload(Long memberId, String role) {
        this.memberId = memberId;
        this.role = role;
    }

    public Payload(MemberPrincipal memberPrincipal) {
        this.memberId = memberPrincipal.getMemberId();
        this.role = convertRole(memberPrincipal.getAuthorities());
    }

    public Payload(Map<String, Claim> claims) {
        this.memberId = claims.get(ID).asLong();
        this.role = claims.get(ROLE).asString();
    }

    private String convertRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
            .map(authority -> Role.removePrefix(authority.getAuthority()))
            .findFirst()
            .orElse(Role.USER.name());
    }
}
