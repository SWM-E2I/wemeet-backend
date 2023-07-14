CREATE TABLE IF NOT EXISTS `team_invitation` (
  `team_invitation_id` bigint NOT NULL AUTO_INCREMENT,
    `accept_status` varchar(7) NOT NULL DEFAULT 'WAITING',
    `created_at` datetime(6),
    `modified_at` datetime(6),
    `member_id` bigint NOT NULL,
    `team_id` bigint NOT NULL,
    PRIMARY KEY (`team_invitation_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`) ON DELETE CASCADE,
    FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`) ON DELETE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET=utf8mb4;
