dbpatcher
=========
**dbpatcher** is a tool for applying incremental updates to a [MySQL](https://www.mysql.com/) or [MariaDB](https://mariadb.org/) database.

It has originally developed for the continuous deployment procedure of a site developed by a startup the tool's author was involved with. It was used for the automated nightly deployment to production procedure.

In agreement with the mentioned startup the code has been forked and made open source. It is currently maintained and developed by its original author. 

**dbpatcher** is released under the terms of [GNU](http://www.gnu.org/) [General Public License](http://www.gnu.org/copyleft/gpl.html).

Informations on the usage of the tool are available in the [user's guide](userguide.md).


Why dbpatcher?
==============
Some years ago in my company we had a well established continuous integration and continuous deployment procedure (most likely the first of its kind in Finland): at every build our automated test suite (about 10% unit tests, the rest functional tests testing both the successful and the error scenarios) was executed and, if successful, the artifacts were deployed to production the next night a 4AM (when users were not using our site).

This worked really well for the application side, but had the big flaw that (backwards incompatible) database changes had to be applied manually to the production database (which meant that we had to stop the application and interrupt the service for our users, deploy the database changes and the application manually and the start up the service again).

That's why we decided to spend some time developing **dbpatcher**. It fulfills the following use cases:
* Ability to create a new empty database from scratch
* Ability to apply incremental updates to an existing database
* Ability to roll back the updates in case of errors applying them
The last use case was there as a safe-guard in order not to leave the service unusable for our users during the automated overnight deployments. dbpatcher has been used in production for years without having any problems, until our service has been shut down.
