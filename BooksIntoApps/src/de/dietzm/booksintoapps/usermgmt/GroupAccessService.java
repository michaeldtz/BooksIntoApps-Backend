package de.dietzm.booksintoapps.usermgmt;

import java.util.ArrayList;
import java.util.List;

import de.dietzm.booksintoapps.db.base.DAO;
import de.dietzm.booksintoapps.usermgmt.model.UserGroup;
import de.dietzm.booksintoapps.usermgmt.model.UserGroupAssignment;

public class GroupAccessService {

	public List<UserGroup> getRolesOfUser(String userID){
		
		@SuppressWarnings("unchecked")
		DAO<UserGroupAssignment> roleDAO = DAO.getByName("UserGroupAssignment");
		List<UserGroupAssignment> rolesAss = roleDAO.query("userID", userID);
		
		ArrayList<UserGroup> userRoles = new ArrayList<UserGroup>();
		for(UserGroupAssignment roleAss : rolesAss){
			UserGroup role = roleAss.getRole();
			userRoles.add(role);
		}
		
		return userRoles;
	}
	
	public List<UserGroup> getAllRoles(){
		
		@SuppressWarnings("unchecked")
		DAO<UserGroup> roleDAO = DAO.getByName("UserGroup");
		List<UserGroup> roles = roleDAO.queryAll();
		
		return roles;
	}
	
	
}
