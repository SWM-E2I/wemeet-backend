package com.e2i.wemeet.domain.teampreferencemeetingtype;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface TeamPreferenceMeetingTypeRepository extends
    JpaRepository<TeamPreferenceMeetingType, Long> {

    @Modifying(clearAutomatically = true)
    void deleteAllByTeamTeamId(Long teamId);

    List<TeamPreferenceMeetingType> findByTeamTeamId(Long teamId);
    
}
