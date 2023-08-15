package com.e2i.wemeet.dto.response.suggestion;

import com.e2i.wemeet.domain.member.data.Mbti;
import lombok.Builder;

@Builder
public record TeamLeaderResponseDto(
    String nickname,
    Mbti mbti,
    String college
) {

}
