package com.e2i.wemeet.service.credential.email;

public interface EmailCredentialService {

    /*
     * 인증 번호 이메일 전송
     */

    void issue(String receiveTarget);

    /*
     * 이메일 인증 번호 확인
     */

    boolean matches(String target, String input, Long memberId);
}
