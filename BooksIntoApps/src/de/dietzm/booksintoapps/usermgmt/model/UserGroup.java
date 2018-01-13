package de.dietzm.booksintoapps.usermgmt.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.annotation.Entity;

import de.dietzm.booksintoapps.db.base.AbstractBaseEntity;

@XmlRootElement
@Entity
public class UserGroup extends AbstractBaseEntity {

	private String technicalName;
	
	private String description;
	
	private boolean isCoreGroup;
	
	
	public String getTechnicalName() {
		return technicalName;
	}

	public void setTechnicalName(String technicalName) {
		this.technicalName = technicalName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isCoreGroup() {
		return isCoreGroup;
	}

	public void setCoreGroup(boolean isCoreGroup) {
		this.isCoreGroup = isCoreGroup;
	}
	
}
