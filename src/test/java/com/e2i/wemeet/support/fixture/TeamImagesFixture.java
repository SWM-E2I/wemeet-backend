package com.e2i.wemeet.support.fixture;

import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team_image.TeamImage;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public enum TeamImagesFixture {

    BASIC_TEAM_IMAGE("https://wemeet-bucket.s3.ap-northeast-2.amazonaws.com/teams/1/1.jpg",
        "https://wemeet-bucket.s3.ap-northeast-2.amazonaws.com/teams/1/2.jpg",
        "https://wemeet-bucket.s3.ap-northeast-2.amazonaws.com/teams/1/3.jpg"),
    SECOND_TEAM_IMAGE("https://wemeet-bucket.s3.ap-northeast-2.amazonaws.com/teams/2/Team2-1.jpg",
        "https://wemeet-bucket.s3.ap-northeast-2.amazonaws.com/teams/2/Team2-2.jpg",
        "https://wemeet-bucket.s3.ap-northeast-2.amazonaws.com/teams/2/Team2-3.jpg");

    private final List<String> teamImages;

    TeamImagesFixture(String... teamImages) {
        this.teamImages = List.of(teamImages);
    }

    public List<TeamImage> createTeamImages(final Team team) {
        AtomicInteger atomicInteger = new AtomicInteger(1);

        return teamImages.stream()
            .map(image -> TeamImage.builder()
                .teamImageUrl(image)
                .sequence(atomicInteger.getAndIncrement())
                .team(team)
                .build())
            .toList();
    }

    public List<String> getTeamImages() {
        return teamImages;
    }
}
