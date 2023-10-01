package com.e2i.wemeet.domain.team;

import com.e2i.wemeet.domain.team.suggestion.SuggestionRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamCustomRepository,
    SuggestionRepository {

    @Query("select t from Team t where t.teamLeader.memberId = :memberId")
    Optional<Team> findByMemberId(@Param("memberId") Long memberId);

}
