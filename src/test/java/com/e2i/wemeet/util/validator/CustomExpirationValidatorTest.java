package com.e2i.wemeet.util.validator;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CustomExpirationValidatorTest {

    // given
    private static Stream<Arguments> provideTwoDaysDifference() {
        return Stream.of(
            Arguments.of(
                LocalDateTime.of(2023, 8, 10, 14, 0),
                LocalDateTime.of(2023, 8, 13, 13, 59),
                3, false),
            Arguments.of(
                LocalDateTime.of(2023, 8, 10, 14, 0),
                LocalDateTime.of(2023, 8, 13, 14, 0),
                3, true),
            Arguments.of(
                LocalDateTime.of(2023, 8, 10, 14, 0),
                LocalDateTime.of(2023, 8, 13, 14, 1),
                3, true)
        );
    }

    @DisplayName("두 날짜의 차이가 days 이상이면 true, 아니라면 false를 반환한다.")
    @MethodSource("provideTwoDaysDifference")
    @ParameterizedTest
    void checkDaysDifferenceWithIn3(LocalDateTime target, LocalDateTime time, int days, boolean expected) {
        // when
        boolean expired = CustomExpirationValidator.isExpiredOfDays(target, time, days);

        // then
        assertThat(expired).isEqualTo(expected);
    }

}