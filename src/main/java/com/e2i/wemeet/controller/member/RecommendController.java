package com.e2i.wemeet.controller.member;

import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.dto.request.member.RecommenderRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.service.member.RecommendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/recommend")
@RestController
public class RecommendController {

    private final RecommendService recommendService;

    @PostMapping
    public ResponseDto<Long> recommend(@MemberId Long memberId,
        @RequestBody @Valid RecommenderRequestDto requestDto) {
        recommendService.recommend(memberId, requestDto.phoneNumber());
        return ResponseDto.success("Recommend Success", null);
    }
}
