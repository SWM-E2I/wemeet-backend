package com.e2i.wemeet.domain.meeting;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MeetingRepository extends JpaRepository<Meeting, Long>, MeetingReadRepository {

    @Query("select mt from Meeting mt where mt.team.teamId = :teamId")
    Optional<Meeting> findByTeamId(Long teamId);

}
