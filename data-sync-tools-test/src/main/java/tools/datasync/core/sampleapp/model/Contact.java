package tools.datasync.core.sampleapp.model;

import java.util.Date;

public class Contact {

    private String contactId; // ContactId varchar(36) not null,
    private Date dateOfBirth; // DateOfBirth date,
    private String firstName; // FirstName varchar(25) not null,
    private String lastName; // LastName varchar(25) not null,
    private int heightFt; // HeightFt integer,
    private int heightInch; // HeightInch integer,
    private byte[] picture; // Picture clob,
    private String preferredHeight; // PreferredHeight varchar(25),

    // primary key (ContactId)

    public String getContactId() {
	return contactId;
    }

    public void setContactId(String contactId) {
	this.contactId = contactId;
    }

    public Date getDateOfBirth() {
	return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
	this.dateOfBirth = dateOfBirth;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public int getHeightFt() {
	return heightFt;
    }

    public void setHeightFt(int heightFt) {
	this.heightFt = heightFt;
    }

    public int getHeightInch() {
	return heightInch;
    }

    public void setHeightInch(int heighInch) {
	this.heightInch = heighInch;
    }

    public byte[] getPicture() {
	return picture;
    }

    public void setPicture(byte[] picture) {
	this.picture = picture;
    }

    public String getPreferredHeight() {
	return preferredHeight;
    }

    public void setPreferredHeight(String preferredHeight) {
	this.preferredHeight = preferredHeight;
    }

}
