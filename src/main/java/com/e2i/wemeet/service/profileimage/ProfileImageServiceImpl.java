package com.e2i.wemeet.service.profileimage;

import static com.e2i.wemeet.exception.ErrorCode.AWS_S3_FILE_CONVERSION_ERROR;
import static com.e2i.wemeet.exception.ErrorCode.AWS_S3_OBJECT_DELETE_ERROR;
import static com.e2i.wemeet.exception.ErrorCode.AWS_S3_OBJECT_UPLOAD_ERROR;
import static com.e2i.wemeet.exception.ErrorCode.UNAUTHORIZED_PROFILE_IMAGE;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.profileimage.ProfileImage;
import com.e2i.wemeet.domain.profileimage.ProfileImageRepository;
import com.e2i.wemeet.exception.internal.InternalServerException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.exception.notfound.ProfileImageNotFoundException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedException;
import com.e2i.wemeet.service.aws.s3.AwsS3CredentialService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileImageServiceImpl implements ProfileImageService {


    @Value("${aws.s3.bucket}")
    private String bucket;

    private final AwsS3CredentialService awsS3CredentialService;
    private final ProfileImageRepository profileImageRepository;
    private final MemberRepository memberRepository;

    private static final String BASIC_SUFFIX = "-basic";
    private static final String BLUR_SUFFIX = "-blur";
    private static final String LOW_BASIC_SUFFIX = "-basic-low";
    private static final String LOW_BLUR_SUFFIX = "-blur-low";
    private static final String FILE_EXTENSION = ".jpg";


    @Override
    @Transactional
    public void postProfileImage(Long memberId, MultipartFile file, boolean isMain) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);

        checkSavedProfileImage(memberId, isMain);
        String objectKey = createObjectKey(memberId, isMain);
        putObject(file, objectKey + BASIC_SUFFIX);

        profileImageRepository.save(ProfileImage.builder()
            .basicUrl(objectKey + BASIC_SUFFIX + FILE_EXTENSION)
            .blurUrl(objectKey + BLUR_SUFFIX + FILE_EXTENSION)
            .lowResolutionBasicUrl(objectKey + LOW_BASIC_SUFFIX + FILE_EXTENSION)
            .lowResolutionBlurUrl(objectKey + LOW_BLUR_SUFFIX + FILE_EXTENSION)
            .isMain(isMain)
            .isCertified(false)
            .member(member)
            .build());
    }

    @Override
    @Transactional
    public void deleteProfileImage(Long memberId, Long profileImageId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);

        ProfileImage profileImage = profileImageRepository.findById(profileImageId)
            .orElseThrow(ProfileImageNotFoundException::new);

        if (member.getMemberId().equals(profileImage.getMember().getMemberId())) {
            throw new UnAuthorizedException(UNAUTHORIZED_PROFILE_IMAGE);
        }

        deleteObject(profileImage.getBasicUrl());
        profileImageRepository.deleteById(profileImageId);
    }


    private void putObject(MultipartFile multipartFile, String objectKey) {
        S3Client s3Client = awsS3CredentialService.getS3Client();

        File file = convertMultipartFileToFile(multipartFile);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", "image/jpg");
        metadata.put("content-length", String.valueOf(multipartFile.getSize()));

        try {
            PutObjectRequest putObj = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey + FILE_EXTENSION)
                .metadata(metadata)
                .build();

            s3Client.putObject(putObj, RequestBody.fromFile(file));
            log.info("Object Upload. ObjectKey: " + objectKey);
        } catch (S3Exception e) {
            log.info(e.getMessage());
            throw new InternalServerException(AWS_S3_OBJECT_UPLOAD_ERROR);
        }
    }

    private void deleteObject(String objectKey) {
        S3Client s3Client = awsS3CredentialService.getS3Client();

        List<ObjectIdentifier> toDelete = new ArrayList<>();
        toDelete.add(ObjectIdentifier.builder()
            .key(objectKey)
            .build());

        try {
            DeleteObjectsRequest deleteTargetObject = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(d -> d.objects(toDelete).build())
                .build();

            DeleteObjectsResponse result = s3Client.deleteObjects(deleteTargetObject);
            log.info("Object deleted. " + result.deleted());
        } catch (S3Exception e) {
            log.info(e.getMessage());
            throw new InternalServerException(AWS_S3_OBJECT_DELETE_ERROR);
        }
    }


    private String createObjectKey(Long memberId, boolean isMain) {
        String uuid = UUID.randomUUID().toString();
        if (isMain) {
            return "v1/" + memberId + "/main/" + uuid;
        }
        return "v1/" + memberId + "/additional/" + uuid;
    }

    private void checkSavedProfileImage(Long memberId, boolean isMain) {
        profileImageRepository.findByMemberMemberIdAndIsMain(memberId, isMain)
            .ifPresent(profileImage -> {
                deleteObject(profileImage.getBasicUrl());
                profileImageRepository.deleteById(profileImage.getProfileImageId());
            });
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) {
        String uniqueFileName = UUID.randomUUID().toString();

        try {
            File tempFile = File.createTempFile(uniqueFileName + "_" + "prefix", "suffix",
                null);
            Files.copy(multipartFile.getInputStream(), tempFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
            tempFile.deleteOnExit();
            return tempFile;
        } catch (IOException e) {
            throw new InternalServerException(AWS_S3_FILE_CONVERSION_ERROR);
        }
    }
}
