package com.e2i.wemeet.service.member_image;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.data.ProfileImage;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.service.aws.s3.S3Service;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class MemberImageServiceImpl implements MemberImageService {

    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    @Value("${aws.s3.profileImageBucket}")
    private String profileImageBucket;

    private static final String VERSION_PREFIX = "/v1/";
    private static final String BASIC_PREFIX = "/basic/";
    private static final String LOW_PREFIX = "/low/";
    private static final String FILE_EXTENSION = ".jpg";

    @Override
    @Transactional
    public void uploadProfileImage(Long memberId, MultipartFile file) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();

        String randomKey = UUID.randomUUID().toString();

        String basicObjectKey =
            VERSION_PREFIX + memberId + BASIC_PREFIX + randomKey + FILE_EXTENSION;
        String lowObjectKey =
            VERSION_PREFIX + memberId + LOW_PREFIX + randomKey + FILE_EXTENSION;

        s3Service.upload(file, basicObjectKey, profileImageBucket);

        member.saveProfileImage(ProfileImage.builder()
            .basicUrl(basicObjectKey)
            .lowUrl(lowObjectKey)
            .build());
    }
}
