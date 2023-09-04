package com.e2i.wemeet.dto.dsl;

import com.e2i.wemeet.exception.badrequest.TeamHasBeenDeletedException;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamCheckProxyDto {

    private Long teamId;
    private LocalDateTime deletedAt;

    @QueryProjection
    public TeamCheckProxyDto(Long teamId, LocalDateTime deletedAt) {
        this.teamId = teamId;
        this.deletedAt = deletedAt;
    }

    public TeamCheckProxyDto checkValid() {
        if (deletedAt != null) {
            throw new TeamHasBeenDeletedException();
        }
        return this;
    }

}
