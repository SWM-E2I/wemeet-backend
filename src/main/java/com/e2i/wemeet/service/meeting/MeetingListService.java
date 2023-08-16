package com.e2i.wemeet.service.meeting;

import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
import com.e2i.wemeet.dto.response.team.AcceptMeetingResponseDto;
import java.util.List;

public interface MeetingListService {

    /*
     * 성사된 미팅 리스트 조회
     * */
    List<AcceptMeetingResponseDto> getAcceptedMeetingList(Long memberId);

    /*
     * 보낸 미팅 신청 리스트 조회
     * */
    List<SentMeetingResponseDto> getSentRequestList(Long memberId);

    /*
     * 받은 미팅 신청 리스트 조회
     * */
    List<ReceivedMeetingResponseDto> getReceiveRequestList(Long memberId);

}
