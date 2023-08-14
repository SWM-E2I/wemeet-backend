package com.e2i.wemeet.service.meeting;

import com.e2i.wemeet.domain.meeting.MeetingRepository;
import com.e2i.wemeet.domain.meeting.MeetingRequestRepository;
import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.dto.request.meeting.MeetingHandleRequestDto;
import com.e2i.wemeet.dto.request.meeting.SendMeetingRequestDto;
import com.e2i.wemeet.dto.request.meeting.SendMeetingWithMessageRequestDto;
import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingRequestRepository meetingRequestRepository;

    @Override
    public void sendRequest(final SendMeetingRequestDto requestDto, final Long memberId) {

    }

    @Override
    public void sendRequestWithMessage(final SendMeetingWithMessageRequestDto requestDto, final Long memberId) {

    }

    @Override
    public AcceptStatus handleRequest(final MeetingHandleRequestDto requestDto, final Long memberId, final Long meetingRequestId) {
        return null;
    }

    @Override
    public List<SentMeetingResponseDto> getSentRequestList(final Long MemberId) {
        return null;
    }

    @Override
    public List<ReceivedMeetingResponseDto> getReceiveRequestList(final Long MemberId) {
        return null;
    }
}
