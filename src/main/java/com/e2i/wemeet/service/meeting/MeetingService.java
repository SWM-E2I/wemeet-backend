package com.e2i.wemeet.service.meeting;

import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.dto.request.meeting.MeetingHandleRequestDto;
import com.e2i.wemeet.dto.request.meeting.SendMeetingRequestDto;
import com.e2i.wemeet.dto.request.meeting.SendMeetingWithMessageRequestDto;
import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
import java.util.List;

public interface MeetingService {

    /*
     * 미팅 신청
     * */
    void sendRequest(SendMeetingRequestDto requestDto, Long memberId);

    /*
     * 쪽지와 함께 미팅 신청
     * */
    void sendRequestWithMessage(SendMeetingWithMessageRequestDto requestDto, Long memberId);

    /*
     * 미팅 신청 수락/거절
     * */
    AcceptStatus handleRequest(MeetingHandleRequestDto requestDto, Long memberId, Long meetingRequestId);

    /*
     * 보낸 미팅 신청 리스트 조회
     * */
    List<SentMeetingResponseDto> getSentRequestList(Long memberId);

    /*
     * 받은 미팅 신청 리스트 조회
     * */
    List<ReceivedMeetingResponseDto> getReceiveRequestList(Long memberId);

}
