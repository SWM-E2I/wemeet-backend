package com.e2i.wemeet.service.aws.s3;

import static com.e2i.wemeet.exception.ErrorCode.AWS_S3_FILE_CONVERSION_ERROR;
import static com.e2i.wemeet.exception.ErrorCode.AWS_S3_OBJECT_UPLOAD_ERROR;

import com.e2i.wemeet.exception.internal.InternalServerException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

@RequiredArgsConstructor
@Service
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private static final String WHOLE_PATH = "v1/";

    @Override
    public void upload(final MultipartFile multipartFile, final String objectKey, final String bucket) {
        File file = convertMultipartFileToFile(multipartFile);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", "image/jpg");
        metadata.put("content-length", String.valueOf(multipartFile.getSize()));

        try {
            PutObjectRequest putObj = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .metadata(metadata)
                .build();

            s3Client.putObject(putObj, RequestBody.fromFile(file));
        } catch (S3Exception e) {
            throw new InternalServerException(AWS_S3_OBJECT_UPLOAD_ERROR);
        }
    }

    @Override
    public void delete(final String bucket, final String targetObjectKeyPrefix) {
        checkTargetKeyPrefixValid(targetObjectKeyPrefix);
        List<String> targetKeys = getObjectKeys(bucket, targetObjectKeyPrefix);

        targetKeys.stream()
            .map(key -> DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()
            )
            .forEach(s3Client::deleteObject);
    }

    // S3 전체 리소스 삭제 방지
    private void checkTargetKeyPrefixValid(String targetObjectKeyPrefix) {
        if (!StringUtils.hasText(targetObjectKeyPrefix) || targetObjectKeyPrefix.equals("/")) {
            throw new IllegalArgumentException("S3 object key prefix must not be empty");
        }

        for (int i = 1; i <= WHOLE_PATH.length(); i++) {
            if (targetObjectKeyPrefix.equals(WHOLE_PATH.substring(0, i))) {
                throw new IllegalArgumentException("S3 object key prefix must not be whole path");
            }
        }
    }

    // 주어진 버킷에서 주어진 prefix 가 붙은 object의 key 목록을 조회한다.
    private List<String> getObjectKeys(String bucket, String targetObjectKeyPrefix) {
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
            .bucket(bucket)
            .prefix(targetObjectKeyPrefix)
            .build();

        return s3Client.listObjectsV2(listObjectsV2Request).contents()
            .stream()
            .map(S3Object::key)
            .toList();
    }

    private static File convertMultipartFileToFile(MultipartFile multipartFile) {
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
