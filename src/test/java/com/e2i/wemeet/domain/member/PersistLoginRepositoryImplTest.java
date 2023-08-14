package com.e2i.wemeet.domain.member;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.domain.member.data.ProfileImage;
import com.e2i.wemeet.domain.member.persist.PersistLoginRepository;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.dto.response.persist.PersistResponseDto;
import com.e2i.wemeet.support.fixture.TeamMemberFixture;
import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PersistLoginRepositoryImplTest extends AbstractRepositoryUnitTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PersistLoginRepository persistLoginRepository;

    @DisplayName("사용자의 상태 정보를 가져올 수 있다.")
    @Test
    void persistLogin() {
        // given
        Member kai = memberRepository.save(KAI.create_image_auth(true));
        teamRepository.save(HONGDAE_TEAM_1.create(kai, TeamMemberFixture.create_3_man()));
        Long kaiId = kai.getMemberId();

        // when
        PersistResponseDto response = persistLoginRepository.findPersistResponseById(kaiId)
            .orElseThrow();

        // then
        assertThat(response)
            .isNotNull()
            .extracting(
                "nickname", "emailAuthenticated",
                "hasMainProfileImage", "basicProfileImage", "lowProfileImage",
                "profileImageAuthenticated", "hasTeam")
            .contains(
                KAI.getNickname(), true,
                true, KAI.getBasicUrl(), KAI.getLowUrl(),
                true, true
            );
    }

    @DisplayName("잘못된 ID를 입력하면 사용자의 정보를 조회할 수 없다.")
    @Test
    void test() {
        // given
        final Long invalidId = 99999L;

        // when
        Optional<PersistResponseDto> response = persistLoginRepository.findPersistResponseById(invalidId);

        // then
        assertThat(response)
            .isEmpty();
    }

    @DisplayName("사용자가 대학 인증을 하지 않았을 경우, emailAuthenticated 는 false 를 반환한다.")
    @Test
    void persistLoginNoEmail() {
        // given
        Member kai = memberRepository.save(KAI.create_email(null));
        teamRepository.save(HONGDAE_TEAM_1.create(kai, TeamMemberFixture.create_3_man()));
        Long kaiId = kai.getMemberId();

        // when
        PersistResponseDto response = persistLoginRepository.findPersistResponseById(kaiId)
            .orElseThrow();

        // then
        assertThat(response)
            .isNotNull()
            .extracting(
                "nickname", "emailAuthenticated",
                "hasMainProfileImage", "basicProfileImage", "lowProfileImage",
                "profileImageAuthenticated", "hasTeam")
            .contains(
                KAI.getNickname(), false,
                true, KAI.getBasicUrl(), KAI.getLowUrl(),
                false, true
            );
    }

    @DisplayName("사용자 프로필 이미지가 없을 경우, hasMainProfileImage 와 이미지 url 정보는 null을 반환한다.")
    @Test
    void persistLoginNoTeam() {
        // given
        ProfileImage noProfileImage = new ProfileImage(null, null, false);
        Long kaiId = memberRepository.save(KAI.create_profile_image(noProfileImage)).getMemberId();

        // when
        PersistResponseDto response = persistLoginRepository.findPersistResponseById(kaiId)
            .orElseThrow();

        // then
        assertThat(response)
            .isNotNull()
            .extracting(
                "nickname", "emailAuthenticated",
                "hasMainProfileImage", "basicProfileImage", "lowProfileImage",
                "profileImageAuthenticated", "hasTeam")
            .contains(
                KAI.getNickname(), true,
                false, null, null,
                false, true
            );
    }

    @DisplayName("사용자가 사진 인증을 하지 않았을 경우, profileImageAuthenticated 는 false 를 반환한다.")
    @Test
    void persistLoginNoProfile() {
        // given
        Member kai = memberRepository.save(KAI.create_image_auth(false));
        teamRepository.save(HONGDAE_TEAM_1.create(kai, TeamMemberFixture.create_3_man()));
        Long kaiId = kai.getMemberId();

        // when
        PersistResponseDto response = persistLoginRepository.findPersistResponseById(kaiId)
            .orElseThrow();

        // then
        assertThat(response)
            .isNotNull()
            .extracting(
                "nickname", "emailAuthenticated",
                "hasMainProfileImage", "basicProfileImage", "lowProfileImage",
                "profileImageAuthenticated", "hasTeam")
            .contains(
                KAI.getNickname(), false,
                true, KAI.getBasicUrl(), KAI.getLowUrl(),
                false, true
            );
    }

    @DisplayName("사용자가 팀에 소속되어있지 않을 경우, hasTeam 은 false 를 반환한다.")
    @Test
    void persistLoginNoPreference() {
        // given
        Long kaiId = memberRepository.save(KAI.create()).getMemberId();

        // when
        PersistResponseDto response = persistLoginRepository.findPersistResponseById(kaiId)
            .orElseThrow();

        // then
        assertThat(response)
            .isNotNull()
            .extracting(
                "nickname", "emailAuthenticated",
                "hasMainProfileImage", "basicProfileImage", "lowProfileImage",
                "profileImageAuthenticated", "hasTeam")
            .contains(
                KAI.getNickname(), true,
                true, KAI.getBasicUrl(), KAI.getLowUrl(),
                false, false
            );
    }
}