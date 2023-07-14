package com.e2i.wemeet.service.team;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.config.security.token.TokenInjector;
import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.Role;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.team.invitation.InvitationAcceptStatus;
import com.e2i.wemeet.domain.team.invitation.TeamInvitation;
import com.e2i.wemeet.domain.team.invitation.TeamInvitationRepository;
import com.e2i.wemeet.domain.teampreferencemeetingtype.TeamPreferenceMeetingType;
import com.e2i.wemeet.domain.teampreferencemeetingtype.TeamPreferenceMeetingTypeRepository;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.InviteTeamRequestDto;
import com.e2i.wemeet.dto.request.team.ModifyTeamRequestDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import com.e2i.wemeet.exception.badrequest.GenderNotMatchException;
import com.e2i.wemeet.exception.badrequest.InvitationAlreadyExistsException;
import com.e2i.wemeet.exception.badrequest.TeamAlreadyActiveException;
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
    private final TeamInvitationRepository teamInvitationRepository;
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
        team.setMember(member);
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

    @Transactional
    @Override
    public void inviteTeam(Long memberId, InviteTeamRequestDto inviteTeamRequestDto) {
        Member teamMember = memberRepository.findByNicknameAndMemberCode(
                inviteTeamRequestDto.nickname(),
                inviteTeamRequestDto.memberCode())
            .orElseThrow(MemberNotFoundException::new);

        Member manager = findMember(memberId);
        Team team = manager.getTeam();

        validateInvitation(teamMember, manager, team);

        teamInvitationRepository.save(
            TeamInvitation.builder()
                .acceptStatus(InvitationAcceptStatus.WAITING)
                .member(teamMember)
                .team(team)
                .build());
    }

    /*
     * 팀 생성 및 수락이 가능한 사용자인지 확인
     */
    private void validateMember(Member member) {
        if (isTeamExist(member)) {
            throw new TeamAlreadyExistsException();
        }

        if (isUnivAuth(member)) {
            throw new UnAuthorizedUnivException();
        }
    }

    /*
     * 초대 신청이 가능한지 확인
     */
    private void validateInvitation(Member teamMember, Member manager, Team team) {
        // 이미 팀이 활성화된 경우
        if (team.isActive()) {
            throw new TeamAlreadyActiveException();
        }

        // 초대한 사용자에게 이미 소속 팀이 있는 경우
        if (isTeamExist(teamMember)) {
            throw new TeamAlreadyExistsException();
        }

        // 초대한 사용자가 대학 인증이 안되어 있는 경우
        if (isUnivAuth(teamMember)) {
            throw new UnAuthorizedUnivException();
        }

        // 초대한 사용자와 성별이 다른 경우
        if (manager.getGender() != teamMember.getGender()) {
            throw new GenderNotMatchException();
        }

        // 이미 초대한 사용자인 경우
        if (isInvitationExist(teamMember, team)) {
            throw new InvitationAlreadyExistsException();
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

    private boolean isInvitationExist(Member member, Team team) {
        return teamInvitationRepository.findByMemberMemberIdAndTeamTeamIdAndAcceptStatus(
                member.getMemberId(),
                team.getTeamId(), InvitationAcceptStatus.WAITING)
            .isPresent();
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
