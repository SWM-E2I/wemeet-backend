package com.e2i.wemeet.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.e2i.wemeet.exception.badrequest.TeamExistsException;
import com.e2i.wemeet.support.fixture.MemberFixture;
import com.e2i.wemeet.support.fixture.TeamFixture;
import com.e2i.wemeet.support.fixture.TeamMemberFixture;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    @DisplayName("회원 탈퇴에 성공한다.")
    @Test
    void deleteSuccess() {
        // given
        Member kai = MemberFixture.KAI.create();

        // when
        LocalDateTime deleteAt = LocalDateTime.now();
        kai.delete(deleteAt);

        // then
        assertThat(kai.getDeletedAt()).isEqualTo(deleteAt);
    }

    @DisplayName("팀이 있다면 회원 탈퇴를 할 수 없다.")
    @Test
    void deleteFailHasTeam() {
        // given
        Member kai = MemberFixture.KAI.create();
        TeamFixture.HONGDAE_TEAM_1.create(kai, TeamMemberFixture.create_3_man());

        // when
        LocalDateTime deleteAt = LocalDateTime.now();

        // then
        assertThatThrownBy(() -> kai.delete(deleteAt))
            .isExactlyInstanceOf(TeamExistsException.class);
    }

}