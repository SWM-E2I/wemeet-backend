package com.e2i.wemeet.config.security.model;

import com.e2i.wemeet.config.security.token.Payload;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.RegistrationType;
import com.e2i.wemeet.domain.member.Role;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

/*
 * SecurityContext 에 저장되는 인증 객체
 * */
public class MemberPrincipal implements UserDetails {

    private final Long memberId;

    /*
     * Authority 는 인가 정책을 적용할 때 필요함
     * Authentication 객체에 "ROLE_" Prefix 가 붙은 권한 이름을 넘겨줘야 정상 작동하므로 Prefix 를 붙여 저장
     * RoleHierarchy 적용 -> 각 사용자 당 권한 정보는 한개만 갖도록 구현
     * */
    private final List<? extends GrantedAuthority> authorities;
    private final RegistrationType registrationType;

    public MemberPrincipal() {
        this.memberId = null;
        this.authorities = List.of(Role.GUEST::getRoleAttachedPrefix);
        this.registrationType = RegistrationType.NOT_REGISTERED;
    }

    public MemberPrincipal(final Member member) {
        this.memberId = member.getMemberId();
        this.authorities = getAuthorities(member.getRole().name());
        this.registrationType = RegistrationType.APP;
    }

    public MemberPrincipal(final Payload payload) {
        this.memberId = payload.getMemberId();
        this.authorities = getAuthorities(payload.getRole());
        this.registrationType = RegistrationType.APP;
    }

    public MemberPrincipal(final Long memberId, final String role) {
        this.memberId = memberId;
        this.authorities = getAuthorities(role);
        this.registrationType = RegistrationType.APP;
    }

    private List<? extends GrantedAuthority> getAuthorities(final String authority) {
        String attachedPrefixRole = Role.getRoleAttachedPrefix(authority);
        return List.of(() -> attachedPrefixRole);
    }

    public Long getMemberId() {
        return memberId;
    }

    public boolean isRegistered() {
        return this.registrationType != null
            & this.registrationType != RegistrationType.NOT_REGISTERED;
    }

    public RegistrationType getRegistrationType() {
        return registrationType;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return String.valueOf(memberId);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        final String role = StringUtils.collectionToCommaDelimitedString(
            authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList()
        );
        final String format = "MemberPrincipal(memberId=%d, role=%s, registrationType=%s)";

        return String.format(format, memberId, role, registrationType);
    }
}
