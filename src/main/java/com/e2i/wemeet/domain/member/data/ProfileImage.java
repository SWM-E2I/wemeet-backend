package com.e2i.wemeet.domain.member.data;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ProfileImage {

    private String basicUrl;
    private String lowUrl;
    private Boolean imageAuth;

    @Builder
    public ProfileImage(String basicUrl, String lowUrl, Boolean imageAuth) {
        this.basicUrl = basicUrl;
        this.lowUrl = lowUrl;
        this.imageAuth = imageAuth;
    }

    public ProfileImage(String basicUrl, String lowUrl) {
        this.basicUrl = basicUrl;
        this.lowUrl = lowUrl;
        this.imageAuth = false;
    }


    public boolean hasProfileImage() {
        return basicUrl != null;
    }

    public boolean isImageAuthenticated() {
        return imageAuth;
    }
}
