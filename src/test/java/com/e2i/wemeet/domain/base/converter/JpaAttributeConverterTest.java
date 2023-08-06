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

    @DisplayName("MEETING_REQUEST 테이블의 accept_status 필드 값 변환기를 검증한다.")
    @Nested
    class AcceptStatusConverterTest {

        @DisplayName("데이터베이스에 요청을 전송할 때, '수락 상태'에 숫자 코드 값이 삽입된다.")
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

    @DisplayName("TEAM 테이블의 additional_activity 필드 값 변환기를 검증한다.")
    @Nested
    class AdditionalActivityConverterTest {

        @DisplayName("데이터베이스에 요청을 전송할 때, '추가 활동'에 숫자 코드 값이 삽입된다.")
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

        @DisplayName("데이터 베이스에서 가져온 숫자 코드 값을 객체로 변환한다")
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

    @DisplayName("college_type 필드 값 변환기를 검증한다.")
    @Nested
    class CollegeTypeConverterTest {

        @DisplayName("데이터베이스에 요청을 전송할 때, '학과'에 숫자 코드 값이 삽입된다.")
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

        @DisplayName("데이터 베이스에서 가져온 숫자 코드 값을 객체로 변환한다")
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

    @DisplayName("TEAM 테이블의 drink_with_game 필드 값 변환기를 검증한다.")
    @Nested
    class DrinkWithGameConverterTest {

        @DisplayName("데이터베이스에 요청을 전송할 때, '음주 선호 정보'에 숫자 코드 값이 삽입된다.")
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

        @DisplayName("데이터 베이스에서 가져온 숫자 코드 값을 객체로 변환한다")
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

    @DisplayName("TEAM 테이블의 region 필드 값 변환기를 검증한다.")
    @Nested
    class RegionConverterTest {

        @DisplayName("데이터베이스에 요청을 전송할 때, '미팅 지역'에 숫자 코드 값이 삽입된다.")
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

        @DisplayName("데이터 베이스에서 가져온 숫자 코드 값을 객체로 변환한다")
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

    @DisplayName("MEMBER 테이블의 gender 필드 값 변환기를 검증한다.")
    @Nested
    class GenderConverterTest {

        @DisplayName("데이터베이스에 요청을 전송할 때, '성별'에 한자리 문자 코드 값이 삽입된다.")
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

        @DisplayName("데이터 베이스에서 가져온 문자 코드 값을 객체로 변환한다")
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

    @DisplayName("MEMBER 테이블의 mbti 필드 값 변환기를 검증한다.")
    @Nested
    class MbtiConverterTest {

        @DisplayName("데이터베이스에 요청을 전송할 때, 'MBTI'에 숫자 코드 값이 삽입된다.")
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

        @DisplayName("데이터 베이스에서 가져온 숫자 코드 값을 객체로 변환한다")
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
