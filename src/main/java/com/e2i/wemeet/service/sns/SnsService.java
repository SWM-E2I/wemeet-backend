package com.e2i.wemeet.service.sns;

public interface SnsService {

    /*
     * SNS 문자 발행
     * */
    void issue(SnsEvent snsEvent);

}
