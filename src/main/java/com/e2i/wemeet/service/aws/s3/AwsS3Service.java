package com.e2i.wemeet.service.aws.s3;

import static com.e2i.wemeet.exception.ErrorCode.AWS_S3_OBJECT_UPLOAD_ERROR;

import com.e2i.wemeet.exception.internal.InternalServerException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsS3Service {

    @Value("${aws.s3.bucket}")
    private String bucket;

    private final AwsS3CredentialService awsS3CredentialService;

    public String putObject(MultipartFile multipartFile, String directory) throws IOException {
        S3Client s3Client = awsS3CredentialService.getS3Client();

        String objectKey = createObjectKey(directory, multipartFile.getOriginalFilename());

        File file = convertMultipartFileToFile(multipartFile);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("content-type", multipartFile.getContentType());
        metadata.put("content-length", String.valueOf(multipartFile.getSize()));

        try {
            PutObjectRequest putObject = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .metadata(metadata)
                .build();

            s3Client.putObject(putObject, RequestBody.fromFile(file));
            log.info("Object Upload. ObjectKey: " + objectKey);
        } catch (S3Exception e) {
            log.info(e.getMessage());
            throw new InternalServerException(AWS_S3_OBJECT_UPLOAD_ERROR);
        }

        return objectKey;
    }

    private String createObjectKey(String directory, String fileName) {
        String pathDelimiter = "/";
        String uuid = UUID.randomUUID().toString();

        return directory + pathDelimiter + uuid + "_" + fileName;
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = File.createTempFile(multipartFile.getOriginalFilename(), null);
        multipartFile.transferTo(file);
        return file;
    }
}
