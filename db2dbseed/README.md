This project implements the seed and sync feature. Following are the main building blocks:

1) Model database SQL
	SQL scripts to create and populate the example application database
	
2) Sync framework database SQL
	SQL scripts to create and populate sync peer database.

3) Spring Service configuration
	Using Spring for bean initialization

4) JUnit for testing
	Application will use JUnit to test the necessary use cases

5) Liquibase for database creation
	Use of liquibase to dynamically create HSQLDB table structure on each test execution
	
6) Use JDBC
	ORM causes issues while accessing CLOBs

7) Classes:-

Sync

