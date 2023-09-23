package com.e2i.wemeet.service.member;

public interface RecommendService {

    // 추천인 입력 (추천인 전화번호)
    // @return 추천인의 memberId
    public void recommend(final Long memberId, final String recommenderPhone);

}
