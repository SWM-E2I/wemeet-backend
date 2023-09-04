package com.e2i.wemeet.domain.meeting;

import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingRequestRepository extends JpaRepository<MeetingRequest, Long> {

    @Query("select mr from MeetingRequest mr join fetch mr.team join fetch mr.partnerTeam where mr.meetingRequestId = :meetingRequestId")
    Optional<MeetingRequest> findByIdFetchTeamAndPartnerTeam(@Param("meetingRequestId") Long meetingRequestId);

    @Query("select mr from MeetingRequest mr join fetch mr.partnerTeam where mr.meetingRequestId = :meetingRequestId")
    Optional<MeetingRequest> findByIdFetchPartnerTeam(@Param("meetingRequestId") Long meetingRequestId);

    @Modifying(clearAutomatically = true)
    @Query("update MeetingRequest mr set mr.acceptStatus = 3 where mr.meetingRequestId in :meetingRequestIdList")
    void updateRequestToExpired(@Param("meetingRequestIdList") Collection<Long> meetingRequestIdList);

}
