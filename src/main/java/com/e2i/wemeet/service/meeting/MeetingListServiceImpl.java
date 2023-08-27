package com.e2i.wemeet.service.meeting;

import com.e2i.wemeet.domain.meeting.MeetingRepository;
import com.e2i.wemeet.dto.response.meeting.AcceptedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
import com.e2i.wemeet.security.manager.IsManager;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MeetingListServiceImpl implements MeetingListService {

    private static final int MEETING_EXPIRE_DAY = 7;
    private static final int REQUEST_EXPIRE_DAY = 3;

    private final MeetingRepository meetingRepository;

    /*
     * 미팅 목록 *
     - 미팅이 성사된지 7일이 되었는지 확인 -> 성사된 미팅은 EXPIRED 상태로 변경
     * -> EXPIRED 된 미팅도 보내줌
     */
    @IsManager
    @Override
    public List<AcceptedMeetingResponseDto> getAcceptedMeetingList(final Long memberId, final LocalDateTime findDateTime) {

        return null;
    }

    /*
     * 보낸 미팅 신청 요청 목록 *
     - 보낸 신청 요청이 3일이 지났는지 확인 -> 3일이 지난 요청은 EXPIRED 상태로 변경
     * 응답 제외 목록
     * 1. EXPIRED 된 요청
     * 2. REJECT 된 요청
     * 3. ACCEPT 된 요청
     */
    @IsManager
    @Override
    public List<SentMeetingResponseDto> getSentRequestList(final Long MemberId, final LocalDateTime findDateTime) {
        return null;
    }

    /*
     * 받은 미팅 신청 요청 목록 *
     - 받은 요청이 3일이 지났는지 확인 -> 3일이 지난 요청은 EXPIRED 상태로 변경
     * 응답 제외 목록
     * 1. EXPIRED 된 요청
     * 2. REJECT 된 요청
     * 3. ACCEPT 된 요청
     */
    @IsManager
    @Override
    public List<ReceivedMeetingResponseDto> getReceiveRequestList(final Long MemberId, final LocalDateTime findDateTime) {
        return null;
    }

}
