package com.e2i.wemeet.domain.member.persist;

import com.e2i.wemeet.domain.member.Preference;
import com.e2i.wemeet.domain.profileimage.ProfileImage;
import com.e2i.wemeet.dto.response.persist.PersistResponseDto;
import org.springframework.util.StringUtils;

public record PersistLoginData(
    String nickname,
    String email,
    Preference preference,
    ProfileImage profileImage,
    Long teamId
) {

    public PersistResponseDto toPersistResponseDto() {
        return new PersistResponseDto(nickname, emailAuthenticated(), preferenceCompleted(),
            hasMainProfileImage(), hasCertifiedProfileImage(), hasTeam());
    }

    private boolean emailAuthenticated() {
        return StringUtils.hasText(this.email);
    }

    private boolean preferenceCompleted() {
        return preference != null && this.preference.isComplete();
    }

    private boolean hasMainProfileImage() {
        return profileImage != null && profileImage.getIsMain();
    }

    private boolean hasCertifiedProfileImage() {
        return profileImage != null && profileImage.getIsCertified();
    }

    private boolean hasTeam() {
        return teamId != null;
    }

    public String toString() {
        return "PersistLoginData(nickname=" + this.nickname + ", email=" + this.email
            + ", preference=" + this.preference + ", profileImage=" + this.profileImage
            + ", team=" + this.teamId + ")";
    }
}
