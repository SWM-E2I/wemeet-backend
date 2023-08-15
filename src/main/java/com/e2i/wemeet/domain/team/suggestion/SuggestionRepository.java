package com.e2i.wemeet.domain.team.suggestion;

import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.domain.team.data.suggestion.SuggestionTeamData;
import java.util.List;

public interface SuggestionRepository {

    /*
     * 팀이 있는 경우 추천 리스트 조회
     * */

    List<SuggestionTeamData> findSuggestionTeamForTeamLeader(Long memberId, int memberNum,
        Gender gender);

    /*
     * 팀이 없는 경우 추천 리스트 조회
     * */

    List<SuggestionTeamData> findSuggestionTeamForUser(Long memberId, Gender gender);
}
