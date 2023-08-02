package com.e2i.wemeet.service.team;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.ModifyTeamRequestDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final SecureRandom random = new SecureRandom();
    private static final int TEAM_CODE_LENGTH = 6;

    // TODO :: service refactoring
    @Override
    @Transactional
    public Long createTeam(Long memberId, CreateTeamRequestDto createTeamRequestDto,
        List<Code> teamPreferenceMeetingTypeList, HttpServletResponse response) {
        return null;
    }

    // TODO :: service refactoring
    @Override
    @Transactional
    public void modifyTeam(Long memberId, ModifyTeamRequestDto modifyTeamRequestDto,
        List<Code> teamPreferenceMeetingTypeList) {
        return;
    }

    // TODO :: service refactoring
    @Override
    @Transactional(readOnly = true)
    public MyTeamDetailResponseDto getMyTeamDetail(Long memberId) {
        return null;
    }

    // TODO :: service refactoring
    @Transactional
    @Override
    public void deleteTeam(Long teamLeaderId) {
        return;
    }

    // TODO :: service refactoring
    private Team getMyTeam(Member member) {
        return null;
    }

    // TODO :: service refactoring
    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();
    }
}
