package com.e2i.wemeet.service.team;

import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.UpdateTeamRequestDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;


    @Override
    public void createTeam(Long memberId, CreateTeamRequestDto createTeamRequestDto) {
    }

    @Override
    public void updateTeam(Long memberId, UpdateTeamRequestDto updateTeamRequestDto) {
    }

    @Override
    public MyTeamDetailResponseDto readTeam(Long memberId) {
        return null;
    }

    @Override
    public void deleteTeam(Long memberId) {
    }
}
