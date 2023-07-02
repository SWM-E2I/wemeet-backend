package com.e2i.wemeet.dto.request.member;

import com.e2i.wemeet.domain.member.Mbti;
import java.util.List;

public record ModifyMemberRequestDto(
    String nickname,
    Mbti mbti,
    String introduction,
    List<String> memberInterestList
) {

}
