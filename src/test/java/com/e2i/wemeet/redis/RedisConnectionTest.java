package com.e2i.wemeet.redis;

import com.e2i.wemeet.support.config.AbstractIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class RedisConnectionTest extends AbstractIntegrationTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @DisplayName("Redis 에서 데이터를 조회하는데 성공한다.")
    @Test
    void load() {
        final String key = "01083215123";
        final String value = "123456";

        ValueOperations<String, String> operation = redisTemplate.opsForValue();
        operation.set(key, value);

        String fromRedis = operation.get(key);
        Assertions.assertThat(fromRedis).isEqualTo(value);
    }

    @DisplayName("Redis 에 존재하지 않는 키값을 조회하면 Null이 반환된다.")
    @Test
    void isNull() {
        final String key = "01083215123";
        final String value = "1234568";

        ValueOperations<String, String> operation = redisTemplate.opsForValue();
        operation.set(key, value);

        String fromRedis = operation.get("123");
        Assertions.assertThat(fromRedis).isNull();
    }
}
