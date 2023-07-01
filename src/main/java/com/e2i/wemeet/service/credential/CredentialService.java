package com.e2i.wemeet.service.credential;

// SMS 인증, Email 인증 인터페이스
public interface CredentialService {
    void issue(String receiveTarget);

    boolean matches(String target, String input);
}
