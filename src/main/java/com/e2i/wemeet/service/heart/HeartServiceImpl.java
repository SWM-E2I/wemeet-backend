package com.e2i.wemeet.service.heart;

import com.e2i.wemeet.domain.heart.Heart;
import com.e2i.wemeet.domain.heart.HeartRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.exception.badrequest.HeartAlreadyExistsException;
import com.e2i.wemeet.exception.badrequest.NotSendToOwnTeamException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.exception.notfound.TeamNotFoundException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class HeartServiceImpl implements HeartService {

    private static final LocalTime boundaryTime = LocalTime.of(23, 11);
    private final HeartRepository heartRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    @Transactional
    @Override
    public void sendHeart(Long memberId, Long partnerTeamId, LocalDateTime requestTime) {
        Member member = memberRepository.findByIdFetchCode(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();

        Team team = member.getCurrentTeam();

        // 본인 팀에게는 보낼 수 없음
        if (team.getTeamId().equals(partnerTeamId)) {
            throw new NotSendToOwnTeamException();
        }

        // 이미 하트 보냈으면 끝
        checkTodayHeart(team.getTeamId(), requestTime);

        Team partnerTeam = teamRepository.findById(partnerTeamId)
            .orElseThrow(TeamNotFoundException::new);

        heartRepository.save(Heart.builder()
            .team(team)
            .partnerTeam(partnerTeam)
            .build());
    }

    private void checkTodayHeart(Long teamId, LocalDateTime requestTime) {
        LocalDateTime boundaryDateTime = requestTime.with(boundaryTime);
        if (requestTime.isBefore(boundaryDateTime)) {
            boundaryDateTime = boundaryDateTime.minusDays(1);
        }

        heartRepository.findTodayHeart(teamId, boundaryDateTime, requestTime)
            .ifPresent(heart -> {
                throw new HeartAlreadyExistsException();
            });
    }
}
