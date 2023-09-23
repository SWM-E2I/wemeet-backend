package com.e2i.wemeet.service.credential.sms;


public interface SmsCredentialService {

    void issue(String receiveTarget);

    boolean matches(String target, String input);
}
