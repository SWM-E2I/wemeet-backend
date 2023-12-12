package com.e2i.wemeet.service.member;

import java.util.List;

public interface BlockService {

    // 차단하기
    Long block(Long memberId, Long blockMemberId);

    // 차단목록 조회
    List<Long> readBlockList(Long memberId);
}
