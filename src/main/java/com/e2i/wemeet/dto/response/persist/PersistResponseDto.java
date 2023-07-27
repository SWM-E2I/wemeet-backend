package com.e2i.wemeet.dto.response.persist;

import lombok.Builder;

@Builder
public record PersistResponseDto(
    String nickname,
    boolean emailAuthenticated,
    boolean preferenceCompleted,
    boolean hasMainProfileImage,
    boolean profileImageAuthenticated,
    boolean hasTeam
) {

}
