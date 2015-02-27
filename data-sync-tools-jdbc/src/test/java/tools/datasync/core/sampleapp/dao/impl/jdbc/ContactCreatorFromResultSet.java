package tools.datasync.core.sampleapp.dao.impl.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tools.datasync.core.sampleapp.model.Contact;
import tools.datasync.core.sampleapp.utils.CreatorFromResultSet;

public class ContactCreatorFromResultSet implements
	CreatorFromResultSet<List<Contact>> {

    private List<Contact> answer = new ArrayList<Contact>();

    public void createAndSetValue(ResultSet resultSet) {
	try {
	    while (resultSet.next()) {
		addContact(answer, resultSet);
	    }

	} catch (SQLException e) {
	    throw (new RuntimeException(e));
	}
    }

    private void addContact(List<Contact> answer, ResultSet resultSet)
	    throws SQLException {
	// ORDER
	// ContactId varchar(36) not null,
	// DateOfBirth date,
	// FirstName varchar(25) not null,
	// LastName varchar(25) not null,
	// HeightFt integer,
	// HeightInch integer,
	// Picture clob,
	// PreferredHeight varchar(25)
	Contact contact = new Contact();
	contact.setContactId(resultSet.getString(1));
	contact.setDateOfBirth(resultSet.getDate(2));
	contact.setFirstName(resultSet.getString(3));
	contact.setLastName(resultSet.getString(4));
	contact.setHeightFt(resultSet.getInt(5));
	contact.setHeightInch(resultSet.getInt(6));
	contact.setPicture(resultSet.getBytes(7));
	contact.setPreferredHeight(resultSet.getString(8));
	answer.add(contact);
    }

    public List<Contact> getCreatedValue() {
	return answer;
    }
}
