package com.e2i.wemeet.service.credential;

import com.e2i.wemeet.exception.notfound.SmsCredentialNotFoundException;
import java.time.Duration;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/*
* SMS 인증 기능
* */
@RequiredArgsConstructor
@Service
public class SmsCredentialService implements CredentialService {

    private final RedisTemplate<String, String> redisTemplate;

    /*
    * TODO
    * SMS 발송 로직 추가 필요
    * */
    @Override
    public void issue(String receiveTarget) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String credential = generateCredential();

        operations.set(receiveTarget, credential, Duration.ofMinutes(10));
    }

    /*
    * 사용자가 입력한 인증 번호 검증
    * target = phone, email
    * input = user input credential
     */
    @Override
    public boolean matches(String target, String input) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String origin = operations.get(target);

        if (!StringUtils.hasText(origin)) {
            throw new SmsCredentialNotFoundException();
        }

        return origin.equals(input);
    }

    // 100000 ~ 999999
    private String generateCredential() {
        int credential = new Random().nextInt(900_000) + 100_000;
        return String.valueOf(credential);
    }
}
