package com.e2i.wemeet.service.aws.s3;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.exception.internal.InternalServerException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@ExtendWith(MockitoExtension.class)
class AwsS3ServiceTest {

    @InjectMocks
    private AwsS3Service awsS3Service;

    @Mock
    private AwsS3CredentialService awsS3CredentialService;

    @Mock
    private S3Client s3Client;
    String objectKey = "test-directory/1234_test-file.txt";

    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        when(awsS3CredentialService.getS3Client()).thenReturn(s3Client);

        byte[] content = "Test file content".getBytes();
        multipartFile = new MockMultipartFile("test-file.txt", "test-file.txt",
            "text/plain", content);
    }

    @Test
    void testPutObject() {
        String result = awsS3Service.putObject(multipartFile, "test-directory");

        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        String regexPattern = "test-directory/[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89aAbB][a-f0-9]{3}-[a-f0-9]{12}";
        MatcherAssert.assertThat(result, Matchers.matchesPattern(regexPattern));
    }

    @Test
    void testPutObject_Failure() {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenThrow(
            S3Exception.class);

        assertThatThrownBy(() -> awsS3Service.putObject(multipartFile, "test-directory"))
            .isExactlyInstanceOf((InternalServerException.class));
    }

    @Test
    void testDeleteObject() {
        DeleteObjectsResponse deleteObjectsResponse = DeleteObjectsResponse.builder().build();
        when(s3Client.deleteObjects(any(DeleteObjectsRequest.class))).thenReturn(
            deleteObjectsResponse);
        String result = awsS3Service.deleteObject(objectKey);

        verify(s3Client).deleteObjects(any(DeleteObjectsRequest.class));
        assertEquals(objectKey, result);
    }

    @Test
    void testDeleteObject_Failure() {
        when(s3Client.deleteObjects(any(DeleteObjectsRequest.class))).thenThrow(
            S3Exception.class);

        assertThatThrownBy(() -> awsS3Service.deleteObject(objectKey))
            .isExactlyInstanceOf((InternalServerException.class));
    }
}