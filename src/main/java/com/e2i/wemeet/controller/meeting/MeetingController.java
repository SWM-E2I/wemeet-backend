package com.e2i.wemeet.controller.meeting;

import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.dto.request.meeting.MeetingRequestAcceptDto;
import com.e2i.wemeet.dto.request.meeting.SendMeetingRequestDto;
import com.e2i.wemeet.dto.request.meeting.SendMeetingWithMessageRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
import com.e2i.wemeet.service.meeting.MeetingHandleService;
import com.e2i.wemeet.service.meeting.MeetingListService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1")
@RestController
public class MeetingController {

    private final MeetingHandleService meetingHandleService;
    private final MeetingListService meetingListService;

    @PostMapping("/meeting")
    public ResponseDto<Void> sendMeetingRequest(@MemberId Long memberId, @RequestBody SendMeetingRequestDto requestDto) {
        meetingHandleService.sendRequest(requestDto, memberId);

        return ResponseDto.success("Send meeting request success");
    }

    @PostMapping("/v1/meeting/message")
    public ResponseDto<Void> sendMeetingRequestWithMessage(@MemberId Long memberId,
        @RequestBody SendMeetingWithMessageRequestDto requestDto) {
        meetingHandleService.sendRequestWithMessage(requestDto, memberId);

        return ResponseDto.success("Send meeting request with message success");
    }

    @PostMapping("/v1/meeting/accept/{meetingRequestId}")
    public ResponseDto<Long> acceptMeetingRequest(@MemberId Long memberId,
        @RequestBody MeetingRequestAcceptDto requestDto,
        @PathVariable Long meetingRequestId) {
        final String openChatLink = requestDto.kakaoOpenChatLink();
        final LocalDateTime acceptDateTime = LocalDateTime.now();

        Long meetingId = meetingHandleService.acceptRequest(openChatLink, memberId, meetingRequestId, acceptDateTime);

        return ResponseDto.success("Meeting was successfully matched.", meetingId);
    }

    @PostMapping("/v1/meeting/reject/{meetingRequestId}")
    public ResponseDto<AcceptStatus> rejectMeetingRequest(@MemberId Long memberId,
        @PathVariable Long meetingRequestId) {
        LocalDateTime rejectDateTime = LocalDateTime.now();
        AcceptStatus acceptStatus = meetingHandleService.rejectRequest(memberId, meetingRequestId, rejectDateTime);

        return ResponseDto.success("Meeting request successfully Rejected", acceptStatus);
    }

    // TODO: 성사된 미팅 목록 조회 API


    @GetMapping("/v1/meeting/sent")
    public ResponseDto<List<SentMeetingResponseDto>> getSentMeetingRequestList(@MemberId Long memberId) {
        List<SentMeetingResponseDto> sentRequestList = meetingListService.getSentRequestList(memberId);

        return ResponseDto.success("Get sent meeting request list success", sentRequestList);
    }

    @GetMapping("/v1/meeting/receive")
    public ResponseDto<List<ReceivedMeetingResponseDto>> getReceiveMeetingRequestList(@MemberId Long memberId) {
        List<ReceivedMeetingResponseDto> receiveRequestList = meetingListService.getReceiveRequestList(memberId);

        return ResponseDto.success("Get receive meeting request list success", receiveRequestList);
    }

}