package com.e2i.wemeet.service.aws.s3;

import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.support.module.AbstractServiceTest;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

@Profile("test")
class S3ServiceImplTest extends AbstractServiceTest {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.testBucket}")
    public String testBucket;

    @DisplayName("S3에 오브젝트를 업로드하는데 성공한다.")
        //@Test
    void upload() {
        // given
        final String objectKey = "v1/test/" + UUID.randomUUID();
        MockMultipartFile uploadFile = new MockMultipartFile("file", "test.jpg", "image/jpg", "test".getBytes());

        // when
        s3Service.upload(uploadFile, objectKey, testBucket);
    }

    @DisplayName("S3에 오브젝트를 업로드하고 삭제하는데 성공한다.")
    @TestFactory
    Collection<DynamicTest> testAllFeature() {
        // given
        final String objectPath = "v1/test/";
        final String objectKey1 = objectPath + "1/" + UUID.randomUUID();
        final String objectKey2 = objectPath + "2/" + UUID.randomUUID();
        MockMultipartFile uploadFile1 = new MockMultipartFile("file1", "test1.jpg", "image/jpg", "test1".getBytes());
        MockMultipartFile uploadFile2 = new MockMultipartFile("file2", "test2.jpg", "image/jpg", "test2".getBytes());

        // when & then
        return List.of(
            // upload Test
            DynamicTest.dynamicTest("S3에 오브젝트를 업로드한다.", () -> {
                // when
                s3Service.upload(uploadFile1, objectKey1, testBucket);
                s3Service.upload(uploadFile2, objectKey2, testBucket);

                // then
                ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                    .bucket(testBucket)
                    .prefix(objectPath)
                    .build();
                ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
                List<String> findKeys = listObjectsV2Response.contents().stream().map(S3Object::key).toList();

                assertThat(findKeys).hasSize(2)
                    .containsOnly(objectKey1, objectKey2);
            }),
            // delete Test
            DynamicTest.dynamicTest("S3에 업로드한 오브젝트를 삭제한다.", () -> {
                // when
                s3Service.delete(testBucket, objectPath);

                // then
                ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                    .bucket(testBucket)
                    .prefix(objectPath)
                    .build();

                List<S3Object> contents = s3Client.listObjectsV2(listObjectsV2Request).contents();
                assertThat(contents).isEmpty();
            })
        );
    }

    @DisplayName("S3 오브젝트 삭제에 성공한다.")
        //@Test
    void deleteWithClient() {
        // given
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
            .bucket("wemeet-static-profile-image")
            .prefix("character_6")
            .build();
        List<String> targetKey = s3Client.listObjectsV2(listObjectsV2Request).contents()
            .stream()
            .map(S3Object::key)
            .toList();

        // when
        targetKey.stream()
            .map(key -> DeleteObjectRequest.builder()
                .bucket("wemeet-static-profile-image")
                .key(key)
                .build()
            )
            .forEach(deleteObjectRequest -> s3Client.deleteObject(deleteObjectRequest));
    }

    @DisplayName("특정 버킷의 오브젝트 목록을 조회한다.")
    @Test
    void list() {
        // given
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
            .bucket("wemeet-static-team-image")
            .prefix("v1/")
            .build();

        // when
        ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);

        // then
        listObjectsV2Response.contents()
            .stream()
            .map(S3Object::key)
            .forEach(System.out::println);
    }
}