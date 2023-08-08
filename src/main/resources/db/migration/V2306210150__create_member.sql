CREATE TABLE IF NOT EXISTS `member`
(
    `member_id`             bigint            NOT NULL AUTO_INCREMENT,
    `nickname`              varchar(10)       NOT NULL,
    `gender`                char(1)           NOT NULL,
    `phone_number`          char(24)          NOT NULL,
    `college_code_id`       char(3)           NOT NULL,
    `college_group_code_id` char(2)           NOT NULL,
    `college_type`          tinyint           NOT NULL,
    `admission_year`        char(2)           NOT NULL,
    `email`                 varchar(70),
    `mbti`                  tinyint           NOT NULL,
    `credit`                smallint UNSIGNED NOT NULL,
    `image_auth`            tinyint DEFAULT 0,
    `basic_url`             varchar(60),
    `low_url`               varchar(60),
    `created_at`            datetime(6),
    `modified_at`           datetime(6),
    `deleted_at`            datetime(6),
    `role`                  varchar(8),
    PRIMARY KEY (`member_id`),
    UNIQUE KEY `member_email` (`email`),
    UNIQUE KEY `member_phone_number` (`phone_number`)
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4;
