dbpatcher - User's guide
========================

Database definition
-------------------
A database is defined by a structure of directories (one per incremental step) containing SQL scripts as in the following example:


* 001-first_version
  * script.sql
* 002-second_version
  * script1.sql
  * script2.sql
  
Directory names must be parseable to an integer value. Steps are applied (e.g. the scripts inside the various directories are executed) following the order given by their names, for example: 1, 2, 3 or 01, 02, 03. Starting from version 0.9.1 also descriptive texts can be added to the directory names using this format <version number>-<text> (for example: 001-create, 002-alter_users, 003-insert_data).

Scripts are valid MySQL ones, for example:
```
-- Table MYTABLE, just for test
create table MYTABLE (
  ID bigint(20) not null primary key,
  NAME varchar(10) character set latin1 collate latin1_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
```
Commands are delimited by a semicolon (;) character. In case a different delimiter is needed (for example when defining triggers) a new one can be specified using a special comment:
```
--DELIMITER "newdelimiter"
```
As in the following example:
```
--DELIMITER "//"
-- Table MYTABLE, just for test
create table MYTABLE (
  ID bigint(20) not null primary key,
  NAME varchar(10) character set latin1 collate latin1_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1//
```
Scripts are run in case-sensitive alphabetical order.