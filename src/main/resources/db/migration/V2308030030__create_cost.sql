CREATE TABLE IF NOT EXISTS `cost`
(
    `cost_id` bigint      NOT NULL AUTO_INCREMENT,
    `type`    varchar(30) NOT NULL,
    `value`   tinyint     NOT NULL,
    PRIMARY KEY (`cost_id`)
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4;

INSERT INTO `cost` (`cost_id`, `type`, `value`)
values (1, 'PAYMENT_5900', 15),
       (2, 'PAYMENT_9900', 30),
       (3, 'PAYMENT_14900', 50),
       (4, 'PAYMENT_19900', 80),
       (5, 'MEETING_REQUEST', 10),
       (6, 'MEETING_REQUEST_WITH_MESSAGE', 12),
       (7, 'MEETING_ACCEPT', 5),
       (8, 'MORE_TEAM', 3)
;
