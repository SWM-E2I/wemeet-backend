package com.e2i.wemeet.service.team;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.domain.code.CodeRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.team_image.TeamImage;
import com.e2i.wemeet.domain.team_image.TeamImageRepository;
import com.e2i.wemeet.domain.team_member.TeamMemberRepository;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.TeamMemberDto;
import com.e2i.wemeet.dto.request.team.UpdateTeamRequestDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import com.e2i.wemeet.exception.notfound.CodeNotFoundException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.service.aws.s3.S3Service;
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
    private final CodeRepository codeRepository;
    private final TeamMemberRepository teamMemberRepository;

    private final TeamImageRepository teamImageRepository;
    private final S3Service s3Service;

    @Value("${aws.s3.teamImageBucket}")
    private String teamImageBucket;

    @Transactional
    @Override
    public void createTeam(final Long memberId, CreateTeamRequestDto createTeamRequestDto,
        List<MultipartFile> images) {
        Member teamLeader = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();
        teamLeader.validateTeamExist();

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
        Member teamLeader = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();

        Team team = teamLeader.getCurrentTeam();

        // 팀 정보 수정
        team.update(updateTeamRequestDto);

        // 팀원 수정
        updateTeamMembers(updateTeamRequestDto.members(), team);

        // 사진 수정
        updateTeamImages(images, team);
    }

    @Transactional(readOnly = true)
    @Override
    public MyTeamDetailResponseDto readTeam(Long memberId) {
        return null;
    }

    @Transactional
    @Override
    public void deleteTeam(Long memberId) {
    }


    private void updateTeamImages(List<MultipartFile> images, Team team) {
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
                .teamImageUrl(imagePath)
                .sequence(sequence + 1)
                .build());
        }
    }

    private void updateTeamMembers(List<TeamMemberDto> teamMemberDtoList, Team team) {
        teamMemberRepository.deleteAllByTeamTeamId(team.getTeamId());
        saveTeamMembers(teamMemberDtoList, team);
    }

    private void saveTeamMembers(List<TeamMemberDto> teamMemberDtoList, Team team) {
        teamMemberDtoList.stream()
            .forEach(teamMember -> teamMemberRepository.save(teamMember.toEntity(
                getCode(teamMember.collegeInfo().collegeCode()), team)));
    }

    private Code getCode(final String groupCodeIdWithCodeId) {
        CodePk collegeCodePk = CodePk.of(groupCodeIdWithCodeId);
        return codeRepository.findByCodePk(collegeCodePk)
            .orElseThrow(CodeNotFoundException::new);
    }
}
