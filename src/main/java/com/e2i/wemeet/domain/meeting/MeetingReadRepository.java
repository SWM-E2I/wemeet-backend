package com.e2i.wemeet.domain.meeting;

import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.dto.response.meeting.AcceptedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
import java.util.List;

public interface MeetingReadRepository {

    /*
     * LeaderId로 Team 프록시 객체 조회
     * */
    Team findTeamReferenceByLeaderId(final Long memberLeaderId);

    /*
     * LeaderId로 TeamId 조회
     * */
    Long findTeamIdByLeaderId(final Long memberLeaderId);

    /*
     * LeaderId로 Team 프록시 객체 조회
     * */
    Team findTeamReferenceById(final Long teamId);

    /*
     * 성사된 미팅 리스트 조회
     * */
    List<AcceptedMeetingResponseDto> findAcceptedMeetingList(Long memberId);

    /*
     * 보낸 미팅 신청 리스트 조회
     * */
    List<SentMeetingResponseDto> findSentRequestList(Long memberId);

    /*
     * 받은 미팅 신청 리스트 조회
     * */
    List<ReceivedMeetingResponseDto> findReceiveRequestList(Long memberId);

}
