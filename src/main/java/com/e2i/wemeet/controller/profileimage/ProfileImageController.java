package com.e2i.wemeet.controller.profileimage;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.ResponseStatus;
import com.e2i.wemeet.service.profileimage.ProfileImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/v1/profile_image")
@RestController
public class ProfileImageController {

    private final ProfileImageService profileImageService;

    @PostMapping
    public ResponseDto<Void> postProfileImage(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @RequestParam("main") boolean isMain, @RequestPart("file") MultipartFile file) {
        profileImageService.postProfileImage(memberPrincipal.getMemberId(), file, isMain);
        return new ResponseDto(ResponseStatus.SUCCESS, "Profile Image Upload Success", null);
    }

    @DeleteMapping("/{profileImageId}")
    public ResponseDto withdrawProfileImage(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @PathVariable("profileImageId") Long profileImageId) {
        profileImageService.deleteProfileImage(memberPrincipal.getMemberId(), profileImageId);
        return new ResponseDto(ResponseStatus.SUCCESS, "Profile Image Withdraw Success", null);
    }
}
