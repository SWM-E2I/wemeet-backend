package com.e2i.wemeet.dto.response.member;

import com.e2i.wemeet.domain.member.data.ProfileImage;

public record ProfileImageResponseDto(
    String basicUrl,
    String lowUrl
) {

    public static ProfileImageResponseDto of(ProfileImage profileImage) {
        return new ProfileImageResponseDto(
            profileImage.getBasicUrl(),
            profileImage.getLowUrl()
        );
    }
}
