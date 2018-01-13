package de.dietzm.booksintoapps.usermgmt;

import java.util.HashMap;
import java.util.List;

import de.dietzm.booksintoapps.usermgmt.model.UserGroup;

public interface UserAccessService {

	public abstract String getCurrentUserID();

	public abstract String getCurrentUserEmail();

	public abstract String getCurrentUserName();

	public abstract String getCurrentUserAuthProvider();

	public abstract boolean isUserLoggedIn();

	public abstract boolean isAdmin();
	
	public abstract String getLoginURL();
	
	public abstract String getLogoutURL();
	
	public abstract boolean isApplicationUser();
	
	public abstract boolean isInGroup(String string);
	
	public abstract HashMap<String,String> getAllLoginURLsForProvider();

	public abstract List<UserGroup> getGroupsOfUser();
	
	public abstract boolean hasUserSentInvitation();

	public abstract boolean isInGroupID(String groupID);

}