package com.e2i.wemeet.domain.base.converter;

import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.domain.meeting.MeetingRequest;
import com.e2i.wemeet.domain.meeting.MeetingRequestRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.data.CollegeType;
import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.team.data.AdditionalActivity;
import com.e2i.wemeet.domain.team.data.DrinkWithGame;
import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.support.config.AbstractRepositoryUnitTest;
import com.e2i.wemeet.support.fixture.MeetingRequestFixture;
import com.e2i.wemeet.support.fixture.MemberFixture;
import com.e2i.wemeet.support.fixture.TeamFixture;
import com.e2i.wemeet.support.fixture.TeamMemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class JpaAttributeConverterTest extends AbstractRepositoryUnitTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MeetingRequestRepository meetingRequestRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Nested
    class AcceptStatusConverterTest {

        @DisplayName("데이터 베이스에 요청을 전송할 때, accept_status field에 key 값이 바인딩된다.")
        @Test
        void convertToDatabaseColumn() {
            // given
            Member rim = memberRepository.save(MemberFixture.RIM.create(WOMANS_CODE));
            Member kai = memberRepository.save(MemberFixture.KAI.create(ANYANG_CODE));

            Team womanTeam = teamRepository.save(
                TeamFixture.HONGDAE_TEAM_1.create(rim, TeamMemberFixture.create_3_woman())
            );
            Team manTeam = teamRepository.save(
                TeamFixture.HONGDAE_TEAM_1.create(kai, TeamMemberFixture.create_3_man())
            );

            MeetingRequest meetingRequest = meetingRequestRepository.save(
                MeetingRequestFixture.BASIC_REQUEST.create(womanTeam, manTeam)
            );
            entityManager.flush();

            // when
            final String sql = "SELECT accept_status FROM meeting_request WHERE meeting_request_id = :meetingRequestId";
            Integer acceptStatus = (Integer) entityManager.createNativeQuery(sql, Integer.class)
                .setParameter("meetingRequestId", meetingRequest.getMeetingRequestId())
                .getSingleResult();

            // then
            assertThat(acceptStatus).isEqualTo(0);
        }
    }

    @Nested
    class AdditionalActivityConverterTest {

        @DisplayName("데이터 베이스에 요청을 전송할 때, additional_activity field에 key 값이 바인딩된다.")
        @Test
        void convertToDatabaseColumn() {
            // given
            Member kai = memberRepository.save(MemberFixture.KAI.create(ANYANG_CODE));

            Team manTeam = teamRepository.save(
                TeamFixture.HONGDAE_TEAM_1.create_with_activity(kai, TeamMemberFixture.create_3_man(), AdditionalActivity.CAFE)
            );

            // when
            final String sql = "SELECT additional_activity FROM team WHERE team_id = :teamId";
            Integer acceptStatus = (Integer) entityManager.createNativeQuery(sql, Integer.class)
                .setParameter("teamId", manTeam.getTeamId())
                .getSingleResult();

            // then
            assertThat(acceptStatus).isEqualTo(5);
        }

        @DisplayName("데이터 베이스에서 가져온 값이 바인딩 될 때 Entity에 AdditionalActivity Enum이 바인딩된다")
        @Test
        void convertToEntityAttribute() {
            // given
            Member kai = memberRepository.save(MemberFixture.KAI.create(ANYANG_CODE));

            teamRepository.save(
                TeamFixture.HONGDAE_TEAM_1.create_with_activity(kai, TeamMemberFixture.create_3_man(), AdditionalActivity.CAFE)
            );

            // when
            final String sql = "SELECT t.additionalActivity FROM Team t WHERE t.additionalActivity = :additionalActivity";
            AdditionalActivity findAdditionalActivity = entityManager.createQuery(sql, AdditionalActivity.class)
                .setParameter("additionalActivity", AdditionalActivity.CAFE)
                .getSingleResult();

            // then
            assertThat(findAdditionalActivity).isEqualTo(AdditionalActivity.CAFE);
        }
    }

    @Nested
    class CollegeTypeConverterTest {

        @DisplayName("데이터 베이스에 요청을 전송할 때, college_type field에 key 값이 바인딩된다.")
        @Test
        void convertToDatabaseColumn() {
            // given
            Member kai = memberRepository.save(MemberFixture.KAI.create(ANYANG_CODE));

            // when
            final String sql = "SELECT college_type FROM member WHERE member_id = :memberId";
            Integer collegeType = (Integer) entityManager.createNativeQuery(sql, Integer.class)
                .setParameter("memberId", kai.getMemberId())
                .getSingleResult();

            // then
            assertThat(collegeType).isEqualTo(2);
        }

        @DisplayName("데이터 베이스에서 가져온 값이 바인딩 될 때 Entity에 CollegeType Enum이 바인딩된다")
        @Test
        void convertToEntityAttribute() {
            // given
            memberRepository.save(MemberFixture.KAI.create(ANYANG_CODE));

            // when
            final String sql = "SELECT m.collegeInfo.collegeType FROM Member m WHERE m.collegeInfo.collegeType = :collegeType";
            CollegeType findCollegeType = entityManager.createQuery(sql, CollegeType.class)
                .setParameter("collegeType", CollegeType.ENGINEERING)
                .getSingleResult();

            // then
            assertThat(findCollegeType).isEqualTo(CollegeType.ENGINEERING);
        }
    }

    @Nested
    class DrinkWithGameConverterTest {

        @DisplayName("데이터 베이스에 요청을 전송할 때, drink_with_game field에 key 값이 바인딩된다.")
        @Test
        void convertToDatabaseColumn() {
            // given
            Member kai = memberRepository.save(MemberFixture.KAI.create(ANYANG_CODE));

            Team manTeam = teamRepository.save(
                TeamFixture.HONGDAE_TEAM_1.create_with_activity(kai, TeamMemberFixture.create_3_man(), AdditionalActivity.CAFE)
            );

            // when
            final String sql = "SELECT drink_with_game FROM team WHERE team_id = :teamId";
            Integer drinkWithGame = (Integer) entityManager.createNativeQuery(sql, Integer.class)
                .setParameter("teamId", manTeam.getTeamId())
                .getSingleResult();

            // then
            assertThat(drinkWithGame).isEqualTo(0);
        }

        @DisplayName("데이터 베이스에서 가져온 값이 바인딩 될 때 Entity에 DrinkWithGame Enum이 바인딩된다")
        @Test
        void convertToEntityAttribute() {
            // given
            Member kai = memberRepository.save(MemberFixture.KAI.create(ANYANG_CODE));

            teamRepository.save(
                TeamFixture.HONGDAE_TEAM_1.create_with_activity(kai, TeamMemberFixture.create_3_man(), AdditionalActivity.CAFE)
            );

            // when
            final String sql = "SELECT t.drinkWithGame FROM Team t WHERE t.drinkWithGame = :drinkWithGame";
            DrinkWithGame findDrinkWithGame = entityManager.createQuery(sql, DrinkWithGame.class)
                .setParameter("drinkWithGame", DrinkWithGame.ANY)
                .getSingleResult();

            // then
            assertThat(findDrinkWithGame).isEqualTo(DrinkWithGame.ANY);
        }
    }

    @Nested
    class RegionConverterTest {

        @DisplayName("데이터 베이스에 요청을 전송할 때, region field에 key 값이 바인딩된다.")
        @Test
        void convertToDatabaseColumn() {
            // given
            Member kai = memberRepository.save(MemberFixture.KAI.create(ANYANG_CODE));

            Team manTeam = teamRepository.save(
                TeamFixture.HONGDAE_TEAM_1.create(kai, TeamMemberFixture.create_3_man())
            );

            // when
            final String sql = "SELECT region FROM team WHERE team_id = :teamId";
            Integer region = (Integer) entityManager.createNativeQuery(sql, Integer.class)
                .setParameter("teamId", manTeam.getTeamId())
                .getSingleResult();

            // then
            assertThat(region).isEqualTo(1);
        }

        @DisplayName("데이터 베이스에서 가져온 값이 바인딩 될 때 Entity에 Region Enum이 바인딩된다")
        @Test
        void convertToEntityAttribute() {
            // given
            Member kai = memberRepository.save(MemberFixture.KAI.create(ANYANG_CODE));

            teamRepository.save(
                TeamFixture.HONGDAE_TEAM_1.create(kai, TeamMemberFixture.create_3_man())
            );

            // when
            final String sql = "SELECT t.region FROM Team t WHERE t.region = :region";
            Region findRegion = entityManager.createQuery(sql, Region.class)
                .setParameter("region", Region.HONGDAE)
                .getSingleResult();

            // then
            assertThat(findRegion).isEqualTo(Region.HONGDAE);
        }
    }

    @Nested
    class GenderConverterTest {

        @DisplayName("데이터 베이스에 요청을 전송할 때, gender field 에 바인딩 되는 값은 Gender의 key 값으로 변환되어 전송된다.")
        @Test
        void convertToDatabaseColumn() {
            // given
            Member member = memberRepository.save(MemberFixture.KAI.create(ANYANG_CODE));

            // when
            final String sql = "SELECT gender FROM member WHERE member_id = :memberId";
            String genderColumn = String.valueOf(entityManager.createNativeQuery(sql)
                .setParameter("memberId", member.getMemberId())
                .getSingleResult());

            // then
            assertThat(genderColumn).isEqualTo("m");
        }

        @DisplayName("데이터 베이스에서 가져온 값이 바인딩 될 때 Entity에 Gender Enum이 바인딩된다")
        @Test
        void convertToEntityAttribute() {
            // given
            memberRepository.save(MemberFixture.KAI.create(ANYANG_CODE));

            // when
            final String sql = "SELECT m FROM Member m WHERE gender = :gender";
            Member findMember = entityManager.createQuery(sql, Member.class)
                .setParameter("gender", Gender.MAN)
                .getSingleResult();

            // then
            assertThat(findMember.getGender()).isEqualTo(Gender.MAN);
        }
    }

    @Nested
    class MbtiConverterTest {

        @DisplayName("데이터 베이스에 요청을 전송할 때, mbti field 에 바인딩 되는 값은 mbti의 key 값으로 변환되어 전송된다.")
        @Test
        void convertToDatabaseColumn() {
            // given
            Member member = memberRepository.save(MemberFixture.KAI.create(ANYANG_CODE));

            // when
            final String sql = "SELECT mbti FROM member WHERE member_id = :memberId";
            Integer mbti = (Integer) entityManager.createNativeQuery(sql, Integer.class)
                .setParameter("memberId", member.getMemberId())
                .getSingleResult();

            // then
            assertThat(mbti).isEqualTo(15);
        }

        @DisplayName("데이터 베이스에서 가져온 값이 바인딩 될 때 Entity에 Mbti Enum이 바인딩된다")
        @Test
        void convertToEntityAttribute() {
            // given
            memberRepository.save(MemberFixture.KAI.create(ANYANG_CODE));

            // when
            final String sql = "SELECT m.mbti FROM Member m WHERE mbti = :mbti";
            Mbti findMbti = entityManager.createQuery(sql, Mbti.class)
                .setParameter("mbti", Mbti.INFJ)
                .getSingleResult();

            // then
            assertThat(findMbti).isEqualTo(Mbti.INFJ);
        }
    }
}
