package com.e2i.wemeet.domain.meeting;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MeetingRepository extends JpaRepository<Meeting, Long>, MeetingReadRepository {

    @Query("select mt from Meeting mt where mt.team.teamId = :teamId")
    Optional<Meeting> findByTeamId(Long teamId);

    @Modifying(clearAutomatically = true)
    @Query("update Meeting mt set mt.isOver = true where mt.meetingId in :meetingIdList")
    void updateMeetingToOver(Collection<Long> meetingIdList);

    @Query("select mt from Meeting mt where mt.meetingId in :meetingIdList")
    List<Meeting> findByMeetingIdIn(Collection<Long> meetingIdList);

}
