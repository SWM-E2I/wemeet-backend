package com.e2i.wemeet.service.suggestion;

import com.e2i.wemeet.domain.history.History;
import com.e2i.wemeet.domain.history.HistoryRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.team.data.suggestion.SuggestionHistoryData;
import com.e2i.wemeet.domain.team.data.suggestion.SuggestionTeamData;
import com.e2i.wemeet.dto.response.suggestion.CheckSuggestionResponseDto;
import com.e2i.wemeet.dto.response.suggestion.SuggestionHistoryTeamDto;
import com.e2i.wemeet.dto.response.suggestion.SuggestionResponseDto;
import com.e2i.wemeet.exception.badrequest.SuggestionHistoryExistsException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import java.time.LocalDateTime;
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

        // 이미 추천을 받은 경우
        if (!teamRepository.findHistory(memberId, LocalDateTime.now()).isEmpty()) {
            throw new SuggestionHistoryExistsException();
        }

        List<SuggestionTeamData> suggestionTeamList = getSuggestionTeamData(member);
        saveHistory(suggestionTeamList, member);

        return SuggestionResponseDto.of(suggestionTeamList);
    }

    @Transactional(readOnly = true)
    @Override
    public CheckSuggestionResponseDto checkTodaySuggestionHistory(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();

        LocalDateTime requestTime = LocalDateTime.now();
        return findSuggestionHistory(member.getMemberId(), requestTime);
    }

    private CheckSuggestionResponseDto findSuggestionHistory(Long memberId,
        LocalDateTime requestTime) {
        List<History> histories = teamRepository.findHistory(memberId, requestTime);

        if (!histories.isEmpty()) {
            // 이미 추천을 받은 경우
            List<SuggestionHistoryData> suggestionHistoryDataList = teamRepository
                .findSuggestionHistoryTeam(memberId, requestTime);

            return CheckSuggestionResponseDto.builder()
                .isReceivedSuggestion(true)
                .teams(SuggestionHistoryTeamDto.of(suggestionHistoryDataList))
                .build();
        }

        return CheckSuggestionResponseDto.builder()
            .isReceivedSuggestion(false)
            .build();
    }

    // 추천 받은 팀 History에 저장
    private void saveHistory(List<SuggestionTeamData> teamList, Member member) {
        for (SuggestionTeamData suggestionTeamData : teamList) {
            historyRepository.save(History.builder()
                .member(member)
                .team(suggestionTeamData.team())
                .isLiked(false)
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
