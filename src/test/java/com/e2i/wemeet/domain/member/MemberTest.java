package com.e2i.wemeet.domain.member;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.e2i.wemeet.dto.request.member.UpdateMemberRequestDto;
import com.e2i.wemeet.exception.badrequest.InvalidDataFormatException;
import com.e2i.wemeet.exception.badrequest.InvalidMbtiException;
import com.e2i.wemeet.exception.badrequest.TeamExistsException;
import com.e2i.wemeet.support.fixture.TeamMemberFixture;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MemberTest {

    @DisplayName("회원 탈퇴에 성공한다.")
    @Test
    void deleteSuccess() {
        // given
        Member kai = KAI.create();

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
        Member kai = KAI.create();
        HONGDAE_TEAM_1.create(kai, TeamMemberFixture.create_3_man());

        // when
        LocalDateTime deleteAt = LocalDateTime.now();

        // then
        assertThatThrownBy(() -> kai.delete(deleteAt))
            .isExactlyInstanceOf(TeamExistsException.class);
    }

    @DisplayName("닉네임과 MBTI를 변경할 수 있다.")
    @Test
    void updateNicknameMbti() {
        // given
        Member kai = KAI.create();
        UpdateMemberRequestDto updateRequest = UpdateMemberRequestDto.builder()
            .nickname("신기우")
            .mbti("ISTJ")
            .build();

        // when
        kai.update(updateRequest);

        // then
        assertThat(kai.getNickname()).isEqualTo("신기우");
        assertThat(kai.getMbti().name()).isEqualTo("ISTJ");
    }

    @DisplayName("변경할 닉네임의 길이가 2 ~ 10자의 길이가 아니라면 닉네임을 변경할 수 없다.")
    @ValueSource(strings = {"기", "", "열한글자마에스트로이다", "띄 어 쓰 기 로 열"})
    @ParameterizedTest
    void updateFailNicknameLength(String nickname) {
        // given
        Member kai = KAI.create();
        UpdateMemberRequestDto updateRequest = UpdateMemberRequestDto.builder()
            .nickname(nickname)
            .mbti("ISTJ")
            .build();

        // when & then
        assertThatThrownBy(() -> kai.update(updateRequest))
            .isInstanceOf(InvalidDataFormatException.class);
    }

    @DisplayName("MBTI 값이 잘못되었다면 MBTI를 변경할 수 없다.")
    @ValueSource(strings = {"NONE", "ANFJ", "XXXI", "ISTA"})
    @ParameterizedTest
    void updateFailMbti(String mbti) {
        // given
        Member kai = KAI.create();
        UpdateMemberRequestDto updateRequest = UpdateMemberRequestDto.builder()
            .nickname("신기우")
            .mbti(mbti)
            .build();

        // when & then
        assertThatThrownBy(() -> kai.update(updateRequest))
            .isInstanceOf(InvalidMbtiException.class);
    }
}