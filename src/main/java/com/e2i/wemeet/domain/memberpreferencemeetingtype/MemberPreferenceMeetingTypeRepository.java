package com.e2i.wemeet.domain.memberpreferencemeetingtype;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface MemberPreferenceMeetingTypeRepository extends
    JpaRepository<MemberPreferenceMeetingType, Long> {

    List<MemberPreferenceMeetingType> findByMemberMemberId(Long memberId);

    @Modifying(clearAutomatically = true)
    void deleteAllByMemberMemberId(Long memberId);

}
