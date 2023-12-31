package com.e2i.wemeet.domain.meeting;

import java.time.LocalDateTime;
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

    /*
     * 팀장의 핸드폰 번호 조회
     * */
    @Query("""
        select m.phoneNumber
        from Member m
        join Team t on t.teamLeader.memberId = m.memberId and t.deletedAt is null
        where t.teamId = :teamId
        """)
    String findLeaderPhoneNumberById(@Param("teamId") final Long teamId);

    /*
     * 미팅이 이미 존재하는지 조회
     * */
    @Query("""
        select mt.createdAt from Meeting mt
        join mt.partnerTeam pt
        join mt.team t
        join MeetingRequest mr on mr.partnerTeam = pt and mr.team = t
        where mr.meetingRequestId = :meetingRequestId
        """)
    List<LocalDateTime> findCreatedAtByMeetingRequestId(
        @Param("meetingRequestId") final Long meetingRequestId);

    /*
     * 팀장의 pushToken 조회
     * */
    @Query("""
        select p.token
        from PushToken p
        join Team t on t.teamLeader.memberId = p.member.memberId and t.deletedAt is null
        where t.teamId = :teamId
        """)
    Optional<String> findLeaderPushTokenById(@Param("teamId") final Long teamId);

}
