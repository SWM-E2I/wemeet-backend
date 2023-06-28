package com.e2i.wemeet.service.aws.s3;

import static com.e2i.wemeet.exception.ErrorCode.AWS_S3_FILE_CONVERSION_ERROR;
import static com.e2i.wemeet.exception.ErrorCode.AWS_S3_OBJECT_DELETE_ERROR;
import static com.e2i.wemeet.exception.ErrorCode.AWS_S3_OBJECT_UPLOAD_ERROR;

import com.e2i.wemeet.exception.internal.InternalServerException;
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
public class AwsS3Service {

    @Value("${aws.s3.bucket}")
    private String bucket;

    private final AwsS3CredentialService awsS3CredentialService;

    public String putObject(MultipartFile multipartFile, String directory) {
        S3Client s3Client = awsS3CredentialService.getS3Client();

        String objectKey = createObjectKey(directory);

        File file = convertMultipartFileToFile(multipartFile);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("content-type", multipartFile.getContentType());
        metadata.put("content-length", String.valueOf(multipartFile.getSize()));
        try {
            PutObjectRequest putObj = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .metadata(metadata)
                .build();

            s3Client.putObject(putObj, RequestBody.fromFile(file));
            log.info("Object Upload. ObjectKey: " + objectKey);
        } catch (S3Exception e) {
            log.info(e.getMessage());
            throw new InternalServerException(AWS_S3_OBJECT_UPLOAD_ERROR);
        }

        return objectKey;
    }

    public String deleteObject(String objectKey) {
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

        return objectKey;
    }

    private String createObjectKey(String directory) {
        String pathDelimiter = "/";
        String uuid = UUID.randomUUID().toString();

        return directory + pathDelimiter + uuid;
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
