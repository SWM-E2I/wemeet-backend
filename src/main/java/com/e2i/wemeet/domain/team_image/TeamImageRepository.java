package com.e2i.wemeet.domain.team_image;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamImageRepository extends JpaRepository<TeamImage, Long> {

    /*
     * 팀 이미지 전체 삭제
     */
    void deleteAllByTeamTeamId(Long teamId);
}
