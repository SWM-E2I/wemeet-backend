CREATE TABLE IF NOT EXISTS `team_member`
(
    `team_member_id`        bigint  NOT NULL AUTO_INCREMENT,
    `college_code_id`       char(3) NOT NULL,
    `college_group_code_id` char(2) NOT NULL,
    `college_type`          tinyint NOT NULL,
    `admission_year`        char(2) NOT NULL,
    `mbti`                  tinyint NOT NULL,
    `created_at`            datetime(6),
    `modified_at`           datetime(6),
    `team_id`               bigint  NOT NULL,
    PRIMARY KEY (`team_member_id`),
    FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`)
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4;
