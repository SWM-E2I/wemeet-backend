package com.e2i.wemeet.support.fixture;

import com.e2i.wemeet.domain.meeting.MeetingRequest;
import com.e2i.wemeet.domain.team.Team;

public enum MeetingRequestFixture {
    BASIC_REQUEST("재미있게 놀아요!");

    private final String message;

    MeetingRequestFixture(final String message) {
        this.message = message;
    }

    public MeetingRequest create(Team team, Team partnerTeam) {
        return createBuilder(team, partnerTeam)
            .build();
    }

    private MeetingRequest.MeetingRequestBuilder createBuilder(Team team, Team partnerTeam) {
        return MeetingRequest.builder()
            .team(team)
            .partnerTeam(partnerTeam)
            .message(this.message);
    }

    public String getMessage() {
        return message;
    }
}
