CREATE TABLE IF NOT EXISTS `history`
(
    `history_id`        bigint  NOT NULL AUTO_INCREMENT,
    `is_like`        tinyint DEFAULT 0 NOT NULL,
    `created_at`            datetime(6),
    `member_id`               bigint  NOT NULL,
    `team_id`               bigint  NOT NULL,
    PRIMARY KEY (`history_id`),
    FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
    )
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4;
