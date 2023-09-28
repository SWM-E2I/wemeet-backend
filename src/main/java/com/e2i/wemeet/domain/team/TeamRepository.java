package com.e2i.wemeet.domain.team;

import com.e2i.wemeet.domain.team.suggestion.SuggestionRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamCustomRepository,
    SuggestionRepository {

    @Query("select t from Team t where t.teamLeader.memberId = :memberId")
    Optional<Team> findByMemberId(Long memberId);

    /*
    ** 팀이 차단된 사용자의 팀인지 확인
    - memberId = 조회하는 사람의 ID
    - teamId = 차단된 팀인지 확인 하는 대상의 teamId
     */
    @Query("""
        SELECT COUNT(b.blockMember) > 0
        FROM Team t
        JOIN Block b on b.blockMember = t.teamLeader
        WHERE t.teamId = :teamId
        AND b.member.memberId = :memberId
        """)
    boolean isBlockedTeam(@Param("memberId") Long memberId, @Param("teamId") Long teamId);

}
