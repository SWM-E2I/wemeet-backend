CREATE TABLE IF NOT EXISTS `team` (
    `team_id` bigint NOT NULL AUTO_INCREMENT,
    `team_code` char(6) NOT NULL,
    `member_count` int NOT NULL,
    `gender` char(6) NOT NULL,
    `region` varchar(20) NOT NULL,
    `drinking_option` char(2) NOT NULL,
    `additional_activity` varchar(20),
    `introduction` varchar(100),
    `created_at` datetime(6),
    `modified_at` datetime(6),
    `member_id` bigint NOT NULL,
    PRIMARY KEY (`team_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`) ON DELETE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET=utf8mb4;

ALTER TABLE `member` ADD `team_id` bigint;
ALTER TABLE `member` ADD FOREIGN KEY (`team_id`) REFERENCES team (`team_id`);

