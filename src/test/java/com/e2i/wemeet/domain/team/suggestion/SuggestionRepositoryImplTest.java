package com.e2i.wemeet.domain.team.suggestion;

import static com.e2i.wemeet.domain.team.suggestion.SuggestionRepositoryImpl.SUGGESTION_TEAM_LIMIT;
import static com.e2i.wemeet.support.fixture.MemberFixture.JEONGYEOL;
import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static com.e2i.wemeet.support.fixture.MemberFixture.SEYUN;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamImagesFixture.BASIC_TEAM_IMAGE;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_woman;
import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.domain.history.History;
import com.e2i.wemeet.domain.history.HistoryRepository;
import com.e2i.wemeet.domain.member.Block;
import com.e2i.wemeet.domain.member.BlockRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.team.data.suggestion.SuggestionHistoryData;
import com.e2i.wemeet.domain.team.data.suggestion.SuggestionTeamData;
import com.e2i.wemeet.domain.team_image.TeamImageRepository;
import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

class SuggestionRepositoryImplTest extends AbstractRepositoryUnitTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamImageRepository teamImageRepository;

    @Autowired
    @Qualifier("suggestionRepositoryImpl")
    private SuggestionRepository suggestionRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @DisplayName("오늘의 추천 팀을 랜덤으로 조회할 수 있다.")
    @Test
    void findSuggestionTeamForUser() {
        // given
        final Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));
        Member jeongyeol = memberRepository.save(JEONGYEOL.create(HANYANG_CODE));

        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
        Team jeonyeolTeam = teamRepository.save(HONGDAE_TEAM_1.create(jeongyeol, create_3_man()));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        Team seyunTeam = teamRepository.save(HONGDAE_TEAM_1.create(seyun, create_3_man()));
        saveTeamImages(rimTeam, jeonyeolTeam, kaiTeam, seyunTeam);

        // when
        List<SuggestionTeamData> suggestion = suggestionRepository.findSuggestionTeamForUser(rim.getMemberId(), rim.getGender());

        // then
        assertThat(suggestion).hasSize(SUGGESTION_TEAM_LIMIT)
            .extracting("team")
            .containsAnyOf(jeonyeolTeam, kaiTeam, seyunTeam);
    }

    @DisplayName("차단된 사용자는 추천되지 않는다.")
    @Test
    void findSuggestionTeamForUserWithoutBlock() {
        // given
        Member rim = memberRepository.save(RIM.create(ANYANG_CODE));
        Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));
        Member jeongyeol = memberRepository.save(JEONGYEOL.create(HANYANG_CODE));

        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
        Team jeonyeolTeam = teamRepository.save(HONGDAE_TEAM_1.create(jeongyeol, create_3_man()));
        Team seyunTeam = teamRepository.save(HONGDAE_TEAM_1.create(seyun, create_3_man()));
        saveTeamImages(rimTeam, jeonyeolTeam, seyunTeam);

        blockRepository.save(new Block(rim, seyun));

        // when
        List<SuggestionTeamData> suggestion = suggestionRepository.findSuggestionTeamForUser(rim.getMemberId(), rim.getGender());

        // then
        assertThat(suggestion).hasSize(1)
            .extracting("team")
            .containsExactly(jeonyeolTeam);
    }

    @DisplayName("오늘 추천받은 팀을 조회할 수 있다.")
    @Test
    void findHistory() {
        // given
        Member rim = memberRepository.save(RIM.create(ANYANG_CODE));
        Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));
        Member jeongyeol = memberRepository.save(JEONGYEOL.create(HANYANG_CODE));

        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
        Team jeonyeolTeam = teamRepository.save(HONGDAE_TEAM_1.create(jeongyeol, create_3_man()));
        Team seyunTeam = teamRepository.save(HONGDAE_TEAM_1.create(seyun, create_3_man()));
        saveTeamImages(rimTeam, jeonyeolTeam, seyunTeam);

        List<SuggestionTeamData> suggestion = suggestionRepository.findSuggestionTeamForUser(rim.getMemberId(), rim.getGender());
        saveHistories(suggestion, rim);

        // when
        LocalDateTime requestTime = LocalDateTime.now();
        List<History> histories = suggestionRepository.findHistory(rim.getMemberId(), requestTime);

        // then
        assertThat(histories).hasSize(SUGGESTION_TEAM_LIMIT)
            .extracting("team")
            .containsAnyOf(jeonyeolTeam, seyunTeam);
    }

    @DisplayName("차단된 사용자는 히스토리에서 조회되지 않는다.")
    @Test
    void findHistoryWithoutBlockedMember() {
        // given
        Member rim = memberRepository.save(RIM.create(ANYANG_CODE));
        Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));
        Member jeongyeol = memberRepository.save(JEONGYEOL.create(HANYANG_CODE));

        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
        Team jeonyeolTeam = teamRepository.save(HONGDAE_TEAM_1.create(jeongyeol, create_3_man()));
        Team seyunTeam = teamRepository.save(HONGDAE_TEAM_1.create(seyun, create_3_man()));
        saveTeamImages(rimTeam, jeonyeolTeam, seyunTeam);

        List<SuggestionTeamData> suggestion = suggestionRepository.findSuggestionTeamForUser(rim.getMemberId(), rim.getGender());
        saveHistories(suggestion, rim);
        LocalDateTime requestTime = LocalDateTime.now();

        // when
        blockRepository.save(new Block(rim, seyun));
        List<History> histories = suggestionRepository.findHistory(rim.getMemberId(), requestTime);

        // then
        assertThat(histories).hasSize(1)
            .extracting("team")
            .containsExactly(jeonyeolTeam);
    }

    @DisplayName("추천받은 팀 정보를 조회할 수 있다.")
    @Test
    void findSuggestionHistoryTeam() {
        // given
        Member rim = memberRepository.save(RIM.create(ANYANG_CODE));
        Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));
        Member jeongyeol = memberRepository.save(JEONGYEOL.create(HANYANG_CODE));

        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
        Team jeonyeolTeam = teamRepository.save(HONGDAE_TEAM_1.create(jeongyeol, create_3_man()));
        Team seyunTeam = teamRepository.save(HONGDAE_TEAM_1.create(seyun, create_3_man()));
        saveTeamImages(rimTeam, jeonyeolTeam, seyunTeam);

        List<SuggestionTeamData> suggestion = suggestionRepository.findSuggestionTeamForUser(rim.getMemberId(), rim.getGender());
        saveHistories(suggestion, rim);
        LocalDateTime requestTime = LocalDateTime.now();

        // when
        List<SuggestionHistoryData> histories = suggestionRepository.findSuggestionHistoryTeam(rim.getMemberId(), requestTime);

        // then
        assertThat(histories).hasSize(suggestion.size())
            .extracting("teamId")
            .containsAnyOf(jeonyeolTeam.getTeamId(), seyunTeam.getTeamId());
    }

    @DisplayName("차단한 사용자는 추천받은 팀을 조회할 때 제외된다.")
    @Test
    void findSuggestionHistoryTeamWithoutBlockMember() {
        // given
        Member rim = memberRepository.save(RIM.create(ANYANG_CODE));
        Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));
        Member jeongyeol = memberRepository.save(JEONGYEOL.create(HANYANG_CODE));

        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
        Team jeonyeolTeam = teamRepository.save(HONGDAE_TEAM_1.create(jeongyeol, create_3_man()));
        Team seyunTeam = teamRepository.save(HONGDAE_TEAM_1.create(seyun, create_3_man()));
        saveTeamImages(rimTeam, jeonyeolTeam, seyunTeam);

        List<SuggestionTeamData> suggestion = suggestionRepository.findSuggestionTeamForUser(rim.getMemberId(), rim.getGender());
        saveHistories(suggestion, rim);
        LocalDateTime requestTime = LocalDateTime.now();

        // when
        blockRepository.save(new Block(rim, seyun));
        List<SuggestionHistoryData> histories = suggestionRepository.findSuggestionHistoryTeam(rim.getMemberId(), requestTime);

        // then
        assertThat(histories).hasSize(1)
            .extracting("teamId")
            .containsAnyOf(jeonyeolTeam.getTeamId());
    }

    private void saveHistories(List<SuggestionTeamData> suggestion, Member rim) {
        List<History> suggestionHistories = suggestion.stream()
            .map(suggestionTeamData -> History.builder()
                .member(rim)
                .team(suggestionTeamData.team())
                .isLiked(false)
                .build())
            .toList();
        historyRepository.saveAll(suggestionHistories);
    }

    private void saveTeamImages(Team... teams) {
        for (Team team : teams) {
            teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(team));
        }
    }
}