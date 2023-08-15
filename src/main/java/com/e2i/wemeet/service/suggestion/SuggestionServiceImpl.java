package com.e2i.wemeet.service.suggestion;

import com.e2i.wemeet.domain.history.History;
import com.e2i.wemeet.domain.history.HistoryRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.team.data.suggestion.SuggestionTeamData;
import com.e2i.wemeet.dto.response.suggestion.SuggestionResponseDto;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SuggestionServiceImpl implements SuggestionService {


    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final HistoryRepository historyRepository;

    @Transactional
    @Override
    public List<SuggestionResponseDto> readSuggestion(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();

        List<SuggestionTeamData> suggestionTeamList = getSuggestionTeamData(member);
        saveHistory(suggestionTeamList, member);

        return SuggestionResponseDto.of(suggestionTeamList);
    }

    // 추천 받은 팀 History에 저장
    private void saveHistory(List<SuggestionTeamData> teamList, Member member) {
        for (SuggestionTeamData suggestionTeamData : teamList) {
            historyRepository.save(History.builder()
                .member(member)
                .team(suggestionTeamData.team())
                .isLike(false)
                .build());
        }
    }

    private List<SuggestionTeamData> getSuggestionTeamData(Member member) {
        if (member.hasTeam()) {
            // 팀이 있는 경우
            return teamRepository.findSuggestionTeamForTeamLeader(member.getMemberId(),
                member.getCurrentTeam().getMemberNum(), member.getCurrentTeam().getGender());
        }

        // 팀이 없는 경우
        return teamRepository.findSuggestionTeamForUser(member.getMemberId(), member.getGender());
    }
}
