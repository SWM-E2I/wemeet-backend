package com.e2i.wemeet.domain.team.data;

import com.e2i.wemeet.exception.badrequest.InvalidDatabaseKeyToEnumException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Region {
    HONGDAE(1, "홍대"),
    GANGNAM(2, "강남"),
    SINCHON(3, "신촌"),
    GUNDAE(4, "건대입구"),
    HYEHWA(5, "혜화"),
    MYEONGDONG(6, "명동"),
    SEOMYEON(7, "서면"),
    HAEUNDAE(8, "해운대"),
    BUPYEONG(9, "부평"),
    ANYANG(10, "안양역"),
    CHUNGJANGRO(11, "충장로"),
    DAEJEON(12, "대전역"),
    BANWOLDANG(13, "반월당"),
    DUJEONG(14, "두정역"),
    SUWON(15, "수원역");


    private final int key;
    private final String name;

    Region(int key, String name) {
        this.key = key;
        this.name = name;
    }

    public static Region findByKey(int key) {
        return Arrays.stream(Region.values())
            .filter(region -> region.getKey() == key)
            .findFirst()
            .orElseThrow(InvalidDatabaseKeyToEnumException::new);
    }
}
