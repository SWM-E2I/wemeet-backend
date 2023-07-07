package com.e2i.wemeet.service.profileimage;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.profileimage.ProfileImage;
import java.util.List;
import java.util.Optional;

public interface ProfileImageService {

    /*
     * ProfileImage 등록
     */
    Long postProfileImage(Member member, String profileUrl, boolean isMain);

    /*
     * 특정 member에 등록된 ProfileImage 검색 (main인지 확인)
     */
    Optional<ProfileImage> findProfileImageByMemberIdWithIsMain(Long memberId, boolean isMain);


    /*
     * ProfileImage 삭제
     */
    void deleteProfileImage(Long profileImageId);

    /*
     * profileImageId를 통한 ProfileImage 검색
     */
    ProfileImage findProfileImageById(Long profileImageId);

    /*
     * 특정 member에 등록된 ProfileImage 검색
     */
    List<ProfileImage> findProfileImageByMemberId(Long memberId);
}
