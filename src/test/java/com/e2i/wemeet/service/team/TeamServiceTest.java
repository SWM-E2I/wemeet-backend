package com.e2i.wemeet.service.team;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.Role;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.teampreferencemeetingtype.TeamPreferenceMeetingTypeRepository;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.ModifyTeamRequestDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import com.e2i.wemeet.exception.badrequest.TeamAlreadyExistsException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedUnivException;
import com.e2i.wemeet.support.fixture.MemberFixture;
import com.e2i.wemeet.support.fixture.TeamFixture;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamPreferenceMeetingTypeRepository teamPreferenceMeetingTypeRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    private static final Member member = MemberFixture.KAI.create();
    private static final Team team = TeamFixture.TEST_TEAM.create();
    private static final List<Code> preferenceMeetingTypeCode = new ArrayList<>();

    @DisplayName("팀 생성에 성공한다.")
    @Test
    void createTeam_Success() {
        // given
        CreateTeamRequestDto requestDto = TeamFixture.TEST_TEAM.createTeamRequestDto();

        when(memberRepository.findById(anyLong())).thenReturn(
            Optional.ofNullable(member));
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(teamPreferenceMeetingTypeRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        // when
        teamService.createTeam(1L, requestDto, preferenceMeetingTypeCode);

        // then
        verify(memberRepository).findById(anyLong());
        verify(teamRepository).save(any(Team.class));
        verify(teamPreferenceMeetingTypeRepository).saveAll(anyList());

        assertEquals(Role.MANAGER, member.getRole());
    }

    @DisplayName("회원이 존재하지 않는 경우 팀 생성을 요청하면 MemberNotFoundException이 발생한다.")
    @Test
    void createTeam_NotFoundMember() {
        // given
        CreateTeamRequestDto requestDto = TeamFixture.TEST_TEAM.createTeamRequestDto();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(MemberNotFoundException.class, () -> {
            teamService.createTeam(1L, requestDto, preferenceMeetingTypeCode);
        });

        verify(memberRepository).findById(anyLong());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @DisplayName("이미 소속된 팀이 있는 경우 팀 생성을 요청하면 TeamAlreadyExistsException이 발생한다.")
    @Test
    void createTeam_TeamAlreadyExists() {
        // given
        CreateTeamRequestDto requestDto = TeamFixture.TEST_TEAM.createTeamRequestDto();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.ofNullable(member));

        member.setTeam(team);

        // when & then
        assertThrows(TeamAlreadyExistsException.class, () -> {
            teamService.createTeam(1L, requestDto, preferenceMeetingTypeCode);
        });

        verify(memberRepository).findById(anyLong());
        verify(teamRepository, never()).save(any(Team.class));

        // after
        member.setTeam(null);
    }

    @DisplayName("대학 미인증 사용자인 경우 팀 생성을 요청하면 UnAuthorizedUnivException이 발생한다.")
    @Test
    void createTeam_UnAuthorizedUniv() {
        // given
        CreateTeamRequestDto requestDto = TeamFixture.TEST_TEAM.createTeamRequestDto();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.ofNullable(member));

        member.getCollegeInfo().saveMail(null);

        // when & then
        assertThrows(UnAuthorizedUnivException.class, () -> {
            teamService.createTeam(1L, requestDto, preferenceMeetingTypeCode);
        });

        verify(memberRepository).findById(anyLong());
        verify(teamRepository, never()).save(any(Team.class));

        // after
        member.getCollegeInfo().saveMail("test@test.ac.kr");
    }

    @DisplayName("팀 수정에 성공한다.")
    @Test
    void modifyTeam_Success() {
        // given
        ModifyTeamRequestDto requestDto = TeamFixture.TEST_TEAM.modifyTeamRequestDto();
        member.setTeam(team);

        when(memberRepository.findById(anyLong())).thenReturn(
            Optional.ofNullable(member));
        when(teamPreferenceMeetingTypeRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        // when
        teamService.modifyTeam(1L, requestDto, preferenceMeetingTypeCode);

        // then
        verify(memberRepository).findById(anyLong());
        verify(teamPreferenceMeetingTypeRepository).saveAll(anyList());

        assertEquals(requestDto.region(), team.getRegion());
        assertEquals(requestDto.drinkingOption(), team.getDrinkingOption());
        assertEquals(requestDto.additionalActivity(), team.getAdditionalActivity().toString());
        assertEquals(requestDto.introduction(), team.getIntroduction());

        // after
        member.setTeam(null);
    }

    @DisplayName("회원이 존재하지 않는 경우 팀 정보 수정을 요청하면 MemberNotFoundException이 발생한다.")
    @Test
    void modifyTeam_NotFoundMember() {
        // given
        ModifyTeamRequestDto requestDto = TeamFixture.TEST_TEAM.modifyTeamRequestDto();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(MemberNotFoundException.class, () -> {
            teamService.modifyTeam(1L, requestDto, preferenceMeetingTypeCode);
        });

        verify(memberRepository).findById(anyLong());
        verify(teamPreferenceMeetingTypeRepository, never()).saveAll(anyList());
    }

    @DisplayName("소속 팀이 있는 경우 마이 팀 조회에 성공한다.")
    @Test
    void getMyTeamWithExistTeam_Success() {
        // given
        member.setTeam(team);

        when(memberRepository.findById(anyLong())).thenReturn(
            Optional.ofNullable(member));
        when(teamPreferenceMeetingTypeRepository.findByTeamTeamId(anyLong())).thenReturn(
            new ArrayList<>());

        // when
        MyTeamDetailResponseDto result = teamService.getMyTeamDetail(1L);

        // then
        assertEquals(result.memberCount(), team.getMemberCount());
        assertEquals(result.region(), team.getRegion());
        assertEquals(result.drinkingOption(), team.getDrinkingOption());
        assertEquals(result.additionalActivity(), team.getAdditionalActivity());
        assertEquals(result.introduction(), team.getIntroduction());
        assertEquals(result.managerImageAuth(), team.getMember().isImageAuth());

        // after
        member.setTeam(null);
    }

    @DisplayName("소속 팀이 없는 경우 마이 팀을 조회하면 null이 반환된다.")
    @Test
    void getMyTeamWithNotExistTeam_Success() {
        // given
        member.setTeam(null);
        when(memberRepository.findById(anyLong())).thenReturn(
            Optional.ofNullable(member));

        // when
        MyTeamDetailResponseDto teamDetailResponseDto = teamService.getMyTeamDetail(1L);

        // then
        verify(memberRepository).findById(anyLong());
        verify(teamPreferenceMeetingTypeRepository, never()).findByTeamTeamId(anyLong());
        assertNull(member.getTeam());
        assertNull(teamDetailResponseDto);
    }

    @DisplayName("회원이 존재하지 않는 경우 팀 정보 수정을 요청하면 MemberNotFoundException이 발생한다.")
    @Test
    void getMyTeam_NotFoundMember() {
        // given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(MemberNotFoundException.class, () -> {
            teamService.getMyTeamDetail(1L);
        });

        verify(memberRepository).findById(anyLong());
        verify(teamPreferenceMeetingTypeRepository, never()).findByTeamTeamId(anyLong());
    }
}