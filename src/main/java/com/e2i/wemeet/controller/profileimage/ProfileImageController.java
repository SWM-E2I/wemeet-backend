package com.e2i.wemeet.controller.profileimage;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.profileimage.ProfileImage;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.ResponseStatus;
import com.e2i.wemeet.service.aws.s3.AwsS3Service;
import com.e2i.wemeet.service.member.MemberService;
import com.e2i.wemeet.service.profileimage.ProfileImageService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final MemberService memberService;
    private final AwsS3Service awsS3Service;

    @PostMapping
    public ResponseEntity<ResponseDto> postProfileImage(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @RequestParam("main") boolean isMain, @RequestPart("file") MultipartFile file) {
        Member member = memberService.findMemberById(memberPrincipal.getMemberId());
        Optional<ProfileImage> profileImage = profileImageService.findProfileImageByMemberIdWithIsMain(
            member.getMemberId(), isMain);

        if (profileImage.isPresent()) {
            awsS3Service.deleteObject(profileImage.get().getBasicUrl());
            profileImageService.deleteProfileImage(profileImage.get().getProfileImageId());
        }

        String key = awsS3Service.putObject(file);
        profileImageService.postProfileImage(member, key, isMain);

        return ResponseEntity.ok(
            new ResponseDto(ResponseStatus.SUCCESS, "Profile Image Upload Success", null)
        );
    }
}
