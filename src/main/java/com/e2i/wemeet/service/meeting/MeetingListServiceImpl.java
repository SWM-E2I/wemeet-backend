package com.e2i.wemeet.service.meeting;

import com.e2i.wemeet.domain.meeting.MeetingRepository;
import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
import com.e2i.wemeet.dto.response.team.AcceptMeetingResponseDto;
import com.e2i.wemeet.security.manager.IsManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MeetingListServiceImpl implements MeetingListService {

    public static final int MEETING_EXPIRE_DAY = 7;

    private final MeetingRepository meetingRepository;

    /*
     * 미팅 목록 *
     - 미팅이 성사된지 7일이 되었는지 확인 -> 성사된 미팅은 EXPIRED 상태로 변경
     * -> EXPIRED 된 미팅도 보내줌
     */
    @Override
    public List<AcceptMeetingResponseDto> getAcceptedMeetingList(Long memberId) {
        return null;
    }

    /*
     * 보낸 미팅 신청 목록 *
     - 보낸 미팅 신청이 3일이 되었는지 확인 -> 성사된 미팅은 EXPIRED 상태로 변경
     * * -> EXPIRED 된 요청은 목록에서 제외
     */
    @IsManager
    @Override
    public List<SentMeetingResponseDto> getSentRequestList(final Long MemberId) {
        return null;
    }

    /*
     * 보낸 미팅 신청 목록 *
     - 보낸 미팅 신청이 3일이 되었는지 확인 -> 성사된 미팅은 EXPIRED 상태로 변경
     * * * -> EXPIRED 된 요청은 목록에서 제외
     */
    @IsManager
    @Override
    public List<ReceivedMeetingResponseDto> getReceiveRequestList(final Long MemberId) {
        return null;
    }

}
