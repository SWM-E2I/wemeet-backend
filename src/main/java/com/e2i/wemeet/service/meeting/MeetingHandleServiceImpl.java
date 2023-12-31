package com.e2i.wemeet.service.meeting;

import static com.e2i.wemeet.domain.cost.Spent.MEETING_ACCEPT;
import static com.e2i.wemeet.domain.cost.Spent.MEETING_REQUEST;
import static com.e2i.wemeet.domain.cost.Spent.MEETING_REQUEST_WITH_MESSAGE;
import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.ACCEPT;
import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.EXPIRED;
import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.PENDING;
import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.REJECT;
import static com.e2i.wemeet.exception.ErrorCode.ACCEPT_STATUS_IS_NOT_PENDING;
import static com.e2i.wemeet.exception.ErrorCode.EXPIRED_MEETING_REQUEST;
import static com.e2i.wemeet.exception.ErrorCode.UNAUTHORIZED;
import static com.e2i.wemeet.service.meeting.MeetingListServiceImpl.MEETING_EXPIRE_DAY;
import static com.e2i.wemeet.service.sns.SnsMessageFormat.getMeetingAcceptMessage;
import static com.e2i.wemeet.service.sns.SnsMessageFormat.getMeetingRequestMessage;
import static com.e2i.wemeet.util.validator.CustomExpirationValidator.isExpiredOfDays;

import com.e2i.wemeet.domain.cost.Spent;
import com.e2i.wemeet.domain.meeting.Meeting;
import com.e2i.wemeet.domain.meeting.MeetingRepository;
import com.e2i.wemeet.domain.meeting.MeetingRequest;
import com.e2i.wemeet.domain.meeting.MeetingRequestRepository;
import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.dto.request.meeting.SendMeetingRequestDto;
import com.e2i.wemeet.dto.request.meeting.SendMeetingWithMessageRequestDto;
import com.e2i.wemeet.exception.badrequest.BadRequestException;
import com.e2i.wemeet.exception.badrequest.DuplicateMeetingRequestException;
import com.e2i.wemeet.exception.badrequest.ExpiredException;
import com.e2i.wemeet.exception.badrequest.MeetingAlreadyExistException;
import com.e2i.wemeet.exception.notfound.MeetingRequestNotFound;
import com.e2i.wemeet.security.manager.CostAuthorize;
import com.e2i.wemeet.security.manager.IsManager;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MeetingHandleServiceImpl implements MeetingHandleService {

    public static final int MEETING_REQUEST_EXPIRE_DAY = 3;

    private final MeetingRepository meetingRepository;
    private final MeetingRequestRepository meetingRequestRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @CostAuthorize(type = MEETING_REQUEST, role = Role.MANAGER)
    @Override
    public Long sendRequest(final SendMeetingRequestDto requestDto, final Long memberLeaderId,
        final LocalDateTime meetingRequestTime) {
        Team team = meetingRepository.findTeamReferenceByLeaderId(memberLeaderId);
        Team partnerTeam = meetingRepository.findTeamReferenceById(requestDto.partnerTeamId());
        checkDuplicateMeetingRequest(team, partnerTeam, meetingRequestTime);

        MeetingRequest meetingRequest = MeetingRequest.builder()
            .team(team)
            .partnerTeam(partnerTeam)
            .build();
        MeetingRequest request = meetingRequestRepository.save(meetingRequest);

        // 이벤트 발행
        publishMeetingEvent(getMeetingRequestMessage(), memberLeaderId, partnerTeam,
            MEETING_REQUEST);
        return request.getMeetingRequestId();
    }

    @Transactional
    @CostAuthorize(type = MEETING_REQUEST_WITH_MESSAGE, role = Role.MANAGER)
    @Override
    public Long sendRequestWithMessage(final SendMeetingWithMessageRequestDto requestDto,
        final Long memberLeaderId,
        final LocalDateTime meetingRequestTime) {
        Team team = meetingRepository.findTeamReferenceByLeaderId(memberLeaderId);
        Team partnerTeam = meetingRepository.findTeamReferenceById(requestDto.partnerTeamId());
        checkDuplicateMeetingRequest(team, partnerTeam, meetingRequestTime);

        MeetingRequest meetingRequest = MeetingRequest.builder()
            .team(team)
            .partnerTeam(partnerTeam)
            .message(requestDto.message())
            .build();
        MeetingRequest request = meetingRequestRepository.save(meetingRequest);

        // 이벤트 발행
        publishMeetingEvent(getMeetingRequestMessage(), memberLeaderId, partnerTeam,
            MEETING_REQUEST_WITH_MESSAGE);
        return request.getMeetingRequestId();
    }

    @Transactional
    @CostAuthorize(type = MEETING_ACCEPT, role = Role.MANAGER)
    @Override
    public Long acceptRequest(final Long memberLeaderId, final Long meetingRequestId,
        final LocalDateTime acceptDateTime) {
        MeetingRequest meetingRequest = meetingRequestRepository.findByIdFetchTeamAndPartnerTeam(
                meetingRequestId)
            .orElseThrow(MeetingRequestNotFound::new)
            .checkValid();
        checkDuplicateMeetingAccept(meetingRequestId, acceptDateTime);

        validateAbleToHandlingMeetingRequest(acceptDateTime, memberLeaderId, meetingRequest);
        meetingRequest.changeStatus(ACCEPT);

        // 미팅 성사 이벤트 발행
        Team myTeam = meetingRequest.getTeam();
        String leaderNickname = meetingRequest.getPartnerTeam().getTeamLeader().getNickname();
        publishMeetingEvent(getMeetingAcceptMessage(leaderNickname), memberLeaderId, myTeam,
            MEETING_ACCEPT);

        return saveMeeting(meetingRequest).getMeetingId();
    }

    @Transactional
    @IsManager
    @Override
    public AcceptStatus rejectRequest(final Long memberLeaderId, final Long meetingRequestId,
        final LocalDateTime rejectDateTime) {
        MeetingRequest meetingRequest = meetingRequestRepository.findByIdFetchPartnerTeam(
                meetingRequestId)
            .orElseThrow(MeetingRequestNotFound::new)
            .checkValid();

        validateAbleToHandlingMeetingRequest(rejectDateTime, memberLeaderId, meetingRequest);
        meetingRequest.changeStatus(REJECT);

        return REJECT;
    }

    private Meeting saveMeeting(final MeetingRequest meetingRequest) {
        Meeting meeting = Meeting.builder()
            .team(meetingRequest.getTeam())
            .partnerTeam(meetingRequest.getPartnerTeam())
            .build();
        return meetingRepository.save(meeting);
    }

    // 미팅 상태를 변경할 수 있는지 검증
    private void validateAbleToHandlingMeetingRequest(final LocalDateTime now,
        final Long memberLeaderId, MeetingRequest meetingRequest) {
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
    private void validateIsExpired(LocalDateTime now, MeetingRequest meetingRequest,
        LocalDateTime meetingRequestDateTime) {
        if (isExpiredOfDays(meetingRequestDateTime, now, MEETING_REQUEST_EXPIRE_DAY)) {
            meetingRequest.changeStatus(EXPIRED);
            throw new ExpiredException(EXPIRED_MEETING_REQUEST);
        }
    }

    // 미팅 요청을 받은 사용자인지 검증
    private void validateHasAuthorizationToChangeStatus(Long memberLeaderId,
        MeetingRequest meetingRequest) {
        Long leaderTeamId = meetingRepository.findTeamIdByLeaderId(memberLeaderId);
        Long partnerTeamId = meetingRequest.getPartnerTeam().getTeamId();

        if (!Objects.equals(leaderTeamId, partnerTeamId)) {
            throw new BadRequestException(UNAUTHORIZED);
        }
    }

    // 미팅 이벤트 발행
    private void publishMeetingEvent(final String message, final Long memberLeaderId,
        final Team targetTeam, final Spent spent) {
        String leaderPhoneNumber = meetingRepository.findLeaderPhoneNumberById(
            targetTeam.getTeamId());
        String leaderPushToken = meetingRepository.findLeaderPushTokenById(
            targetTeam.getTeamId()).orElse(null);
        
        eventPublisher.publishEvent(
            MeetingEvent.of(leaderPhoneNumber, leaderPushToken, message, spent, memberLeaderId)
        );
    }

    // 중복된 미팅 요청인지 검증
    private void checkDuplicateMeetingRequest(Team team, Team partnerTeam,
        LocalDateTime meetingRequestTime) {
        meetingRequestRepository.findIdByTeamIdAndPartnerTeamId(team.getTeamId(),
                partnerTeam.getTeamId())
            .filter(meetingRequestCreatedAt -> !isExpiredOfDays(meetingRequestCreatedAt,
                meetingRequestTime, MEETING_REQUEST_EXPIRE_DAY))
            .ifPresent(meetingRequestId -> {
                throw new DuplicateMeetingRequestException();
            });
    }

    // 미팅 수락 요청이 중복인지 검증
    private void checkDuplicateMeetingAccept(Long meetingRequestId, LocalDateTime acceptDateTime) {
        meetingRepository.findCreatedAtByMeetingRequestId(meetingRequestId).stream()
            .filter(createdAt -> !isExpiredOfDays(createdAt, acceptDateTime, MEETING_EXPIRE_DAY))
            .findAny()
            .ifPresent(createdAt -> {
                throw new MeetingAlreadyExistException();
            });
    }
}
