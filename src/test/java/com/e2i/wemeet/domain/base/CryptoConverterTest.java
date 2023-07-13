package com.e2i.wemeet.domain.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.e2i.wemeet.support.config.AbstractIntegrationTest;
import com.e2i.wemeet.util.encryption.TwoWayEncryption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("CryptoConverter 테스트")
class CryptoConverterTest extends AbstractIntegrationTest {

    @Autowired
    private CryptoConverter cryptoConverter;

    @Autowired
    private TwoWayEncryption twoWayEncryption;

    @DisplayName("암호화에 성공한다.")
    @Test
    void convertToDatabaseColumn() {
        // given
        final String phone = "+821077229911";

        // when
        String encrypted = cryptoConverter.convertToDatabaseColumn(phone);

        // then
        assertThat(encrypted).isNotEqualTo(phone);
    }

    @DisplayName("복호화에 성공한다.")
    @Test
    void convertToEntityAttribute() {
        // given
        final String phone = "+821077229911";
        String encrypted = twoWayEncryption.encrypt(phone);

        // when
        String decrypt = cryptoConverter.convertToEntityAttribute(encrypted);

        // then
        assertThat(decrypt).isNotEqualTo(encrypted).isEqualTo(phone);
    }

}