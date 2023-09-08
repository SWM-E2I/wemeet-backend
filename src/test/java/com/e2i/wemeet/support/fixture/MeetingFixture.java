package com.e2i.wemeet.support.fixture;

import com.e2i.wemeet.domain.meeting.Meeting;
import com.e2i.wemeet.domain.team.Team;

public enum MeetingFixture {

    BASIC_MEETING("https://open.kakao.com/o/1sdfasdf"),
    SECOND_MEETING("https://open.kakao.com/o/second22"),
    THIRD_MEETING("https://open.kakao.com/o/third333");

    private final String chatUrl;

    MeetingFixture(final String chatUrl) {
        this.chatUrl = chatUrl;
    }

    public Meeting create(final Team team, final Team partnerTeam) {
        return createBuilder(team, partnerTeam)
            .build();
    }

    public Meeting.MeetingBuilder createBuilder(final Team team, final Team partnerTeam) {
        return Meeting.builder()
            .team(team)
            .partnerTeam(partnerTeam)
            .chatLink(this.chatUrl);
    }

    public String getChatUrl() {
        return chatUrl;
    }
}
