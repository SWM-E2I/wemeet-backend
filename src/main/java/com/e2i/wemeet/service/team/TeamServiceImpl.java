package com.e2i.wemeet.service.team;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.config.security.token.TokenInjector;
import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.Role;
import com.e2i.wemeet.domain.profileimage.ProfileImage;
import com.e2i.wemeet.domain.profileimage.ProfileImageRepository;
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
import com.e2i.wemeet.dto.response.team.TeamManagementResponseDto;
import com.e2i.wemeet.dto.response.team.TeamMemberResponseDto;
import com.e2i.wemeet.exception.badrequest.GenderNotMatchException;
import com.e2i.wemeet.exception.badrequest.InvitationAlreadyExistsException;
import com.e2i.wemeet.exception.badrequest.InvitationAlreadySetException;
import com.e2i.wemeet.exception.badrequest.ManagerSelfDeletionException;
import com.e2i.wemeet.exception.badrequest.NonTeamMemberException;
import com.e2i.wemeet.exception.badrequest.NotBelongToTeamException;
import com.e2i.wemeet.exception.badrequest.TeamAlreadyActiveException;
import com.e2i.wemeet.exception.badrequest.TeamAlreadyExistsException;
import com.e2i.wemeet.exception.notfound.InvitationNotFoundException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedUnivException;
import jakarta.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final ProfileImageRepository profileImageRepository;
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

    @Transactional
    @Override
    public void takeAcceptStatus(Long memberId, Long invitationId, boolean accepted) {
        Member member = findMember(memberId);
        validateMember(member);

        TeamInvitation teamInvitation =
            teamInvitationRepository.findByTeamInvitationIdAndMemberMemberId(
                    invitationId, memberId)
                .orElseThrow(InvitationNotFoundException::new);

        if (teamInvitation.getAcceptStatus() != InvitationAcceptStatus.WAITING) {
            throw new InvitationAlreadySetException();
        }

        if (accepted) {
            acceptInvitation(teamInvitation);
        } else {
            rejectInvitation(teamInvitation);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public TeamManagementResponseDto getTeamMemberList(Long memberId) {
        Member member = findMember(memberId);
        Team team = getMyTeam(member);

        List<TeamMemberResponseDto> members = new ArrayList<>();
        members.addAll(findWaitingMembers(team));
        members.addAll(findTeamMembers(memberId, team));

        return TeamManagementResponseDto.builder()
            .managerId(memberId)
            .teamCode(team.getTeamCode())
            .members(members)
            .build();
    }

    @Transactional
    @Override
    public void deleteTeam(Long memberId, HttpServletResponse response) {
        Member member = findMember(memberId);
        Team team = getMyTeam(member);

        teamRepository.delete(team);

        member.setRole(Role.USER);
        tokenInjector.injectToken(response, new MemberPrincipal(member));
    }

    @Transactional
    @Override
    public void deleteTeamMember(Long managerId, Long memberId) {
        if (managerId.equals(memberId)) {
            throw new ManagerSelfDeletionException();
        }

        Member manager = findMember(managerId);
        Member member = findMember(memberId);
        Team team = getMyTeam(manager);

        if (member.getTeam() != team) {
            throw new NonTeamMemberException();
        }

        member.setTeam(null);
    }

    private Team getMyTeam(Member member) {
        if (!isTeamExist(member)) {
            throw new NotBelongToTeamException();
        }

        return member.getTeam();
    }

    /*
     * 수락 대기 중인 사용자 목록 조회
     */
    private List<TeamMemberResponseDto> findWaitingMembers(Team team) {
        return teamInvitationRepository.findByTeamTeamIdAndAcceptStatus(team.getTeamId(),
                InvitationAcceptStatus.WAITING)
            .stream()
            .map(teamInvitation -> {
                Member member = teamInvitation.getMember();
                Optional<ProfileImage> profileImage = profileImageRepository
                    .findByMemberMemberIdAndIsMain(member.getMemberId(), true);

                return TeamMemberResponseDto.builder()
                    .memberId(member.getMemberId())
                    .nickname(member.getNickname())
                    .memberCode(member.getMemberCode())
                    .profileImage(
                        profileImage.map(ProfileImage::getLowResolutionBasicUrl).orElse(null))
                    .isAccepted(false)
                    .build();
            })
            .toList();
    }


    /*
     * 팀원 목록 조회
     */
    private List<TeamMemberResponseDto> findTeamMembers(Long memberId, Team team) {
        return team.getMembers().stream()
            .filter(teamMember -> !teamMember.getMemberId().equals(memberId))
            .map(teamMember -> {
                Optional<ProfileImage> profileImage
                    = profileImageRepository.findByMemberMemberIdAndIsMain(
                    teamMember.getMemberId(), true);

                return TeamMemberResponseDto.builder()
                    .memberId(teamMember.getMemberId())
                    .nickname(teamMember.getNickname())
                    .memberCode(teamMember.getMemberCode())
                    .profileImage(
                        profileImage.map(ProfileImage::getLowResolutionBasicUrl).orElse(null))
                    .isAccepted(true)
                    .build();
            })
            .toList();
    }

    private void acceptInvitation(TeamInvitation teamInvitation) {
        Team team = teamInvitation.getTeam();
        if (isActiveTeam(team)) {
            throw new TeamAlreadyActiveException();
        }

        if (team.getMembers().size() + 1 == team.getMemberCount()) {
            team.setActive(true);
        }
        teamInvitation.updateAcceptStatus(InvitationAcceptStatus.ACCEPT);
        team.setMember(teamInvitation.getMember());
    }

    private void rejectInvitation(TeamInvitation teamInvitation) {
        teamInvitation.updateAcceptStatus(InvitationAcceptStatus.REJECT);
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

    private boolean isActiveTeam(Team team) {
        return team.isActive();
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
