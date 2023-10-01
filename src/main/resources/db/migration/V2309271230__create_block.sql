CREATE TABLE IF NOT EXISTS `block`
(
    `block_id`     bigint NOT NULL AUTO_INCREMENT,
    `member`       bigint NOT NULL,
    `block_member` bigint NOT NULL,
    `created_at`   datetime(6),
    PRIMARY KEY (`block_id`),
    FOREIGN KEY (`member`) REFERENCES member (`member_id`) ON DELETE CASCADE,
    FOREIGN KEY (`block_member`) REFERENCES member (`member_id`) ON DELETE CASCADE
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4;
