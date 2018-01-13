package de.dietzm.booksintoapps.usermgmt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import de.dietzm.booksintoapps.db.base.DAO;
import de.dietzm.booksintoapps.db.base.DAOFactory;
import de.dietzm.booksintoapps.usermgmt.model.ApplicationUser;
import de.dietzm.booksintoapps.usermgmt.model.Invitation;
import de.dietzm.booksintoapps.usermgmt.model.UserGroup;

public class GAEOpenIDUserAccessService implements UserAccessService {

	@Override
	public String getCurrentUserID() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser().getUserId();
	}

	@Override
	public String getCurrentUserEmail() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser().getEmail();
	}

	@Override
	public String getCurrentUserName() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser().getNickname();
	}

	@Override
	public String getCurrentUserAuthProvider() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser().getAuthDomain();
	}

	@Override
	public boolean isUserLoggedIn() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.isUserLoggedIn();
	}

	@Override
	public boolean isAdmin() {
		UserService userService = UserServiceFactory.getUserService();
		if (userService.isUserLoggedIn())
			return userService.isUserAdmin();
		return false;
	}

	@Override
	public String getLoginURL() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.createLoginURL("");
	}

	@Override
	public HashMap<String, String> getAllLoginURLsForProvider() {

		HashMap<String, String> openIdProviders = new HashMap<String, String>();
		UserService userService = UserServiceFactory.getUserService();
		HashSet<String> attributes = new HashSet<String>();

		// Add most common openID Providers
		openIdProviders.put("Google",
				userService.createLoginURL("/", null, "https://www.google.com/accounts/o8/id", attributes));
		openIdProviders.put("Yahoo", userService.createLoginURL("/", null, "yahoo.com", attributes));
		openIdProviders.put("MySpace", userService.createLoginURL("/", null, "myspace.com", attributes));
		openIdProviders.put("Flicker", userService.createLoginURL("/", null, "flicker.com", attributes));
		openIdProviders
				.put("MyOpenId.com", userService.createLoginURL("/", null, "myopenid.com", attributes));
		openIdProviders.put("ClaimID", userService.createLoginURL("/", null, "claimID.com", attributes));

		return openIdProviders;
	}

	@Override
	public String getLogoutURL() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.createLogoutURL("/");
	}
	
	public boolean isApplicationUser(){
		if(isAdmin())
			return true;
		
		String userId = getCurrentUserID();
		
		if(userId == null)
			return false;
		
		DAO<ApplicationUser> userDAO = DAOFactory.getEntityManager("User");
		List<ApplicationUser> userList = userDAO.query("userID", userId);
		
		if(userList.size() <= 0)
			return false;
		
		return true;
	}

	@Override
	public boolean isInGroup(String roleName) {
		
		GroupAccessService roleService = new GroupAccessService();
		String userID = getCurrentUserID();
		List<UserGroup> roles = roleService.getRolesOfUser(userID);

		for (UserGroup role : roles) {
			if (role.getTechnicalName().equals(roleName)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<UserGroup> getGroupsOfUser() {
		GroupAccessService roleService = new GroupAccessService();
		return roleService.getRolesOfUser(getCurrentUserID());
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasUserSentInvitation() {
		DAO<Invitation> inviteDAO = DAO.getByName("Invitation");
		List<Invitation> invites = inviteDAO.query("userID=", getCurrentUserID());

		if (invites.size() > 0)
			return true;

		return false;
	}

	@Override
	public boolean isInGroupID(String groupID) {
		GroupAccessService roleService = new GroupAccessService();
		String userID = getCurrentUserID();
		List<UserGroup> roles = roleService.getRolesOfUser(userID);

		for (UserGroup role : roles) {
			if (role.getId().equals(groupID)) {
				return true;
			}
		}

		return false;
	}
}
