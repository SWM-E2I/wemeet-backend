package com.e2i.wemeet.controller.admin;

import static com.e2i.wemeet.controller.admin.AdminMemberFixture.KAI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.data.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AdminMemberFixture 테스트")
class AdminMemberFixtureTest {

    @Test
    void create() {
        assertThat(KAI.create()).isExactlyInstanceOf(Member.class);
    }

    @Test
    void createWithCredit() {
        Member member = KAI.createWithCredit(1000);
        assertThat(member.getCredit()).isEqualTo(1000);
    }

    @Test
    void createWithRole() {
        Member member = KAI.createWithRole(Role.MANAGER);
        assertThat(member.getRole()).isEqualTo(Role.MANAGER);
    }

    @Test
    void createWithRoleCredit() {
        Member member = KAI.createWithRoleCredit(Role.MANAGER, 1000);
        assertAll(
            () -> assertThat(member.getRole()).isEqualTo(Role.MANAGER),
            () -> assertThat(member.getCredit()).isEqualTo(1000)
        );
    }

    @Test
    void getFixture() {
        final String name = "KAI";
        assertThat(AdminMemberFixture.getFixture(name)).isEqualTo(KAI);
    }
}