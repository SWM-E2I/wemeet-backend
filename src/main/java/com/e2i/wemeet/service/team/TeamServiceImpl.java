package com.e2i.wemeet.service.team;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.domain.profile_image.ProfileImage;
import com.e2i.wemeet.domain.profile_image.ProfileImageRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.team.invitation.InvitationAcceptStatus;
import com.e2i.wemeet.domain.team.invitation.TeamInvitationRepository;
import com.e2i.wemeet.domain.teampreferencemeetingtype.TeamPreferenceMeetingType;
import com.e2i.wemeet.domain.teampreferencemeetingtype.TeamPreferenceMeetingTypeRepository;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.ModifyTeamRequestDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import com.e2i.wemeet.dto.response.team.TeamManagementResponseDto;
import com.e2i.wemeet.dto.response.team.TeamMemberResponseDto;
import com.e2i.wemeet.exception.badrequest.ManagerSelfDeletionException;
import com.e2i.wemeet.exception.badrequest.NonTeamMemberException;
import com.e2i.wemeet.exception.badrequest.NotBelongToTeamException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
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
    private final SecureRandom random = new SecureRandom();
    private static final int TEAM_CODE_LENGTH = 6;

    @Override
    @Transactional
    public Long createTeam(Long memberId, CreateTeamRequestDto createTeamRequestDto,
        List<Code> teamPreferenceMeetingTypeList, HttpServletResponse response) {
        Member member = findMember(memberId);

        // team 생성
        Team team = teamRepository.save(
            createTeamRequestDto.toTeamEntity(createTeamCode(TEAM_CODE_LENGTH, memberId), member));
        team.addMember(member);
        member.setRole(Role.MANAGER);

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
            .memberCount(team.getMemberNum())
            .drinkingOption(team.getDrinkingOption())
            .region(team.getRegion())
            .introduction(team.getIntroduction())
            .additionalActivity(team.getAdditionalActivity())
            .managerImageAuth(team.getTeamLeader().getImageAuth())
            .preferenceMeetingTypeList(preferenceMeetingTypeToCodeString(preferenceMeetingTypeList))
            .build();
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
            .teamCode(team.getTeamCode())
            .members(members)
            .build();
    }

    @Transactional
    @Override
    public void deleteTeam(Long teamLeaderId) {
        Member teamLeader = findMember(teamLeaderId);
        teamLeader.deleteTeam();
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

        member.withdrawalFromTeam();
    }

    private Team getMyTeam(Member member) {
        if (!isTeamExist(member)) {
            throw new NotBelongToTeamException();
        }
        return member.getTeam()
            .checkTeamValid();
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
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();
    }

    private boolean isTeamExist(Member member) {
        return member.getTeam() != null;
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

    private String createTeamCode(int length, Long managerId) {
        String alphanumeric = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphanumeric.length());
            sb.append(alphanumeric.charAt(index));
        }

        return String.format("%d@%s", managerId, sb);
    }
}
