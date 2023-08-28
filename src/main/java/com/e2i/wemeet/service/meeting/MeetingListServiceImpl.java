package com.e2i.wemeet.service.meeting;

import com.e2i.wemeet.domain.meeting.MeetingRepository;
import com.e2i.wemeet.domain.meeting.MeetingRequestRepository;
import com.e2i.wemeet.dto.response.meeting.AcceptedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.MeetingRequestResponseDto;
import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
import com.e2i.wemeet.security.manager.IsManager;
import com.e2i.wemeet.util.validator.CustomExpirationValidator;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MeetingListServiceImpl implements MeetingListService {

    public static final int MEETING_EXPIRE_DAY = 7;
    public static final int REQUEST_EXPIRE_DAY = 3;

    private final MeetingRepository meetingRepository;
    private final MeetingRequestRepository meetingRequestRepository;

    /*
     * 미팅 목록 *
     - 매칭된 이후 7일이 지났는지 확인 & 상대 팀이 삭제 되었는지 확인
     * -> 두 조건 중 하나라도 만족하면 Meeting Entity 의 isOver == true 로 update
     * -> 결과 값에는 isExpired == true 보내줌
     */
    @IsManager
    @Override
    public List<AcceptedMeetingResponseDto> getAcceptedMeetingList(final Long memberId, final LocalDateTime findDateTime) {
        List<AcceptedMeetingResponseDto> meetingList = meetingRepository.findAcceptedMeetingList(memberId);
        updateMeetingsIfNecessary(meetingList, findDateTime);

        for (AcceptedMeetingResponseDto acceptedMeetingResponseDto : meetingList) {
            if (CustomExpirationValidator.isExpiredOfDays(acceptedMeetingResponseDto.getMeetingAcceptTime(), findDateTime, MEETING_EXPIRE_DAY)
                || acceptedMeetingResponseDto.isDeleted()) {
                acceptedMeetingResponseDto.expired();
            }
        }

        return meetingList;
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
    public List<SentMeetingResponseDto> getSentRequestList(final Long memberId, final LocalDateTime findDateTime) {
        List<SentMeetingResponseDto> sentRequestList = meetingRepository.findSentRequestList(memberId);
        updateMeetingRequestIfNecessary(sentRequestList, findDateTime);

        // 응답에 필요한 정보만 필터링
        return sentRequestList.stream()
            .filter(request -> request.isPending()
                && !request.isDeleted()
                && !CustomExpirationValidator.isExpiredOfDays(request.getRequestTime(), findDateTime, REQUEST_EXPIRE_DAY))
            .sorted(Comparator.comparing(MeetingRequestResponseDto::getRequestTime).reversed())
            .toList();
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
    public List<ReceivedMeetingResponseDto> getReceiveRequestList(final Long memberId, final LocalDateTime findDateTime) {
        List<ReceivedMeetingResponseDto> receivedRequests = meetingRepository.findReceiveRequestList(memberId);
        updateMeetingRequestIfNecessary(receivedRequests, findDateTime);

        // 응답에 필요한 정보만 필터링
        return receivedRequests.stream()
            .filter(request -> request.isPending()
                && !request.isDeleted()
                && !CustomExpirationValidator.isExpiredOfDays(request.getRequestTime(), findDateTime, REQUEST_EXPIRE_DAY))
            .sorted(Comparator.comparing(MeetingRequestResponseDto::getRequestTime).reversed())
            .toList();
    }


    // 만료된 신청 & 삭제된 팀 MeetingRequest 상태 갱신 (acceptStatus = EXPIRED)
    private void updateMeetingRequestIfNecessary(final List<? extends MeetingRequestResponseDto> requestList,
        final LocalDateTime findDateTime) {
        Stream<Long> expiredIds = requestList.stream()
            .filter(MeetingRequestResponseDto::isPending)
            .filter(request -> CustomExpirationValidator.isExpiredOfDays(request.getRequestTime(), findDateTime, REQUEST_EXPIRE_DAY))
            .map(MeetingRequestResponseDto::getMeetingRequestId);

        Stream<Long> deletedIds = requestList.stream()
            .filter(MeetingRequestResponseDto::isPending)
            .filter(MeetingRequestResponseDto::isDeleted)
            .map(MeetingRequestResponseDto::getMeetingRequestId);

        List<Long> updateList = Stream.concat(expiredIds, deletedIds)
            .toList();

        meetingRequestRepository.updateRequestToExpired(updateList);
    }

    // 만료된 미팅 & 삭제된 팀 Meeting 상태 갱신 (isOver = true)
    private void updateMeetingsIfNecessary(final List<AcceptedMeetingResponseDto> meetingList, final LocalDateTime findDateTime) {
        Stream<Long> expiredIds = meetingList.stream()
            .filter(meeting -> !meeting.isExpired())
            .filter(meeting -> CustomExpirationValidator.isExpiredOfDays(meeting.getMeetingAcceptTime(), findDateTime, MEETING_EXPIRE_DAY))
            .map(AcceptedMeetingResponseDto::getMeetingId);

        Stream<Long> deletedIds = meetingList.stream()
            .filter(AcceptedMeetingResponseDto::isDeleted)
            .filter(meeting -> !meeting.isExpired())
            .map(AcceptedMeetingResponseDto::getMeetingId);

        List<Long> updateList = Stream.concat(expiredIds, deletedIds)
            .toList();

        meetingRepository.updateMeetingToOver(updateList);
    }

}
