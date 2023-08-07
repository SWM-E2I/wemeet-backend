package com.e2i.wemeet.service.member_image;

import org.springframework.web.multipart.MultipartFile;

public interface MemberImageService {

    /*
     * 프로필 이미지 등록
     * */
    void uploadProfileImage(Long memberId, MultipartFile file);
}
