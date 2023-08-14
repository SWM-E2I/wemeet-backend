package com.e2i.wemeet.domain.meeting;

import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MeetingListRepositoryImpl implements MeetingListRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SentMeetingResponseDto> getSentRequestList(final Long memberId) {
        return null;
    }

    @Override
    public List<ReceivedMeetingResponseDto> getReceiveRequestList(final Long memberId) {
        return null;
    }
}
