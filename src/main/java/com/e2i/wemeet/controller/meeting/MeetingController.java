package com.e2i.wemeet.controller.meeting;

import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.dto.request.meeting.SendMeetingRequestDto;
import com.e2i.wemeet.dto.request.meeting.SendMeetingWithMessageRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.meeting.AcceptedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
import com.e2i.wemeet.service.meeting.MeetingHandleService;
import com.e2i.wemeet.service.meeting.MeetingListService;
import jakarta.validation.Valid;
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
@RequestMapping("/v1/meeting")
@RestController
public class MeetingController {

    private final MeetingHandleService meetingHandleService;
    private final MeetingListService meetingListService;

    @PostMapping
    public ResponseDto<Void> sendMeetingRequest(@MemberId Long memberId, @RequestBody @Valid SendMeetingRequestDto requestDto) {
        meetingHandleService.sendRequest(requestDto, memberId);

        return ResponseDto.success("Send meeting request success");
    }

    @PostMapping("/message")
    public ResponseDto<Void> sendMeetingRequestWithMessage(@MemberId Long memberId,
        @RequestBody @Valid SendMeetingWithMessageRequestDto requestDto) {
        meetingHandleService.sendRequestWithMessage(requestDto, memberId);

        return ResponseDto.success("Send meeting request with message success");
    }

    @PostMapping("/accept/{meetingRequestId}")
    public ResponseDto<Long> acceptMeetingRequest(@MemberId Long memberId,
        @PathVariable Long meetingRequestId) {
        final LocalDateTime acceptDateTime = LocalDateTime.now();

        Long meetingId = meetingHandleService.acceptRequest(memberId, meetingRequestId, acceptDateTime);

        return ResponseDto.success("Meeting was successfully matched", meetingId);
    }

    @PostMapping("/reject/{meetingRequestId}")
    public ResponseDto<AcceptStatus> rejectMeetingRequest(@MemberId Long memberId,
        @PathVariable Long meetingRequestId) {
        LocalDateTime rejectDateTime = LocalDateTime.now();
        AcceptStatus acceptStatus = meetingHandleService.rejectRequest(memberId, meetingRequestId, rejectDateTime);

        return ResponseDto.success("Meeting request successfully Rejected", acceptStatus);
    }

    @GetMapping("/accepted")
    public ResponseDto<List<AcceptedMeetingResponseDto>> getAcceptedMeetingList(@MemberId Long memberId) {
        final LocalDateTime findDateTime = LocalDateTime.now();
        List<AcceptedMeetingResponseDto> acceptedMeetingList = meetingListService.getAcceptedMeetingList(memberId, findDateTime);

        return ResponseDto.success("Get accepted meeting list success", acceptedMeetingList);
    }


    @GetMapping("/sent")
    public ResponseDto<List<SentMeetingResponseDto>> getSentMeetingRequestList(@MemberId Long memberId) {
        final LocalDateTime findDateTime = LocalDateTime.now();
        List<SentMeetingResponseDto> sentRequestList = meetingListService.getSentRequestList(memberId, findDateTime);

        return ResponseDto.success("Get sent meeting request list success", sentRequestList);
    }

    @GetMapping("/received")
    public ResponseDto<List<ReceivedMeetingResponseDto>> getReceivedMeetingRequestList(@MemberId Long memberId) {
        final LocalDateTime findDateTime = LocalDateTime.now();
        List<ReceivedMeetingResponseDto> receiveRequestList = meetingListService.getReceiveRequestList(memberId, findDateTime);

        return ResponseDto.success("Get receive meeting request list success", receiveRequestList);
    }


}
