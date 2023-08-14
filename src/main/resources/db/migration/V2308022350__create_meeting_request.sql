CREATE TABLE IF NOT EXISTS `meeting_request`
(
    `meeting_request_id` bigint      NOT NULL AUTO_INCREMENT,
    `team_id`            bigint      NOT NULL,
    `partner_team_id`    bigint      NOT NULL,
    `accept_status`      tinyint     NOT NULL DEFAULT 0,
    `created_at`         datetime(6) NOT NULL,
    `modified_at`        datetime(6),
    `message`            varchar(50),
    PRIMARY KEY (`meeting_request_id`),
    FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`) ON DELETE CASCADE,
    FOREIGN KEY (`partner_team_id`) REFERENCES `team` (`team_id`) ON DELETE CASCADE
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4;
