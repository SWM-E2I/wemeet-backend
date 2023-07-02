package com.e2i.wemeet.service.memberpreferencemeetingtype;

import com.e2i.wemeet.domain.memberpreferencemeetingtype.MemberPreferenceMeetingType;
import java.util.List;

public interface MemberPreferenceMeetingTypeService {

    /*
     * memberId를 통한 선호하는 만남 특징 검색
     */

    List<MemberPreferenceMeetingType> findMemberPreferenceMeetingType(Long memberId);
}
