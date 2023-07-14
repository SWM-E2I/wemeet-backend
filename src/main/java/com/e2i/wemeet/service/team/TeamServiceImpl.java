package com.e2i.wemeet.service.team;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.config.security.token.TokenInjector;
import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.Role;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.teampreferencemeetingtype.TeamPreferenceMeetingType;
import com.e2i.wemeet.domain.teampreferencemeetingtype.TeamPreferenceMeetingTypeRepository;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.ModifyTeamRequestDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import com.e2i.wemeet.exception.badrequest.TeamAlreadyExistsException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedUnivException;
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
    private final TeamPreferenceMeetingTypeRepository teamPreferenceMeetingTypeRepository;
    private final MemberRepository memberRepository;
    private final TokenInjector tokenInjector;
    private final SecureRandom random = new SecureRandom();
    private static final int TEAM_CODE_LENGTH = 6;

    @Override
    @Transactional
    public Long createTeam(Long memberId, CreateTeamRequestDto createTeamRequestDto,
        List<Code> teamPreferenceMeetingTypeList, HttpServletResponse response) {
        Member member = findMember(memberId);
        validateMember(member);

        // team 생성
        Team team = teamRepository.save(
            createTeamRequestDto.toTeamEntity(createTeamCode(TEAM_CODE_LENGTH), member));
        member.setTeam(team);
        member.setRole(Role.MANAGER);

        // 바뀐 Role을 적용한 token 재발급
        tokenInjector.injectToken(response, new MemberPrincipal(member));

        teamPreferenceMeetingTypeRepository.saveAll(
            teamPreferenceMeetingTypeList.stream()
                .map(
                    teamPreferenceMeetingType -> TeamPreferenceMeetingType.builder()
                        .code(teamPreferenceMeetingType)
                        .team(team)
                        .build()).toList());

        return team.getTeamId();
    }

    @Override
    @Transactional
    public void modifyTeam(Long memberId, ModifyTeamRequestDto modifyTeamRequestDto,
        List<Code> teamPreferenceMeetingTypeList) {
        Member member = findMember(memberId);
        Team team = member.getTeam();

        savePreferenceMeetingType(team, teamPreferenceMeetingTypeList);
        team.updateTeam(modifyTeamRequestDto);
    }

    @Override
    @Transactional(readOnly = true)
    public MyTeamDetailResponseDto getMyTeamDetail(Long memberId) {
        Member member = findMember(memberId);
        Team team = member.getTeam();
        if (team == null) {
            return null;
        }

        List<TeamPreferenceMeetingType> preferenceMeetingTypeList
            = teamPreferenceMeetingTypeRepository.findByTeamTeamId(team.getTeamId());

        return MyTeamDetailResponseDto.builder()
            .memberCount(team.getMemberCount())
            .drinkingOption(team.getDrinkingOption())
            .region(team.getRegion())
            .introduction(team.getIntroduction())
            .additionalActivity(team.getAdditionalActivity())
            .managerImageAuth(team.getMember().isImageAuth())
            .preferenceMeetingTypeList(preferenceMeetingTypeToCodeString(preferenceMeetingTypeList))
            .build();
    }

    /*
     * 팀 생성 가능한 사용자인지 확인
     */
    private void validateMember(Member member) {
        if (isTeamExist(member)) {
            throw new TeamAlreadyExistsException();
        }

        if (isUnivAuth(member)) {
            throw new UnAuthorizedUnivException();
        }
    }

    private List<String> preferenceMeetingTypeToCodeString(
        List<TeamPreferenceMeetingType> preferenceMeetingTypeList) {
        return preferenceMeetingTypeList.stream()
            .map(preferenceMeetingType ->
                preferenceMeetingType.getCode().getCodePk().getGroupCodeId() + "_"
                    + preferenceMeetingType.getCode()
                    .getCodePk().getCodeId())
            .toList();
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);
    }

    private boolean isTeamExist(Member member) {
        return member.getTeam() != null;
    }

    private boolean isUnivAuth(Member member) {
        return member.getCollegeInfo().getMail() == null;
    }

    private void savePreferenceMeetingType(Team team, List<Code> codeList) {
        List<TeamPreferenceMeetingType> preferenceMeetingTypeList = codeList.stream()
            .map(preferenceMeetingTypeCode -> TeamPreferenceMeetingType.builder()
                .team(team)
                .code(preferenceMeetingTypeCode)
                .build())
            .toList();

        teamPreferenceMeetingTypeRepository.deleteAllByTeamTeamId(team.getTeamId());
        teamPreferenceMeetingTypeRepository.saveAll(preferenceMeetingTypeList);
    }

    private String createTeamCode(int length) {
        String alphanumeric = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphanumeric.length());
            sb.append(alphanumeric.charAt(index));
        }

        return sb.toString();
    }
}
