package de.dietzm.booksintoapps.usermgmt.model;

import com.googlecode.objectify.annotation.Entity;

import de.dietzm.booksintoapps.db.base.AbstractBaseEntity;

@Entity
public class ApplicationUser extends AbstractBaseEntity {

	private String userID;
	
	private String emailAdress;
	
	private String fullname;

	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getEmailAdress() {
		return emailAdress;
	}

	public void setEmailAdress(String emailAdress) {
		this.emailAdress = emailAdress;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
	
}
