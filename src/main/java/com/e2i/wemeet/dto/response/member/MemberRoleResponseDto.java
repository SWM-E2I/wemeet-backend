package com.e2i.wemeet.dto.response.member;

import com.e2i.wemeet.security.model.MemberPrincipal;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;

@Builder
public record MemberRoleResponseDto(
    boolean isManager,
    boolean hasTeam
) {

    public static MemberRoleResponseDto of(final MemberPrincipal memberPrincipal) {
        boolean isManager = isManager(memberPrincipal);
        boolean hasTeam = isManager;

        return MemberRoleResponseDto.builder()
            .isManager(isManager)
            .hasTeam(hasTeam)
            .build();
    }

    private static boolean isManager(MemberPrincipal memberPrincipal) {
        GrantedAuthority roleManager = memberPrincipal.getAuthorities().stream()
            .filter(authority -> authority.getAuthority().equals("ROLE_MANAGER"))
            .findFirst()
            .orElse(null);
        return roleManager != null;
    }
}