package com.e2i.wemeet.util.encryption;

import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.support.config.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

class AdvancedEncryptionStandardTest extends AbstractIntegrationTest {

    @Autowired
    private TwoWayEncryption twoWayEncryption;

    @DisplayName("암호화에 성공한다.")
    @ValueSource(strings = {"+821077229911", "+821077229912", "+821077229913", "+821077229914", "+821077229915"})
    @ParameterizedTest
    void encrypt(String phone) {
        // given
        String encrypted = twoWayEncryption.encrypt(phone);

        // when
        assertThat(encrypted).isNotEqualTo(phone);
    }

    @DisplayName("복호화에 성공한다.")
    @ValueSource(strings = {"+821077229911", "+821077229912", "+821077229913", "+821077229914", "+821077229915"})
    @ParameterizedTest
    void decrypt(String phone) {
        //given
        String encrypted = twoWayEncryption.encrypt(phone);

        //when
        String decrypt = twoWayEncryption.decrypt(encrypted);

        //then
        assertThat(decrypt).isNotEqualTo(encrypted).isEqualTo(phone);
    }
}