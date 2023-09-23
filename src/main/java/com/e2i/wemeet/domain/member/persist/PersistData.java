package com.e2i.wemeet.domain.member.persist;

import com.e2i.wemeet.domain.member.data.ProfileImage;

public record PersistData(
    String nickname,
    String email,
    ProfileImage profileImage,
    Long teamId
) {

}
