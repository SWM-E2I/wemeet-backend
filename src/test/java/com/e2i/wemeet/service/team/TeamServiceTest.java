package com.e2i.wemeet.service.team;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.exception.badrequest.NotBelongToTeamException;
import com.e2i.wemeet.exception.badrequest.TeamAlreadyExistsException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedUnivException;
import com.e2i.wemeet.security.token.TokenInjector;
import com.e2i.wemeet.support.fixture.MemberFixture;
import com.e2i.wemeet.support.fixture.TeamFixture;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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
    private TokenInjector tokenInjector;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private TeamServiceImpl teamService;

    private Member member;
    private Member manager;
    private Member inviteMember;
    private Team team;
    private List<Code> preferenceMeetingTypeCode = new ArrayList<>();

    private Long managerId;
    private Long memberId;

    @BeforeEach
    void setUp() {
        member = MemberFixture.KAI.create();
        manager = MemberFixture.SEYUN.create();
        inviteMember = MemberFixture.JEONGYEOL.create();
        team = TeamFixture.TEST_TEAM.create_with_id(MemberFixture.KAI.create(), 1L);
        preferenceMeetingTypeCode = new ArrayList<>();
        managerId = manager.getMemberId();
        memberId = member.getMemberId();
    }

    @DisplayName("팀 생성에 성공한다.")
    @Test
    void createTeam_Success() {
        // given

    }

    @DisplayName("회원이 존재하지 않는 경우 팀 생성을 요청하면 MemberNotFoundException이 발생한다.")
    @Test
    void createTeam_NotFoundMember() {
        // given
        CreateTeamRequestDto requestDto = TeamFixture.TEST_TEAM.createTeamRequestDto();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
            () -> teamService.createTeam(1L, requestDto, preferenceMeetingTypeCode,
                response)).isInstanceOf(MemberNotFoundException.class);

        verify(memberRepository).findById(anyLong());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @DisplayName("이미 소속된 팀이 있는 경우 팀 생성을 요청하면 TeamAlreadyExistsException이 발생한다.")
    @Test
    void createTeam_TeamAlreadyExists() {
        // given
        CreateTeamRequestDto requestDto = TeamFixture.TEST_TEAM.createTeamRequestDto();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.ofNullable(member));
        // when & then
        assertThatThrownBy(
            () -> teamService.createTeam(1L, requestDto, preferenceMeetingTypeCode, response))
            .isInstanceOf(TeamAlreadyExistsException.class);

        verify(memberRepository).findById(anyLong());
        verify(teamRepository, never()).save(any(Team.class));

        // after
    }

    @DisplayName("대학 미인증 사용자인 경우 팀 생성을 요청하면 UnAuthorizedUnivException이 발생한다.")
    @Test
    void createTeam_UnAuthorizedUniv() {
        // given
        CreateTeamRequestDto requestDto = TeamFixture.TEST_TEAM.createTeamRequestDto();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.ofNullable(member));

        member.getCollegeInfo().saveMail(null);

        // when & then
        assertThatThrownBy(
            () -> teamService.createTeam(1L, requestDto, preferenceMeetingTypeCode, response))
            .isInstanceOf(UnAuthorizedUnivException.class);

        verify(memberRepository).findById(anyLong());
        verify(teamRepository, never()).save(any(Team.class));

        // after
        member.getCollegeInfo().saveMail("test@test.ac.kr");
    }

    @DisplayName("팀 수정에 성공한다.")
    @Test
    void modifyTeam_Success() {
        // given
    }

    @DisplayName("회원이 존재하지 않는 경우 팀 정보 수정을 요청하면 MemberNotFoundException이 발생한다.")
    @Test
    void modifyTeam_NotFoundMember() {
    }

    @DisplayName("소속 팀이 있는 경우 마이 팀 조회에 성공한다.")
    @Test
    void getMyTeamWithExistTeam_Success() {
        // given
    }

    @DisplayName("소속 팀이 없는 경우 마이 팀을 조회하면 null이 반환된다.")
    @Test
    void getMyTeamWithNotExistTeam_Success() {
        // given
    }

    @DisplayName("회원이 존재하지 않는 경우 팀 정보 수정을 요청하면 MemberNotFoundException이 발생한다.")
    @Test
    void getMyTeam_NotFoundMember() {
        // given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
            () -> teamService.getMyTeamDetail(1L))
            .isInstanceOf(MemberNotFoundException.class);

        verify(memberRepository).findById(anyLong());
    }


    @DisplayName("팀원 조회에 성공한다.")
    @Test
    void getTeamMemberList_Success() {

    }

    @DisplayName("팀이 없는 경우 팀원 조회를 하면 NotBelongToTeamException이 발생한다.")
    @Test
    void getTeamMemberList_NotBelongToTeamException() {
        // given

    }

    @DisplayName("팀 삭제에 성공한다.")
    @Test
    void deleteTeam_Success() {
        // given

        // when
        teamService.deleteTeam(managerId);

        // then
        verify(memberRepository).findById(managerId);
        assertThat(manager.getRole()).isEqualTo(Role.USER);

        // after
        manager.setRole(Role.MANAGER);
    }

    @DisplayName("팀을 삭제하면 팀원들의 팀 정보가 null 로 초기화되며 deleteAt에 삭제 시간이 기록된다.")
    @Test
    void deleteTeam_mark() {
        // given
    }

    @DisplayName("팀이 없는 경우 팀 삭제를 요청하면 NotBelongToTeamException이 발생한다.")
    @Test
    void deleteTeam_NotBelongToTeamException() {
        // given
        when(memberRepository.findById(managerId)).thenReturn(
            Optional.of(manager));

        // when & then
        assertThatThrownBy(
            () -> teamService.deleteTeam(managerId))
            .isInstanceOf(NotBelongToTeamException.class);

        verify(memberRepository).findById(managerId);
        verify(teamRepository, never()).delete(any(Team.class));
    }

}
