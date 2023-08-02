package com.e2i.wemeet.controller.profileimage;

import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.security.model.MemberPrincipal;
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

    // TODO :: service refactoring
    @PostMapping
    public ResponseDto<Void> postProfileImage(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @RequestParam("main") boolean isMain,
        @RequestPart("file") MultipartFile file) {

        return ResponseDto.success("Profile Image Upload Success");
    }

    // TODO :: service refactoring
    @DeleteMapping("/{profileImageId}")
    public ResponseDto<Void> withdrawProfileImage(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @PathVariable("profileImageId") Long profileImageId) {

        return ResponseDto.success("Profile Image Withdraw Success");
    }
}
