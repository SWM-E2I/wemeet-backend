package com.e2i.wemeet.domain.meeting;

import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.domain.team.Team;
import java.util.Collection;
import java.util.List;
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

    // 미팅 신청 이력 조회 ('대기중' 인 요청만)
    @Query("""
        select mr.meetingRequestId from MeetingRequest mr
        where mr.team.teamId = :teamId and mr.partnerTeam.teamId = :partnerTeamId 
        and mr.acceptStatus = 0
        """)
    Optional<Long> findIdByTeamIdAndPartnerTeamId(@Param("teamId") Long teamId, @Param("partnerTeamId") Long partnerTeamId);

    // 팀의 이전 미팅 신청 이력 조회 (신청 상태 조건)
    @Query("""
        select mr from MeetingRequest mr
        where mr.team = :team and mr.acceptStatus = :acceptStatus
        """)
    List<MeetingRequest> findAllByTeamAndAcceptStatus(@Param("team") Team team, @Param("acceptStatus") AcceptStatus acceptStatus);

}
