package de.dietzm.booksintoapps.servicespublic;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import de.dietzm.booksintoapps.usermgmt.GAEOpenIDUserAccessService;
import de.dietzm.booksintoapps.usermgmt.UserAccessService;
import de.dietzm.booksintoapps.usermgmt.model.UserGroup;

@Path("/session")
public class SessionInfoAPI {
	
	
	@GET
	@Path("/isLoggedIn")
	@Produces("application/json")
	public Response isLoggedIn() throws JSONException {

		JSONObject json = new JSONObject();
		UserAccessService userService = new GAEOpenIDUserAccessService();

		if (userService.isUserLoggedIn()) {
			List<UserGroup> roles = userService.getGroupsOfUser();
			JSONArray roleArr = new JSONArray();
			for (UserGroup role : roles) {
				String techName = role.getTechnicalName();
				roleArr.put(techName);
			}

			if (userService.isApplicationUser()) {
				json.put("hasAppRole", true);
			} else {
				json.put("hasAppRole", false);
			}

			json.put("loggedIn", true);
			json.put("loginName", userService.getCurrentUserName());
			json.put("email", userService.getCurrentUserEmail());
			json.put("roles", roleArr);
		} else {
			json.put("loggedIn", false);
		}

		if (userService.isAdmin()) {
			json.put("isAdmin", true);
			json.put("hasAppRole", true);
		}

		return Response.ok(json.toString()).build();
	}

	@GET
	@Path("/getOpenIDProvider")
	@Produces("application/json")
	public Response getOpenIDProvider() throws JSONException {

		GAEOpenIDUserAccessService userSerive = new GAEOpenIDUserAccessService();
		Map<String, String> providersAndURLs = userSerive.getAllLoginURLsForProvider();

		JSONObject providerJSON = new JSONObject();
		Iterator<String> providers = providersAndURLs.keySet().iterator();
		while (providers.hasNext()) {
			String provider = (String) providers.next();
			String url = providersAndURLs.get(provider);
			providerJSON.put(provider, url);
		}

		JSONObject resultJSON = new JSONObject();
		resultJSON.put("openIDProviders", providerJSON);

		return Response.ok(resultJSON.toString()).build();

	}

	@GET
	@Path("/getLogoutURL")
	@Produces("application/json")
	public Response getLogoutURL() throws JSONException {

		UserAccessService userSerive = new GAEOpenIDUserAccessService();
		JSONObject json = new JSONObject();
		json.put("logoutURL", userSerive.getLogoutURL());

		return Response.ok(json.toString()).build();
	}

}
