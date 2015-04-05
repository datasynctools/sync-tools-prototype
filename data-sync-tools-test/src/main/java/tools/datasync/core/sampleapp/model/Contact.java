package tools.datasync.core.sampleapp.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Contact")
public class Contact {

    @Id
    private String contactId; // ContactId varchar(36) not null,
    @Column
    private Date dateOfBirth; // DateOfBirth date,
    @Column(length = 128)
    private String firstName; // FirstName varchar(25) not null,
    @Column(length = 128)
    private String lastName; // LastName varchar(25) not null,
    @Column
    private int heightFt; // HeightFt integer,
    @Column
    private int heightInch; // HeightInch integer,
    // TOD for Derby appears to be a bug:
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=286206
    // @Column(nullable = true, columnDefinition = "BLOB")
    // private byte[] picture; // Picture blob,
    @Column(length = 32, nullable = true)
    private String preferredHeight; // PreferredHeight varchar(25),

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

    // public byte[] getPicture() {
    // return picture;
    // }
    //
    // public void setPicture(byte[] picture) {
    // this.picture = picture;
    // }

    public String getPreferredHeight() {
	return preferredHeight;
    }

    public void setPreferredHeight(String preferredHeight) {
	this.preferredHeight = preferredHeight;
    }

}
