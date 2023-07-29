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

    public String toString() {
        return "PersistResponseDto(nickname=" + this.nickname() + ", emailAuthenticated=" + this.emailAuthenticated() + ", preferenceCompleted="
            + this.preferenceCompleted() + ", hasMainProfileImage=" + this.hasMainProfileImage() + ", profileImageAuthenticated="
            + this.profileImageAuthenticated() + ", hasTeam=" + this.hasTeam() + ")";
    }
}
