package com.e2i.wemeet.domain.meeting;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MeetingRequestRepository extends JpaRepository<MeetingRequest, Long> {

    @Query("select mr from MeetingRequest mr join fetch mr.team join fetch mr.partnerTeam where mr.meetingRequestId = :meetingRequestId")
    Optional<MeetingRequest> findByIdFetchTeamAndPartnerTeam(Long meetingRequestId);

    @Query("select mr from MeetingRequest mr join fetch mr.partnerTeam where mr.meetingRequestId = :meetingRequestId")
    Optional<MeetingRequest> findByIdFetchPartnerTeam(Long meetingRequestId);

}
