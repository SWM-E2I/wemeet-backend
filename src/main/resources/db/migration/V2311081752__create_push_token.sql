CREATE TABLE IF NOT EXISTS `push_token`
(
    `token`             varchar(100)       NOT NULL,
    `member_id`      bigint,
    PRIMARY KEY (`token`),
    FOREIGN KEY (`member_id`) REFERENCES member (`member_id`) ON DELETE CASCADE
    )
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4;
