package com.e2i.wemeet.domain.team.data.suggestion;

import com.e2i.wemeet.domain.member.data.Mbti;

public record TeamLeaderData(
    String nickname,
    Mbti mbti,
    String profileImageUrl,
    String college
) {

}
