package com.e2i.wemeet.service.meeting;

import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.ACCEPT;
import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.EXPIRED;
import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.PENDING;
import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.REJECT;
import static com.e2i.wemeet.exception.ErrorCode.ACCEPT_STATUS_IS_NOT_PENDING;
import static com.e2i.wemeet.exception.ErrorCode.EXPIRED_MEETING_REQUEST;
import static com.e2i.wemeet.exception.ErrorCode.UNAUTHORIZED;
import static com.e2i.wemeet.util.validator.CustomExpirationValidator.isExpiredOfDays;

import com.e2i.wemeet.domain.meeting.Meeting;
import com.e2i.wemeet.domain.meeting.MeetingRepository;
import com.e2i.wemeet.domain.meeting.MeetingRequest;
import com.e2i.wemeet.domain.meeting.MeetingRequestRepository;
import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.dto.request.meeting.SendMeetingRequestDto;
import com.e2i.wemeet.dto.request.meeting.SendMeetingWithMessageRequestDto;
import com.e2i.wemeet.exception.badrequest.BadRequestException;
import com.e2i.wemeet.exception.badrequest.ExpiredException;
import com.e2i.wemeet.exception.notfound.MeetingRequestNotFound;
import com.e2i.wemeet.security.manager.CreditAuthorize;
import com.e2i.wemeet.security.manager.IsManager;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MeetingHandleServiceImpl implements MeetingHandleService {

    public static final int MEETING_REQUEST_EXPIRE_DAY = 3;

    public static final int CREDIT_AMOUNT_ACCEPT = 5;
    public static final int CREDIT_AMOUNT_REQUEST = 10;
    public static final int CREDIT_AMOUNT_REQUEST_WITH_MESSAGE = 12;

    private final MeetingRepository meetingRepository;
    private final MemberRepository memberRepository;
    private final MeetingRequestRepository meetingRequestRepository;

    @Transactional
    @CreditAuthorize(value = CREDIT_AMOUNT_REQUEST, role = Role.MANAGER)
    @Override
    public void sendRequest(final SendMeetingRequestDto requestDto, final Long memberLeaderId) {
        Team team = meetingRepository.findTeamReferenceByLeaderId(memberLeaderId);
        Team partnerTeam = meetingRepository.findTeamReferenceById(requestDto.partnerTeamId());

        MeetingRequest meetingRequest = MeetingRequest.builder()
            .team(team)
            .partnerTeam(partnerTeam)
            .build();

        meetingRequestRepository.save(meetingRequest);

        memberRepository.findById(memberLeaderId)
            .ifPresent(member -> member.useCredit(CREDIT_AMOUNT_REQUEST));
    }

    @Transactional
    @CreditAuthorize(value = 12, role = Role.MANAGER)
    @Override
    public void sendRequestWithMessage(final SendMeetingWithMessageRequestDto requestDto, final Long memberLeaderId) {
        Team team = meetingRepository.findTeamReferenceByLeaderId(memberLeaderId);
        Team partnerTeam = meetingRepository.findTeamReferenceById(requestDto.partnerTeamId());

        MeetingRequest meetingRequest = MeetingRequest.builder()
            .team(team)
            .partnerTeam(partnerTeam)
            .message(requestDto.getMessageWithValid())
            .build();

        meetingRequestRepository.save(meetingRequest);

        memberRepository.findById(memberLeaderId)
            .ifPresent(member -> member.useCredit(CREDIT_AMOUNT_REQUEST_WITH_MESSAGE));
    }

    @Transactional
    @CreditAuthorize(value = CREDIT_AMOUNT_ACCEPT, role = Role.MANAGER)
    @Override
    public Long acceptRequest(final String openChatLink, final Long memberLeaderId, final Long meetingRequestId, final LocalDateTime acceptDateTime) {
        MeetingRequest meetingRequest = meetingRequestRepository.findByIdFetchTeamAndPartnerTeam(meetingRequestId)
            .orElseThrow(MeetingRequestNotFound::new)
            .checkValid();

        validateAbleToHandlingMeetingRequest(acceptDateTime, memberLeaderId, meetingRequest);
        meetingRequest.changeStatus(ACCEPT);

        memberRepository.findById(memberLeaderId)
            .ifPresent(member -> member.useCredit(CREDIT_AMOUNT_ACCEPT));

        return saveMeeting(openChatLink, meetingRequest).getMeetingId();
    }

    @Transactional
    @IsManager
    @Override
    public AcceptStatus rejectRequest(final Long memberLeaderId, final Long meetingRequestId, final LocalDateTime rejectDateTime) {
        MeetingRequest meetingRequest = meetingRequestRepository.findByIdFetchPartnerTeam(meetingRequestId)
            .orElseThrow(MeetingRequestNotFound::new)
            .checkValid();

        validateAbleToHandlingMeetingRequest(rejectDateTime, memberLeaderId, meetingRequest);
        meetingRequest.changeStatus(REJECT);

        return REJECT;
    }

    private Meeting saveMeeting(final String openChatLink, final MeetingRequest meetingRequest) {
        Meeting meeting = Meeting.builder()
            .team(meetingRequest.getTeam())
            .partnerTeam(meetingRequest.getPartnerTeam())
            .chatLink(openChatLink)
            .build();
        return meetingRepository.save(meeting);
    }

    // 미팅 상태를 변경할 수 있는지 검증
    private void validateAbleToHandlingMeetingRequest(final LocalDateTime now, final Long memberLeaderId, MeetingRequest meetingRequest) {
        final LocalDateTime meetingRequestDateTime = meetingRequest.getCreatedAt();

        validateHasAuthorizationToChangeStatus(memberLeaderId, meetingRequest);
        validateIsPending(meetingRequest.getAcceptStatus());
        validateIsExpired(now, meetingRequest, meetingRequestDateTime);
    }

    // 미팅 요청이 PENDING 상태인지 검증
    private static void validateIsPending(final AcceptStatus acceptStatus) {
        if (acceptStatus != PENDING) {
            throw new BadRequestException(ACCEPT_STATUS_IS_NOT_PENDING);
        }
    }

    // 미팅 요청이 만료되었는지 검증
    private void validateIsExpired(LocalDateTime now, MeetingRequest meetingRequest, LocalDateTime meetingRequestDateTime) {
        if (isExpiredOfDays(meetingRequestDateTime, now, MEETING_REQUEST_EXPIRE_DAY)) {
            meetingRequest.changeStatus(EXPIRED);
            throw new ExpiredException(EXPIRED_MEETING_REQUEST);
        }
    }

    // 미팅 요청을 받은 사용자인지 검증
    private void validateHasAuthorizationToChangeStatus(Long memberLeaderId, MeetingRequest meetingRequest) {
        Long leaderTeamId = meetingRepository.findTeamIdByLeaderId(memberLeaderId);
        Long partnerTeamId = meetingRequest.getPartnerTeam().getTeamId();

        if (!Objects.equals(leaderTeamId, partnerTeamId)) {
            throw new BadRequestException(UNAUTHORIZED);
        }
    }

}
