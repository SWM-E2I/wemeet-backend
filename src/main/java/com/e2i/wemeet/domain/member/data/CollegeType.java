package com.e2i.wemeet.domain.member.data;

import com.e2i.wemeet.exception.badrequest.InvalidDatabaseKeyToEnumException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum CollegeType {
    ETC(0, "그 외"),
    SOCIAL(1, "인문사회"),
    ENGINEERING(2, "자연공학"),
    ARTS(3, "예체능"),
    EDUCATION(4, "교육"),
    MEDICINE(5, "의약"),
    ;

    private final int key;
    private final String description;

    CollegeType(int key, String description) {
        this.key = key;
        this.description = description;
    }

    public static CollegeType findByKey(int key) {
        return Arrays.stream(CollegeType.values())
            .filter(collegeType -> collegeType.getKey() == key)
            .findFirst()
            .orElseThrow(InvalidDatabaseKeyToEnumException::new);
    }
}
