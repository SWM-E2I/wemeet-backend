package com.e2i.wemeet.support.fixture;

import com.e2i.wemeet.domain.meeting.Meeting;
import com.e2i.wemeet.domain.team.Team;

public enum MeetingFixture {

    BASIC_MEETING,
    SECOND_MEETING,
    THIRD_MEETING;

    public Meeting create(final Team team, final Team partnerTeam) {
        return createBuilder(team, partnerTeam)
            .build();
    }

    public Meeting.MeetingBuilder createBuilder(final Team team, final Team partnerTeam) {
        return Meeting.builder()
            .team(team)
            .partnerTeam(partnerTeam);
    }

}
