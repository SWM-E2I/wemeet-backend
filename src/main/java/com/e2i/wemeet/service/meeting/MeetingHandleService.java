package com.e2i.wemeet.service.meeting;

import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.dto.request.meeting.SendMeetingRequestDto;
import com.e2i.wemeet.dto.request.meeting.SendMeetingWithMessageRequestDto;
import java.time.LocalDateTime;

public interface MeetingHandleService {

    /*
     * 미팅 신청
     * */
    void sendRequest(SendMeetingRequestDto requestDto, Long memberId);

    /*
     * 쪽지와 함께 미팅 신청
     * */
    void sendRequestWithMessage(SendMeetingWithMessageRequestDto requestDto, Long memberId);

    /*
     * 미팅 신청 수락
     * */
    Long acceptRequest(String openChatLink, Long memberId, Long meetingRequestId, LocalDateTime acceptDateTime);

    /*
     * 미팅 신청 거절
     * */
    AcceptStatus rejectRequest(Long memberId, Long meetingRequestId, LocalDateTime rejectDateTime);

}
