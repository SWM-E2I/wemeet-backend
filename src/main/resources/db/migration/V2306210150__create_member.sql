CREATE TABLE IF NOT EXISTS `member`
(
    `member_id`                       bigint      NOT NULL AUTO_INCREMENT,
    `member_code`                     char(4)     NOT NULL,
    `nickname`                        varchar(20) NOT NULL,
    `gender`                          varchar(6)  NOT NULL,
    `college`                         varchar(30) NOT NULL,
    `college_type`                    varchar(20) NOT NULL,
    `admission_year`                  char(2)     NOT NULL,
    `mail`                            varchar(60),
    `phone_number`                    char(60)    NOT NULL,
    `mbti`                            varchar(7)  NOT NULL,
    `introduction`                    varchar(100),
    `start_preference_admission_year` char(2),
    `end_preference_admission_year`   char(2),
    `credit`                          int         NOT NULL,
    `same_college_state`              char(2),
    `drinking_option`                 char(2),
    `is_avoided_friends`              tinyint,
    `preference_mbti`                 char(4),
    `image_auth`                      tinyint DEFAULT 0,
    `created_at`                      datetime(6),
    `modified_at`                     datetime(6),
    `role`                            varchar(8),
    PRIMARY KEY (`member_id`),
    UNIQUE KEY `member_mail` (`mail`),
    UNIQUE KEY `member_phone_number` (`phone_number`)
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4;

ALTER TABLE `member`
    ADD `registration_type` varchar(15) NOT NULL AFTER `credit`;
