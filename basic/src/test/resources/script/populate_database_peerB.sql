insert into org.Contact (ContactId, DateOfBirth, FirstName, LastName, HeightFt, HeightInch, Picture, PreferredHeight)
values ('151EFA13-A3AD-4C18-A2CE-9D66D0AED112','1988-1-23','Jill','Anderson',5,3,null,'Metric');

insert into org.Contact (ContactId, DateOfBirth, FirstName, LastName, HeightFt, HeightInch, Picture, PreferredHeight)
values ('0934A378-DEDB-4207-B99C-DD0D61DC59BC','1987-5-28','Mindy','Johnson',5,1,null,'Metric');

insert into org.Contact (ContactId, DateOfBirth, FirstName, LastName, HeightFt, HeightInch, Picture, PreferredHeight)
values ('6869F4EC-09BB-4FED-A1A7-B446D2C63BAF','1988-5-28','Jennifer','Smith',5,4,null,'Imperial');

insert into org.Contact (ContactId, DateOfBirth, FirstName, LastName, HeightFt, HeightInch, Picture, PreferredHeight)
values ('42FC5EBC-088D-4DC2-BDBD-D89FA170E5C6','1993-1-23','John','Doe',5,9,null,'Metric');

insert into org.WorkHistory(contactId, endMonth, endYear, jobDescription, jobTitle, organizationId, startMonth, startYear, workHistoryId)
values ('151EFA13-A3AD-4C18-A2CE-9D66D0AED112', '', '', 'Job Description 1', 'Job Title 1', null, '6', '2000', '5155FFA2-DBA3-4A37-B72E-F9AC7065F599');

insert into org.WorkHistory(contactId, endMonth, endYear, jobDescription, jobTitle, organizationId, startMonth, startYear, workHistoryId)
values ('151EFA13-A3AD-4C18-A2CE-9D66D0AED112', '5', '2000', 'Job Description 3', 'Job Title 3', null, '2', '1990', '79C54C95-23E0-4A4D-B85A-652599EAE966');

insert into org.WorkHistory(contactId, endMonth, endYear, jobDescription, jobTitle, organizationId, startMonth, startYear, workHistoryId)
values ('0934A378-DEDB-4207-B99C-DD0D61DC59BC', '', '', 'Job Description 4', 'Job Title 4', null, '6', '1988', 'C63DFAC4-658A-4D05-9D01-1C01EF5B732E');

insert into org.WorkHistory(contactId, endMonth, endYear, jobDescription, jobTitle, organizationId, startMonth, startYear, workHistoryId)
values ('6869F4EC-09BB-4FED-A1A7-B446D2C63BAF', '', '', 'Job Description 5', 'Job Title 5', null, '5', '1989', 'E30344DA-6290-4A46-83FC-CAB6E5119BD4');

insert into org.ContactLink(contactLinkId, sourceContactId, targetContactId, workHistoryId)
values ('211EFA13-D6AD-5D18-B3CE-0E66D0AED223', '151EFA13-A3AD-4C18-A2CE-9D66D0AED112', '0934A378-DEDB-4207-B99C-DD0D61DC59BC', '5155FFA2-DBA3-4A37-B72E-F9AC7065F599');

insert into org.ContactLink(contactLinkId, sourceContactId, targetContactId, workHistoryId)
values ('312EFA13-B4AD-6E18-C4CE-1F66D0AED334', '42FC5EBC-088D-4DC2-BDBD-D89FA170E5C6', '6869F4EC-09BB-4FED-A1A7-B446D2C63BAF', 'E30344DA-6290-4A46-83FC-CAB6E5119BD4');

insert into org.ContactLink(contactLinkId, sourceContactId, targetContactId, workHistoryId)
values ('413EFA13-C5AD-7F18-D5CE-2G66D0AED445', '0934A378-DEDB-4207-B99C-DD0D61DC59BC', '6869F4EC-09BB-4FED-A1A7-B446D2C63BAF', 'C63DFAC4-658A-4D05-9D01-1C01EF5B732E');
