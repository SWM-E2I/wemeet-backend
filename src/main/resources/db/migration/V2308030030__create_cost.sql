CREATE TABLE IF NOT EXISTS `cost`
(
    `cost_id`     bigint      NOT NULL AUTO_INCREMENT,
    `type`        varchar(5)  NOT NULL,
    `value`       tinyint     NOT NULL,
    `create_at`   datetime(6) NOT NULL,
    `modified_at` datetime(6) NOT NULL,
    PRIMARY KEY (`cost_id`)
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4;
