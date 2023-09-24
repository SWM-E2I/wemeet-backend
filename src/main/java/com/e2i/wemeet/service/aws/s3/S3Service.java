package com.e2i.wemeet.service.aws.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    /*
     * S3 오브젝트 업로드
     * */
    void upload(MultipartFile multipartFile, String objectKey, String bucket);

    /*
     * S3 오브젝트 삭제
     * */
    void delete(String bucket, String targetObjectKeyPrefix);
}
