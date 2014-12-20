insert into org.Contact (ContactId, DateOfBirth, FirstName, LastName, HeightFt, HeightInch, Picture, PreferredHeight)
values ('42FC5EBC-088D-4DC2-BDBD-D89FA170E5C6','1992-1-23','John','Doe',5,9,null,'Metric');

insert into org.Contact (ContactId, DateOfBirth, FirstName, LastName, HeightFt, HeightInch, Picture, PreferredHeight)
values ('B6581A36-804D-45AC-B2E2-F6DA265AF7DE','1990-4-29','Jack','Smith',6,1,null,'Metric');

insert into org.WorkHistory(contactId, endMonth, endYear, jobDescription, jobTitle, organizationId, startMonth, startYear, workHistoryId)
values ('42FC5EBC-088D-4DC2-BDBD-D89FA170E5C6', '', '', 'Job Description 1', 'Job Title 1', null, '3', '2010', '6692C889-8A75-4869-B91D-4F1909D00D2B');

insert into org.WorkHistory(contactId, endMonth, endYear, jobDescription, jobTitle, organizationId, startMonth, startYear, workHistoryId)
values ('42FC5EBC-088D-4DC2-BDBD-D89FA170E5C6', '2', '2010', 'Job Description 2', 'Job Title 2', null, '4', '2000', '96B1FB6C-EAB2-42B2-BD0A-FEFC1F98A604');

insert into org.WorkHistory(contactId, endMonth, endYear, jobDescription, jobTitle, organizationId, startMonth, startYear, workHistoryId)
values ('B6581A36-804D-45AC-B2E2-F6DA265AF7DE', '', '', 'Job Description 3', 'Job Title 3', null, '1', '2012', '4165FB55-FCD9-49B5-BD70-AD7F68F750AF');

insert into org.ContactLink(contactLinkId, sourceContactId, targetContactId, workHistoryId)
values ('1DB7C02F-DF6A-4245-839D-870B6F9E34D1','42FC5EBC-088D-4DC2-BDBD-D89FA170E5C6','B6581A36-804D-45AC-B2E2-F6DA265AF7DE','4165FB55-FCD9-49B5-BD70-AD7F68F750AF');
