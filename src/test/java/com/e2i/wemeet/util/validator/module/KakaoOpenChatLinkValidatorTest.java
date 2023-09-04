package com.e2i.wemeet.util.validator.module;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class KakaoOpenChatLinkValidatorTest {

    private KakaoOpenChatLinkValidator kakaoOpenChatLinkValidator = new KakaoOpenChatLinkValidator();

    @DisplayName("주어진 문자열이 오픈 카카오톡 링크인지 검증할 수 있다.")
    @CsvSource({
        "https://open.kakao.com/o/S13kdfs1, true",
        "https://open.kakao.com/o/a2r5zqs1, true",
        "https://open.daum.com/o/gjgjgjgj, false",
        "https://open.kakao.com/gjgjgjgj, false"
    })
    @ParameterizedTest
    void validateWithKakaoOpenChatLink(String kakaoChatLink, boolean expected) {
        // when
        boolean valid = kakaoOpenChatLinkValidator.isValid(kakaoChatLink, null);

        // then
        assertThat(valid).isEqualTo(expected);
    }
}