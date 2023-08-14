package com.e2i.wemeet.controller.meeting;

import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.dto.request.meeting.MeetingHandleRequestDto;
import com.e2i.wemeet.dto.request.meeting.SendMeetingRequestDto;
import com.e2i.wemeet.dto.request.meeting.SendMeetingWithMessageRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
import com.e2i.wemeet.service.meeting.MeetingService;
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

    private final MeetingService meetingService;

    @PostMapping("/meeting")
    public ResponseDto<Void> sendMeetingRequest(@MemberId Long memberId, @RequestBody SendMeetingRequestDto requestDto) {
        meetingService.sendRequest(requestDto, memberId);

        return ResponseDto.success("Send meeting request success");
    }

    @PostMapping("/v1/meeting/message")
    public ResponseDto<Void> sendMeetingRequestWithMessage(@MemberId Long memberId,
        @RequestBody SendMeetingWithMessageRequestDto requestDto) {
        meetingService.sendRequestWithMessage(requestDto, memberId);

        return ResponseDto.success("Send meeting request with message success");
    }

    @PostMapping("/v1/meeting/{meetingRequestId}")
    public ResponseDto<AcceptStatus> handleMeetingRequest(@MemberId Long memberId,
        @RequestBody MeetingHandleRequestDto requestDto,
        @PathVariable Long meetingRequestId) {
        AcceptStatus acceptStatus = meetingService.handleRequest(requestDto, memberId, meetingRequestId);

        return ResponseDto.success("Change Meeting Request to " + acceptStatus.name(), acceptStatus);
    }

    @GetMapping("/v1/meeting/sent")
    public ResponseDto<List<SentMeetingResponseDto>> getSentMeetingRequestList(@MemberId Long memberId) {
        List<SentMeetingResponseDto> sentRequestList = meetingService.getSentRequestList(memberId);

        return ResponseDto.success("Get sent meeting request list success", sentRequestList);
    }

    @GetMapping("/v1/meeting/receive")
    public ResponseDto<List<ReceivedMeetingResponseDto>> getReceiveMeetingRequestList(@MemberId Long memberId) {
        List<ReceivedMeetingResponseDto> receiveRequestList = meetingService.getReceiveRequestList(memberId);

        return ResponseDto.success("Get receive meeting request list success", receiveRequestList);
    }

}
