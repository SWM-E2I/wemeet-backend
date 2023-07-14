CREATE TABLE IF NOT EXISTS `team_preference_meeting_type` (
    `team_preference_meeting_type_id` bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6),
    `modified_at` datetime(6),
    `team_id` bigint NOT NULL,
    `code_id` char(4) NOT NULL,
    `group_code_id` char(4) NOT NULL,
    PRIMARY KEY (`team_preference_meeting_type_id`),
    FOREIGN KEY (`code_id`, `group_code_id`) REFERENCES code (`code_id`, `group_code_id`) ON DELETE CASCADE,
    FOREIGN KEY (`team_id`) REFERENCES team (`team_id`) ON DELETE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET=utf8mb4;
