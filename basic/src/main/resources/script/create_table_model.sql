CREATE SCHEMA org AUTHORIZATION DBA;

create table org.Contact (
	ContactId			varchar(36)		not null,
	DateOfBirth			date,
	FirstName			varchar(25)		not null,
	LastName			varchar(25)		not null,
	HeightFt			integer,
	HeightInch			integer,
	Picture				clob,
	PreferredHeight		varchar(25),
	primary key (ContactId)
);

create table org.Organization (
	OrganizationId		varchar(36)		not null,
	Name				varchar(50)		not null,
	primary key (OrganizationId)
);

create table org.WorkHistory (
	WorkHistoryId		varchar(36)		not null,
	ContactId			varchar(36)		not null,
	OrganizationId		varchar(36),
	StartMonth			varchar(2),
	StartYear			varchar(4),
	EndMonth			varchar(2),
	EndYear				varchar(4),
	JobDescription		varchar(200),
	JobTitle			varchar(50),
	primary key (WorkHistoryId),
	FOREIGN KEY (ContactId) REFERENCES org.Contact(ContactId),
	FOREIGN KEY (OrganizationId) REFERENCES org.Organization(OrganizationId)
);

create table org.ContactLink (
	SourceContactId		varchar(36)		not null,
	TargetContactId		varchar(36)		not null,
	WorkHistoryId		varchar(36)		not null,
	primary key (SourceContactId, TargetContactId, WorkHistoryId),
	FOREIGN KEY (SourceContactId) REFERENCES org.Contact(ContactId),
	FOREIGN KEY (TargetContactId) REFERENCES org.Contact(ContactId),
	FOREIGN KEY (WorkHistoryId) REFERENCES org.WorkHistory(WorkHistoryId)
);

