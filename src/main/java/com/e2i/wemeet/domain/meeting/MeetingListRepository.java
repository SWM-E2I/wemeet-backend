package com.e2i.wemeet.domain.meeting;

import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
import java.util.List;

public interface MeetingListRepository {

    /*
     * 보낸 미팅 신청 리스트 조회
     * */
    List<SentMeetingResponseDto> getSentRequestList(Long memberId);

    /*
     * 받은 미팅 신청 리스트 조회
     * */
    List<ReceivedMeetingResponseDto> getReceiveRequestList(Long memberId);

}
