package com.e2i.wemeet.domain.member.data;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ProfileImage {

    private String basicUrl;
    private String lowUrl;

    public ProfileImage(String basicUrl, String lowUrl) {
        this.basicUrl = basicUrl;
        this.lowUrl = lowUrl;
    }
}
