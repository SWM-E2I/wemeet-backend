CREATE TABLE IF NOT EXISTS `group_code` (
    `group_code_id` char(2) NOT NULL,
    `name` varchar(30) NOT NULL,
    `description` varchar(100) NOT NULL,
    `created_at` datetime(6),
    `modified_at` datetime(6),
    PRIMARY KEY (`group_code_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET=utf8mb4;

CREATE TABLE IF NOT EXISTS `code` (
    `code_id` char(3) NOT NULL,
    `group_code_id` char(2) NOT NULL,
    `value` varchar(255) NOT NULL,
    `created_at` datetime(6),
    `modified_at` datetime(6),
    PRIMARY KEY (`code_id`, `group_code_id`),
    FOREIGN KEY (`group_code_id`) REFERENCES group_code (`group_code_id`) ON DELETE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET=utf8mb4;
