--DELIMITER "!"
CREATE TABLE `MAIL_MESSAGES` (
  `MAIL_MESSAGE_ID` bigint(20) NOT NULL primary key AUTO_INCREMENT,
  `TYPE` varchar(10) character set latin1 collate latin1_bin NOT NULL default 'NORMAL',
  `STATE` varchar(10) character set latin1 collate latin1_bin NOT NULL,
  `FROM` varchar(256) character set latin1 collate latin1_bin NOT NULL,
  `TO` varchar(256) character set latin1 collate latin1_bin NOT NULL,
  `SUBJECT` varchar(256) character set latin1 collate latin1_bin NOT NULL,
  `BODY` mediumtext default NULL,
  `HTML_BODY` mediumtext default NULL,
  `CREATION_TIME` datetime default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1!
