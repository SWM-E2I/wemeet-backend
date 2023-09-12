package com.e2i.wemeet.redis;

import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.support.module.AbstractIntegrationTest;
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
        assertThat(fromRedis).isEqualTo(value);
    }

    @DisplayName("Redis 에 존재하지 않는 키값을 조회하면 Null이 반환된다.")
    @Test
    void isNull() {
        final String key = "01083215123";
        final String value = "1234568";

        ValueOperations<String, String> operation = redisTemplate.opsForValue();
        operation.set(key, value);

        String fromRedis = operation.get("123");
        assertThat(fromRedis).isNull();
    }

    @DisplayName("중복된 키값을 저장하면 덮어쓰기 된다.")
    @Test
    void duplicate() {
        // given
        final String key = "01083215123";
        final String value = "123456";
        final String secondValue = "987643";

        // when
        ValueOperations<String, String> operation = redisTemplate.opsForValue();
        operation.set(key, value);
        operation.set(key, secondValue);

        // then
        String fromRedis = operation.get(key);
        assertThat(fromRedis).isEqualTo(secondValue);
    }

}
