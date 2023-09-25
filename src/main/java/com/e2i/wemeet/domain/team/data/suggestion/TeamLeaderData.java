package com.e2i.wemeet.domain.team.data.suggestion;

import com.e2i.wemeet.domain.member.data.Mbti;
import lombok.Builder;

@Builder
public record TeamLeaderData(
    String nickname,
    Mbti mbti,
    String profileImageUrl,
    String college,
    String admissionYear
) {

}
