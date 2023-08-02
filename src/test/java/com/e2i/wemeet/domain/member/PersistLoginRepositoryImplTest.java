package com.e2i.wemeet.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.e2i.wemeet.domain.member.data.CollegeInfo;
import com.e2i.wemeet.domain.profile_image.ProfileImage;
import com.e2i.wemeet.domain.profile_image.ProfileImageRepository;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.dto.response.persist.PersistResponseDto;
import com.e2i.wemeet.support.config.RepositoryTest;
import com.e2i.wemeet.support.fixture.MemberFixture;
import com.e2i.wemeet.support.fixture.ProfileImageFixture;
import com.e2i.wemeet.support.fixture.TeamFixture;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@RepositoryTest
class PersistLoginRepositoryImplTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private ProfileImageRepository profileImageRepository;

    @DisplayName("사용자의 persist 정보를 가져오는데 성공한다.")
    @Test
    void persistLogin() {
        // given
        Member kai = memberRepository.save(MemberFixture.KAI.create());
        teamRepository.save(TeamFixture.HONGDAE_TEAM.create(kai));

        ProfileImage kaiProfile = ProfileImageFixture.MAIN_IMAGE.create(kai);
        profileImageRepository.save(kaiProfile);
        entityManager.flush();

        // when
        PersistResponseDto persistResponseDto = memberRepository.findPersistResponseById(
            kai.getMemberId());

        // then
        assertAll(
            () -> assertThat(persistResponseDto).isNotNull(),
            () -> assertThat(persistResponseDto.emailAuthenticated()).isTrue(),
            () -> assertThat(persistResponseDto.hasTeam()).isTrue(),
            () -> assertThat(persistResponseDto.hasMainProfileImage()).isTrue(),
            () -> assertThat(persistResponseDto.nickname()).isEqualTo(
                MemberFixture.KAI.getNickname()),
            () -> assertThat(persistResponseDto.preferenceCompleted()).isTrue(),
            () -> assertThat(persistResponseDto.profileImageAuthenticated()).isTrue()
        );
    }

    @DisplayName("사용자가 이메일 인증을 진행하지 않았을 경우, emailAuthenticated 는 false 를 반환한다.")
    @Test
    void persistLoginNoEmail() {
        // given
        CollegeInfo collegeInfo = CollegeInfo.builder()
            .college("한국대학교")
            .collegeType("이공계열")
            .admissionYear("17")
            .mail(null)
            .build();
        Member kai = memberRepository.save(MemberFixture.KAI.create_college(collegeInfo));

        // when
        PersistResponseDto persistResponseDto = memberRepository.findPersistResponseById(
            kai.getMemberId());

        // then
        assertAll(
            () -> assertThat(persistResponseDto).isNotNull(),
            () -> assertThat(persistResponseDto.hasTeam()).isFalse()
        );
    }

    @DisplayName("사용자가 Team 이 없을 경우, hasTeam 은 false 를 반환한다.")
    @Test
    void persistLoginNoTeam() {
        // given
        Member kai = memberRepository.save(MemberFixture.KAI.create());

        // when
        PersistResponseDto persistResponseDto = memberRepository.findPersistResponseById(
            kai.getMemberId());

        // then
        assertAll(
            () -> assertThat(persistResponseDto).isNotNull(),
            () -> assertThat(persistResponseDto.hasTeam()).isFalse()
        );
    }

    @DisplayName("사용자가 ProfileImage 가 없을 경우, hasMainProfileImage 는 false 를 반환한다.")
    @Test
    void persistLoginNoProfile() {
        // given
        Member kai = memberRepository.save(MemberFixture.KAI.create());

        // when
        PersistResponseDto persistResponseDto = memberRepository.findPersistResponseById(
            kai.getMemberId());

        // then
        assertAll(
            () -> assertThat(persistResponseDto).isNotNull(),
            () -> assertThat(persistResponseDto.hasMainProfileImage()).isFalse(),
            () -> assertThat(persistResponseDto.profileImageAuthenticated()).isFalse()
        );
    }

    @DisplayName("사용자가 선호 상대에 대한 정보를 입력하지 않았을 경우, preferenceCompleted 는 false 를 반환한다.")
    @Test
    void persistLoginNoPreference() {
        // given
        Member kai = memberRepository.save(MemberFixture.KAI.create_preference(null));

        // when
        PersistResponseDto persistResponseDto = memberRepository.findPersistResponseById(
            kai.getMemberId());

        // then
        assertAll(
            () -> assertThat(persistResponseDto).isNotNull(),
            () -> assertThat(persistResponseDto.preferenceCompleted()).isFalse()
        );
    }
}