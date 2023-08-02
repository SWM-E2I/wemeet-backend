CREATE TABLE IF NOT EXISTS `team`
(
    `team_id`             bigint       NOT NULL AUTO_INCREMENT,
    `member_num`          tinyint      NOT NULL,
    `gender`              char(1)      NOT NULL,
    `region`              tinyint      NOT NULL,
    `drink_rate`          tinyint      NOT NULL,
    `drink_with_game`     tinyint      NOT NULL,
    `additional_activity` tinyint,
    `introduction`        varchar(150) NOT NULL,
    `created_at`          datetime(6),
    `modified_at`         datetime(6),
    `deleted_at`          datetime(6),
    `team_leader_id`      bigint,
    PRIMARY KEY (`team_id`),
    FOREIGN KEY (`team_leader_id`) REFERENCES member (`member_id`) ON DELETE SET NULL
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4;
