package com.e2i.wemeet.controller.suggestion;

import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.suggestion.CheckSuggestionResponseDto;
import com.e2i.wemeet.dto.response.suggestion.SuggestionResponseDto;
import com.e2i.wemeet.service.suggestion.SuggestionService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/suggestion")
@RestController
public class SuggestionController {

    private final SuggestionService suggestionService;

    @GetMapping
    public ResponseDto<List<SuggestionResponseDto>> readSuggestion(@MemberId Long memberId) {
        LocalDateTime requestTime = LocalDateTime.now();
//        List<SuggestionResponseDto> response = suggestionService.readSuggestion(memberId, requestTime);
        // 사전 예약 카드 노출
        List<SuggestionResponseDto> response = suggestionService.tempSuggestion(memberId,
            requestTime);

        return ResponseDto.success("Get Suggestion Success", response);
    }

    @GetMapping("/check")
    public ResponseDto<CheckSuggestionResponseDto> checkTodaySuggestionHistory(
        @MemberId Long memberId) {
        LocalDateTime requestTime = LocalDateTime.now();
        CheckSuggestionResponseDto response = suggestionService.checkTodaySuggestionHistory(
            memberId, requestTime);

        return ResponseDto.success("Check Suggestion Success", response);
    }
}
