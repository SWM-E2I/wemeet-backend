CREATE TABLE IF NOT EXISTS `cost_history`
(
    `cost_history_id`    bigint     NOT NULL AUTO_INCREMENT,
    `cost_type`          varchar(5)  NOT NULL,
    `cost_value`         tinyint     NOT NULL,
    `member_id`          bigint     NOT NULL,
    `create_at`          datetime(6) NOT NULL,
    `modified_at`        datetime(6) NOT NULL,
    PRIMARY KEY (`cost_history_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4;
