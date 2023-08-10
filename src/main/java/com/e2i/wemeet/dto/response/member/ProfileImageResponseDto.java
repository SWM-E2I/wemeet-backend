package com.e2i.wemeet.dto.response.member;

import com.e2i.wemeet.domain.member.data.ProfileImage;
import java.util.Objects;

public record ProfileImageResponseDto(
    String basicUrl,
    String lowUrl
) {

    public static ProfileImageResponseDto of(ProfileImage profileImage) {
        if (Objects.isNull(profileImage)) {
            return new ProfileImageResponseDto(null, null);
        }

        return new ProfileImageResponseDto(
            profileImage.getBasicUrl(),
            profileImage.getLowUrl()
        );
    }
}
