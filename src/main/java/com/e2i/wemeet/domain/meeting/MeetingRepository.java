package com.e2i.wemeet.domain.meeting;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingRepository extends JpaRepository<Meeting, Long>, MeetingReadRepository {

    @Query("select mt from Meeting mt where mt.team.teamId = :teamId")
    Optional<Meeting> findByTeamId(@Param("teamId") Long teamId);

    @Modifying(clearAutomatically = true)
    @Query("update Meeting mt set mt.isOver = true where mt.meetingId in :meetingIdList")
    void updateMeetingToOver(@Param("meetingIdList") Collection<Long> meetingIdList);

    @Query("select mt from Meeting mt where mt.meetingId in :meetingIdList")
    List<Meeting> findByMeetingIdIn(@Param("meetingIdList") Collection<Long> meetingIdList);

}
