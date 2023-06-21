CREATE TABLE IF NOT EXISTS `team` (
   `team_id` bigint NOT NULL AUTO_INCREMENT,
   `basic_url` varchar(255) NOT NULL,
    `low_resolution_basic_url` varchar(255) NOT NULL,
    `blur_url` varchar(255) NOT NULL,
    `low_resolution_blur_url` varchar(255) NOT NULL,
    `is_main` tinyint NOT NULL,
    `is_certified` tinyint NOT NULL,
    `created_at` datetime(6),
    `modified_at` datetime(6),
    `member_id` bigint NOT NULL,
    PRIMARY KEY (`team_id`),
    FOREIGN KEY (`member_id`) REFERENCES member (`member_id`) ON DELETE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET=utf8mb4;

ALTER TABLE `member` ADD `team_id` bigint;
ALTER TABLE `member` ADD FOREIGN KEY (`team_id`) REFERENCES team (`team_id`);
