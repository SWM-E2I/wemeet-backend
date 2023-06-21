CREATE TABLE IF NOT EXISTS `member_interest` (
   `member_interest_id` bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6),
    `modified_at` datetime(6),
    `member_id` bigint NOT NULL,
    `code_id` char(4) NOT NULL,
    `group_code_id` char(4) NOT NULL,
    PRIMARY KEY (`member_interest_id`),
    FOREIGN KEY (`code_id`, `group_code_id`) REFERENCES code (`code_id`, `group_code_id`) ON DELETE CASCADE,
    FOREIGN KEY (`member_id`) REFERENCES member (`member_id`) ON DELETE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET=utf8mb4;
