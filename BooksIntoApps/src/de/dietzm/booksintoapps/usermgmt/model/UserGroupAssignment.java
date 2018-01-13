package de.dietzm.booksintoapps.usermgmt.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.annotation.Entity;

import de.dietzm.booksintoapps.db.base.AbstractBaseEntity;

@XmlRootElement
@Entity
public class UserGroupAssignment extends AbstractBaseEntity{

	private UserGroup role;
	
	private String userID;

	private Boolean active;

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public UserGroup getRole() {
		return role;
	}

	public void setRole(UserGroup role) {
		this.role = role;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
	
}
