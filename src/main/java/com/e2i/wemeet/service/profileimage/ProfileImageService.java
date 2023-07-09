package com.e2i.wemeet.service.profileimage;

import org.springframework.web.multipart.MultipartFile;

public interface ProfileImageService {

    /*
     * ProfileImage 등록
     */
    void postProfileImage(Long memberId, MultipartFile file, boolean isMain);

    /*
     * ProfileImage 삭제
     */
    void deleteProfileImage(Long memberId, Long profileImageId);

}
