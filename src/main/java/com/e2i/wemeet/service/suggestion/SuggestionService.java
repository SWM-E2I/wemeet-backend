package com.e2i.wemeet.service.suggestion;

import com.e2i.wemeet.dto.response.suggestion.CheckSuggestionResponseDto;
import com.e2i.wemeet.dto.response.suggestion.SuggestionResponseDto;
import java.time.LocalDateTime;
import java.util.List;

public interface SuggestionService {

    /*
     * 오늘의 팀 추천 불러오기
     * */
    List<SuggestionResponseDto> readSuggestion(Long memberId, LocalDateTime requestTime);


    /*
     * 오늘의 추천 여부 확인
     * */
    CheckSuggestionResponseDto checkTodaySuggestionHistory(Long memberId,
        LocalDateTime requestTime);

    /*
     * 사전 예약 정보 노출
     * */
    List<SuggestionResponseDto> tempSuggestion(Long memberId, LocalDateTime requestTime);
}
