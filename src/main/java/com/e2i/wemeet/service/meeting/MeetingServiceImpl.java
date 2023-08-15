package com.e2i.wemeet.service.meeting;

import com.e2i.wemeet.domain.meeting.MeetingRepository;
import com.e2i.wemeet.domain.meeting.MeetingRequest;
import com.e2i.wemeet.domain.meeting.MeetingRequestRepository;
import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.dto.request.meeting.MeetingHandleRequestDto;
import com.e2i.wemeet.dto.request.meeting.SendMeetingRequestDto;
import com.e2i.wemeet.dto.request.meeting.SendMeetingWithMessageRequestDto;
import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
import com.e2i.wemeet.exception.notfound.TeamNotFoundException;
import com.e2i.wemeet.security.manager.CreditAuthorize;
import com.e2i.wemeet.security.manager.IsManager;
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
    private final TeamRepository teamRepository;

    @Transactional
    @CreditAuthorize(value = 10)
    @Override
    public void sendRequest(final SendMeetingRequestDto requestDto, final Long memberId) {
        Team team = teamRepository.findByMemberId(memberId)
            .orElseThrow(TeamNotFoundException::new)
            .checkTeamValid();

        Team partnerTeam = teamRepository.findById(requestDto.partnerTeamId())
            .orElseThrow(TeamNotFoundException::new)
            .checkTeamValid();

        MeetingRequest meetingRequest = MeetingRequest.builder()
            .team(team)
            .partnerTeam(partnerTeam)
            .build();

        meetingRequestRepository.save(meetingRequest);
    }

    @Transactional
    @CreditAuthorize(value = 12)
    @Override
    public void sendRequestWithMessage(final SendMeetingWithMessageRequestDto requestDto, final Long memberId) {

    }

    @Transactional
    @IsManager
    @Override
    public AcceptStatus handleRequest(final MeetingHandleRequestDto requestDto, final Long memberId, final Long meetingRequestId) {
        return null;
    }

    @IsManager
    @Override
    public List<SentMeetingResponseDto> getSentRequestList(final Long MemberId) {
        return null;
    }

    @IsManager
    @Override
    public List<ReceivedMeetingResponseDto> getReceiveRequestList(final Long MemberId) {
        return null;
    }
}
