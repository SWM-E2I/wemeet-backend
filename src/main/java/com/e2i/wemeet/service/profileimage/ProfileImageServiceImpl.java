package com.e2i.wemeet.service.profileimage;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.profileimage.ProfileImage;
import com.e2i.wemeet.domain.profileimage.ProfileImageRepository;
import com.e2i.wemeet.exception.notfound.ProfileImageNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProfileImageServiceImpl implements ProfileImageService {


    private final ProfileImageRepository profileImageRepository;

    // todo: image key 값 분리
    @Override
    @Transactional
    public Long postProfileImage(Member member, String imageKey, boolean isMain) {
        ProfileImage savedProfileImage = profileImageRepository.save(ProfileImage.builder()
            .basicUrl(imageKey)
            .blurUrl(imageKey)
            .lowResolutionBasicUrl(imageKey)
            .lowResolutionBlurUrl(imageKey)
            .isMain(isMain)
            .isCertified(false)
            .member(member)
            .build());

        return savedProfileImage.getProfileImageId();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfileImage> findProfileImageByMemberIdWithIsMain(Long memberId,
        boolean isMain) {
        return profileImageRepository.findByMemberMemberIdAndIsMain(memberId, isMain);
    }

    @Override
    @Transactional
    public void deleteProfileImage(Long profileImageId) {
        profileImageRepository.deleteById(profileImageId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileImage findProfileImageById(Long profileImageId) {
        return profileImageRepository.findById(profileImageId)
            .orElseThrow(ProfileImageNotFoundException::new);
    }
}
