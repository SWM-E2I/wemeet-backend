package com.e2i.wemeet.service.suggestion;

import com.e2i.wemeet.dto.response.suggestion.CheckSuggestionResponseDto;
import com.e2i.wemeet.dto.response.suggestion.SuggestionResponseDto;
import java.util.List;

public interface SuggestionService {

    /**
     * 오늘의 팀 추천 불러오기
     */
    List<SuggestionResponseDto> readSuggestion(Long memberId);

    /*
     * 오늘의 추천 여부 확인
     * */
    CheckSuggestionResponseDto checkTodaySuggestionHistory(Long memberId);
}
