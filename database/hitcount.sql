CREATE TABLE `jj`.`hitcount` (
  `resource` VARCHAR(255)                          NOT NULL,
  `count`    INT                                   NOT NULL DEFAULT '0',
  `updated`  TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`resource`)
)
  ENGINE = INNODB
  CHARACTER SET utf8
  COLLATE utf8_unicode_ci