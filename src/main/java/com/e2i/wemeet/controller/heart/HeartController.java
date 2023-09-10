package com.e2i.wemeet.controller.heart;

import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.heart.ReceivedHeartResponseDto;
import com.e2i.wemeet.dto.response.heart.SentHeartResponseDto;
import com.e2i.wemeet.security.manager.IsManager;
import com.e2i.wemeet.service.heart.HeartService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/heart")
@RestController
public class HeartController {

    private final HeartService heartService;

    @IsManager
    @PostMapping("/{partnerTeamId}")
    public ResponseDto<Void> sendHeart(@MemberId Long memberId, @PathVariable Long partnerTeamId) {
        LocalDateTime requestTime = LocalDateTime.now();
        heartService.sendHeart(memberId, partnerTeamId, requestTime);

        return ResponseDto.success("Send Heart Success");
    }

    @GetMapping("/sent")
    public ResponseDto<List<SentHeartResponseDto>> getSentHeart(@MemberId Long memberId) {
        LocalDateTime requestTime = LocalDateTime.now();
        List<SentHeartResponseDto> result = heartService.getSentHeart(memberId, requestTime);

        return ResponseDto.success("Get Sent Heart Detail Success", result);
    }

    @GetMapping("/received")
    public ResponseDto<List<ReceivedHeartResponseDto>> getReceivedHeart(@MemberId Long memberId) {
        LocalDateTime requestTime = LocalDateTime.now();
        List<ReceivedHeartResponseDto> result = heartService.getReceivedHeart(memberId,
            requestTime);

        return ResponseDto.success("Get Received Heart Detail Success", result);
    }
}
