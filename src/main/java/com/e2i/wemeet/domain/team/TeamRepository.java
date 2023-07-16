package com.e2i.wemeet.domain.team;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamRepository extends JpaRepository<Team, Long> {


    @Query("select distinct t from Team t join fetch t.members where t.teamId = :id")
    Optional<Team> findFetchMembersById(@Param("id") Long teamId);
}
