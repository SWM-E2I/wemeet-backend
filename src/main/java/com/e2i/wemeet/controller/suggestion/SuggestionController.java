package com.e2i.wemeet.controller.suggestion;

import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.suggestion.SuggestionResponseDto;
import com.e2i.wemeet.service.suggestion.SuggestionService;
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
        List<SuggestionResponseDto> response = suggestionService.readSuggestion(memberId);

        return ResponseDto.success("Get Suggestion Success", response);
    }
}
