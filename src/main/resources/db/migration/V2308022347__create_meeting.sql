CREATE TABLE IF NOT EXISTS `meeting`
(
    `meeting_id`      bigint      NOT NULL AUTO_INCREMENT,
    `team_id`         bigint      NOT NULL,
    `partner_team_id` bigint      NOT NULL,
    `created_at`      datetime(6) NOT NULL,
    `modified_at`     datetime(6),
    `chat_link`       varchar(50),
    `is_over`         datetime(6),
    PRIMARY KEY (`meeting_id`),
    FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`) ON DELETE CASCADE,
    FOREIGN KEY (`partner_team_id`) REFERENCES `team` (`team_id`) ON DELETE CASCADE
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4;
