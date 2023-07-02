package com.e2i.wemeet.service.profileimage;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.profileimage.ProfileImage;
import java.util.Optional;

public interface ProfileImageService {

    /*
     * ProfileImage 등록
     */
    Long postProfileImage(Member member, String profileUrl, boolean isMain);

    /*
     * 특정 member에 등록된 ProfileImage 검색
     */
    Optional<ProfileImage> findProfileImageByMemberIdWithIsMain(Long memberId, boolean isMain);


    /*
     * ProfileImage 삭제
     */
    void deleteProfileImage(Long profileImageId);
}
