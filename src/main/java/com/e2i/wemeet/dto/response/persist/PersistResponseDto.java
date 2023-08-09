package com.e2i.wemeet.dto.response.persist;

import lombok.Builder;

@Builder
public record PersistResponseDto(
    String nickname,
    boolean emailAuthenticated,
    boolean hasMainProfileImage,
    String basicProfileImage,
    String lowProfileImage,
    boolean profileImageAuthenticated,
    boolean hasTeam
) {

    @Override
    public String toString() {
        return "PersistResponseDto{" +
            "nickname='" + nickname + '\'' +
            ", emailAuthenticated=" + emailAuthenticated +
            ", hasMainProfileImage=" + hasMainProfileImage +
            ", basicProfileImage='" + basicProfileImage + '\'' +
            ", lowProfileImage='" + lowProfileImage + '\'' +
            ", profileImageAuthenticated=" + profileImageAuthenticated +
            ", hasTeam=" + hasTeam +
            '}';
    }
}
