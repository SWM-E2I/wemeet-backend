package com.e2i.wemeet.domain.member.persist;

import com.e2i.wemeet.dto.response.persist.PersistResponseDto;
import org.springframework.util.StringUtils;

// TODO :: refactoring
public record PersistLoginData(
    String nickname,
    String email,
    Long teamId
) {

    public PersistResponseDto toPersistResponseDto() {
        return null;
    }

    private boolean emailAuthenticated() {
        return StringUtils.hasText(this.email);
    }

    private boolean hasTeam() {
        return teamId != null;
    }
}
