package com.e2i.wemeet.dto.response.team;

public record TeamImageDto(
    String url
) {

    public static TeamImageDto of(String url) {
        return new TeamImageDto(url);
    }
}
