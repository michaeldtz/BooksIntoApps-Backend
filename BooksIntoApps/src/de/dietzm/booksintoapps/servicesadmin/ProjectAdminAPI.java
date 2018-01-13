package de.dietzm.booksintoapps.servicesadmin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import de.dietzm.booksintoapps.db.ContentItem;
import de.dietzm.booksintoapps.db.Project;
import de.dietzm.booksintoapps.db.base.DAO;
import de.dietzm.booksintoapps.db.base.DAOFactory;
import de.dietzm.booksintoapps.utils.ResponseBuilder;
import de.dietzm.booksintoapps.utils.UUIDUtil;

@Path("/project")
public class ProjectAdminAPI {

	@GET
	@Path("/list")
	@Produces("application/json")
	public Response listProjects() {

		DAO<Project> dao = DAOFactory.getEntityManager("Project");
		List<Project> project = dao.queryAll();
		return ResponseBuilder.createListJSONResponse(project, false);

	}

	@POST
	@Path("loginto")
	@Produces("application/json")
	public Response loginToProject(@FormParam("id") Long id, @Context HttpServletRequest request) {

		DAO<Project> dao = DAOFactory.getEntityManager("Project");
		Project project = dao.get(id);

		HttpSession session = request.getSession();
		session.setAttribute("LOGGED_IN_TO_PROJECT", project.getId());

		return ResponseBuilder.createStatusResponse("S", "Project Login Successful");

	}
	
	@GET
	@Path("/listorphaneditems")
	@Produces("application/json")
	public Response getOrphanedItems() {

		DAO<ContentItem> dao = DAOFactory.getEntityManager("ContentItem");
		List<ContentItem> project = dao.query("projectID", 0l);
		return ResponseBuilder.createListJSONResponse(project, false);

	}

	/*
	 * @GET
	 * 
	 * @Path("/projects")
	 * 
	 * @Produces("application/json") public Response getProjects() {
	 * 
	 * DAO<Project> dao = DAOFactory.getEntityManager("Project"); List<Project>
	 * project = dao.queryAll();
	 * 
	 * return ResponseBuilder.createListJSONResponse(project); }
	 */

	@POST
	@Path("/create")
	@Produces("application/json")
	public Response createProject(@FormParam("title") String title,
			@FormParam("password") String password) {

		DAO<Project> dao = DAOFactory.getEntityManager("Project");
		Project project = new Project();
		project.setAccessKey(UUIDUtil.generateUUID().substring(1, 12));
		project.setAppKey("AP" + UUIDUtil.generateUUID());
		project.setSecret("SC" + UUIDUtil.generateUUID());
		project.setPassword(password);
		project.setTitle(title);

		Long newId = dao.create(project);
		return ResponseBuilder.createNewObjectCreatedResponse("Project", newId);
	}

}
