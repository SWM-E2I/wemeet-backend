package com.e2i.wemeet;

import com.e2i.wemeet.security.token.Payload;
import com.e2i.wemeet.security.token.handler.AccessTokenHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AccessTokenTest {

    @Autowired
    private AccessTokenHandler accessTokenHandler;

    @Test
    void createAccessToken() {
        String accessToken = accessTokenHandler.createTokenWithNotExpired(new Payload(100001L, "MANAGER"));
        System.out.println("accessToken = " + accessToken);
    }

}
