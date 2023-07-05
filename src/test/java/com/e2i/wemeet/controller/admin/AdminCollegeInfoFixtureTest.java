package com.e2i.wemeet.controller.admin;

import static com.e2i.wemeet.controller.admin.AdminCollegeInfoFixture.KU;
import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.domain.member.CollegeInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AdminCollegeInfoFixture 테스트")
class AdminCollegeInfoFixtureTest {

    @Test
    void create() {
        assertThat(KU.create()).isExactlyInstanceOf(CollegeInfo.class);
    }

    @Test
    void getAdmissionYear() {
        assertThat(KU.getAdmissionYear()).isGreaterThan(2000);
    }

    @Test
    void getCollegeType() {
        assertThat(KU.getCollegeType()).isExactlyInstanceOf(String.class);
    }

    @Test
    void getCollege() {
        assertThat(KU.getCollege()).isExactlyInstanceOf(String.class);
    }

    @Test
    void getMail() {
        assertThat(KU.getMail()).isExactlyInstanceOf(String.class);
    }
}