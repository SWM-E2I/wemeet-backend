package com.e2i.wemeet.util.validator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public abstract class CustomExpirationValidator {

    private CustomExpirationValidator() {
        throw new AssertionError();
    }

    // 만료일이 지났다면 true를 반환
    public static boolean isExpiredOfDays(final LocalDateTime target, final LocalDateTime executeTime, final int days) {
        long daysDifference = ChronoUnit.DAYS.between(target, executeTime);

        return Math.abs(daysDifference) >= days;
    }

}
