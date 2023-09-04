package com.e2i.wemeet.domain.team;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamCustomRepository {

    @Query("select t from Team t where t.teamLeader.memberId = :memberId")
    Optional<Team> findByMemberId(Long memberId);

}
