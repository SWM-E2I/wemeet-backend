package com.e2i.wemeet.util.encryption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EncryptionUtilsTest {

    @DisplayName("해시 데이터를 생성하는데 성공한다")
    @ValueSource(strings = {"kia", "email@email.com", "+821012345678"})
    @ParameterizedTest
    void hashData(final String data) {
        // when
        String encryptedData = EncryptionUtils.hashData(data);

        // then
        assertThat(encryptedData).isNotEqualTo(data);
    }
}