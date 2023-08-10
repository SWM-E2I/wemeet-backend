package com.e2i.wemeet.dto.response.team;

import lombok.Builder;

@Builder
public record TeamImageDto(
    String url
) {

    public static TeamImageDto of(String url) {
        return TeamImageDto.builder()
            .url(url)
            .build();
    }
}
