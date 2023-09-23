package com.e2i.wemeet.domain.team_member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    /*
     * 팀원 전체 삭제
     */
    void deleteAllByTeamTeamId(Long teamId);

}
