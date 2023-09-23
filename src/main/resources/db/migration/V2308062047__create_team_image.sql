CREATE TABLE IF NOT EXISTS `team_image`
(
    `team_image_id`        bigint  NOT NULL AUTO_INCREMENT,
    `team_image_url`       varchar(150) NOT NULL,
    `sequence`                 tinyint NOT NULL,
    `created_at`            datetime(6),
    `team_id`               bigint  NOT NULL,
    PRIMARY KEY (`team_image_id`),
    FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`) ON DELETE CASCADE
    )
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4;
