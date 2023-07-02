package com.e2i.wemeet.domain.memberpreferencemeetingtype;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPreferenceMeetingTypeRepository extends
    JpaRepository<MemberPreferenceMeetingType, Long> {

    List<MemberPreferenceMeetingType> findByMemberMemberId(Long memberId);

}
