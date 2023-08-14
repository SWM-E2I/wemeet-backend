package com.e2i.wemeet.dto.response.meeting;

import com.e2i.wemeet.domain.member.data.Mbti;

public record LeaderResponse(
    Long leaderId,
    String nickname,
    Mbti mbti,
    String collegeName,
    String leaderProfileImageUrl

) {

    public static LeaderResponse of() {
        return null;
    }

}
