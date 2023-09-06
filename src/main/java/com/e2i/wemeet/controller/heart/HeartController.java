package com.e2i.wemeet.controller.heart;

import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.security.manager.IsManager;
import com.e2i.wemeet.service.heart.HeartService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
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
}
