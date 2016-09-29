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

Running from command line
-------------------------
**dbpatcher** can be executed from command line by either using the standalone jar:
```
java -jar dbpatcher-standalone.jar
```
or by using the regular jar and all the dependencies:
```
java -cp dbpatcher.jar:dependency.jar:...:dependency.jar net.robyf.dbpatcher.Launcher
```
In both cases the same set of parameters must be provided as per the following usage informations: 
```
usage: java -jar dbpatcher.jar -u username -p password -d database_name
            [options] schema_root
Available options:
 -c,--character-set <arg>   Character set (default value: ISO-8859-1)
 -d,--databaseName <arg>    Database name
 -p,--password <arg>        Database password
 -r,--rollback-if-error     Rolls back the entire operation in case of
                            errors
 -s,--simulation            Simulate the operation without touching the
                            current database
 -u,--username <arg>        Database username
 -v,--to-version <arg>      Target version number
```
The database must be residing on localhost (network is not supported) and the user used must have full control over it (and also permissions to create and drop new databases).
`schema_root` is the full path to a database definition. It can be either a directory in the file system or a zip file.
The process' exit code indicates if the operation was successful or not (0 indicates success, every other value failure).

Note that the tool tracks what increments have been applied in a database table, called `DATABASE_VERSION`. **dbpatcher** itself creates it and it must not be altered manually in any way.

### Simulation mode
In order to be sure, for example in an automated deployment to production procedure, that the SQL scripts work properly and don't break the database `simulation mode` has been introduced. When **dbpatcher** is run in this mode it first takes a backup of the target database, restore it into a newly created temporary one, tries to apply the increments and then drop the newly create temporary db. By doing this the procedure can be rehearsed without risking to leave the database in an unusable state.
An example procedure for an automated deployment to production is:
1. Run **dbpatcher** in simulation mode against the production database, interrupt the operation in case of error
2. Stop the application
3. Run **dbpatcher** against the production database
4. Deploy the new version of the application
5. Start up the application

Running from ant
----------------
**dbpatcher** can be executed as an ant task if both `dbpatcher.jar` and all its dependencies are in the ant's classpath. Dependencies must be in the classpath also when using the standalone jar.
In order to use the task in a buildfile the following `taskdef` declaration must be provided:
```
<taskdef name="dbpatcher" resource="net/robyf/dbpatcher/dbpatcher.properties" classpath="path.to.dbpatcher.jar" />
```

### Parameters
All parameters are required unless specified.

Attribute | Description 
--- | ---
username | Database user's username
password | Database user's password
database | Database name
schemaRoot | Path to a database definition. It can be either a directory or a zip file
version | Target version number (optional, by default all the increments are applied)
rollbackIfError | Rolls back the entire operation in case of errors (optional, default = false)
simulationMode | Simulate the operation without touching the current database (optional, default = false)
charset | Character set used for reading the scripts (optional, default = ISO-8859-1)

Running from gradle
-------------------
**dbpatcher** can be executed as gradle task using the [gradle dbpatcher plugin](https://github.com/robyf/gradle-dbpatcher-plugin).