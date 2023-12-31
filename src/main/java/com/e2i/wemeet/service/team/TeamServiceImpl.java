package com.e2i.wemeet.service.team;

import static com.e2i.wemeet.exception.ErrorCode.BLOCKED_TEAM;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.domain.code.CodeRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.team.data.TeamImageData;
import com.e2i.wemeet.domain.team_image.TeamImage;
import com.e2i.wemeet.domain.team_image.TeamImageRepository;
import com.e2i.wemeet.dto.dsl.TeamInformationDto;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.TeamMemberRequestDto;
import com.e2i.wemeet.dto.request.team.UpdateTeamRequestDto;
import com.e2i.wemeet.dto.response.LeaderResponseDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import com.e2i.wemeet.dto.response.team.MyTeamResponseDto;
import com.e2i.wemeet.dto.response.team.TeamDetailResponseDto;
import com.e2i.wemeet.exception.badrequest.BlockedException;
import com.e2i.wemeet.exception.notfound.CodeNotFoundException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.exception.notfound.TeamNotFoundException;
import com.e2i.wemeet.service.aws.s3.S3Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final TeamImageRepository teamImageRepository;
    private final CodeRepository codeRepository;

    private final S3Service s3Service;


    @Value("${aws.s3.teamImageBucket}")
    private String teamImageBucket;

    @Value("${aws.cloudFront.teamImageDomain}")
    private String teamImageDomain;

    @Transactional
    @Override
    public void createTeam(final Long memberId, CreateTeamRequestDto createTeamRequestDto,
        List<MultipartFile> images) {
        Member teamLeader = memberRepository.findByMemberId(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();
        teamLeader.validateTeamCreation();

        // team 생성
        Team team = teamRepository.save(createTeamRequestDto.toEntity(teamLeader));

        // teamMember 저장
        saveTeamMembers(createTeamRequestDto.members(), team);

        // 사진 저장
        uploadTeamImages(images, team);
    }


    @Transactional
    @Override
    public void updateTeam(final Long memberId, UpdateTeamRequestDto updateTeamRequestDto,
        List<MultipartFile> images) {
        Member teamLeader = memberRepository.findByMemberId(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();

        Team team = teamLeader.getCurrentTeam();

        // 팀 정보 수정
        team.update(updateTeamRequestDto);

        // 팀원 수정
        saveTeamMembers(updateTeamRequestDto.members(), team);

        // 사진 수정
        updateTeamImages(images, team);
    }

    @Transactional(readOnly = true)
    @Override
    public MyTeamResponseDto readTeam(Long memberId) {
        Member teamLeader = memberRepository.findByMemberId(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();

        if (!teamLeader.hasTeam()) {
            return MyTeamResponseDto.of(teamLeader.hasTeam());
        }

        List<TeamImageData> teamImages = teamRepository.findTeamImagesByTeamId(
            teamLeader.getCurrentTeam().getTeamId());

        return MyTeamResponseDto.of(teamLeader.hasTeam(),
            MyTeamDetailResponseDto.of(teamLeader.getCurrentTeam(), teamImages, teamLeader));
    }

    @Transactional
    @Override
    public TeamDetailResponseDto readByTeamId(final Long memberId, final Long teamId, final LocalDateTime readTime) {
        if (teamRepository.isBlockedTeam(memberId, teamId)) {
            throw new BlockedException(BLOCKED_TEAM);
        }

        TeamInformationDto teamInformation = teamRepository.findTeamInformationByTeamId(memberId, teamId, readTime)
            .orElseThrow(TeamNotFoundException::new);
        LeaderResponseDto leader = teamRepository.findLeaderByTeamId(teamId)
            .orElseThrow(TeamNotFoundException::new);
        List<String> teamImages = teamRepository.findTeamImagesByTeamId(teamId)
            .stream()
            .map(TeamImageData::url)
            .toList();

        return TeamDetailResponseDto.of(teamInformation, leader, teamImages);
    }

    @Transactional
    @Override
    public void deleteTeam(Long memberId) {
        Member teamLeader = memberRepository.findByMemberId(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();

        teamLeader.getCurrentTeam().delete(LocalDateTime.now());
    }


    private void updateTeamImages(List<MultipartFile> images, Team team) {
        if (images.isEmpty()) {
            return;
        }
        teamImageRepository.deleteAllByTeamTeamId(team.getTeamId());
        uploadTeamImages(images, team);
    }

    private void uploadTeamImages(List<MultipartFile> images, Team team) {
        for (int sequence = 0; sequence < images.size(); sequence++) {
            String randomKey = UUID.randomUUID().toString();
            String imagePath = String.format("v1/%s/%d/%s.jpg", team.getTeamId(), sequence + 1,
                randomKey);

            s3Service.upload(images.get(sequence), imagePath, teamImageBucket);

            teamImageRepository.save(TeamImage.builder()
                .team(team)
                .teamImageUrl(teamImageDomain + imagePath)
                .sequence(sequence + 1)
                .build());
        }
    }

    private void saveTeamMembers(List<TeamMemberRequestDto> teamMembers, Team team) {
        team.addTeamMembers(
            teamMembers.stream()
                .map(teamMember -> teamMember.toEntity(
                    getCode(teamMember.collegeInfo().collegeCode()), team))
                .toList()
        );
    }

    private Code getCode(final String groupCodeIdWithCodeId) {
        CodePk collegeCodePk = CodePk.of(groupCodeIdWithCodeId);
        return codeRepository.findByCodePk(collegeCodePk)
            .orElseThrow(CodeNotFoundException::new);
    }
}
