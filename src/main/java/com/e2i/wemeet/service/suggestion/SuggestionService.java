package com.e2i.wemeet.service.suggestion;

import com.e2i.wemeet.dto.response.suggestion.SuggestionResponseDto;
import java.util.List;

public interface SuggestionService {

    List<SuggestionResponseDto> readSuggestion(Long memberId);

}
