CREATE TABLE IF NOT EXISTS `member` (
    `member_id` bigint NOT NULL AUTO_INCREMENT,
    `member_code` char(4) NOT NULL,
    `nickname` varchar(20) NOT NULL,
    `gender` varchar(6) NOT NULL,
    `college` varchar(30) NOT NULL,
    `college_type` varchar(20) NOT NULL,
    `admission_year` int NOT NULL,
    `mail`  varchar(50),
    `phone_number` char(13) NOT NULL,
    `mbti` varchar(7) NOT NULL,
    `introduction` varchar(100),
    `start_preference_admission_year` int NOT NULL,
    `end_preference_admission_year` int NOT NULL,
    `credit` int NOT NULL,
    `same_college_state` tinyint NOT NULL,
    `drinking_option` tinyint NOT NULL,
    `is_avoided_friends` tinyint NOT NULL,
    `e_or_i` int NOT NULL,
    `s_or_n` int NOT NULL,
    `t_or_f` int NOT NULL,
    `j_or_p` int NOT NULL,
    `created_at` datetime(6),
    `modified_at` datetime(6),
    PRIMARY KEY (`member_id`),
    UNIQUE KEY `member_mail` (`mail`),
    UNIQUE KEY `member_phone_number` (`phone_number`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET=utf8mb4;
