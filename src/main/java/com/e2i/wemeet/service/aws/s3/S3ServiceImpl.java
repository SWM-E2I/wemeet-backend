package com.e2i.wemeet.service.aws.s3;

import static com.e2i.wemeet.exception.ErrorCode.AWS_S3_FILE_CONVERSION_ERROR;
import static com.e2i.wemeet.exception.ErrorCode.AWS_S3_OBJECT_UPLOAD_ERROR;

import com.e2i.wemeet.exception.internal.InternalServerException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RequiredArgsConstructor
@Service
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    @Override
    public void upload(MultipartFile multipartFile, String objectKey, String bucket) {
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
