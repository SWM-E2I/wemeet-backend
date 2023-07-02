package com.e2i.wemeet.service.memberpreferencemeetingtype;

import com.e2i.wemeet.domain.memberpreferencemeetingtype.MemberPreferenceMeetingType;
import com.e2i.wemeet.domain.memberpreferencemeetingtype.MemberPreferenceMeetingTypeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberPreferenceMeetingTypeServiceImpl implements MemberPreferenceMeetingTypeService {

    private final MemberPreferenceMeetingTypeRepository memberPreferenceMeetingTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MemberPreferenceMeetingType> findMemberPreferenceMeetingType(Long memberId) {
        return memberPreferenceMeetingTypeRepository.findByMemberMemberId(memberId);
    }
}
