package de.dietzm.booksintoapps.servicespublic;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
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
import de.dietzm.booksintoapps.utils.JSONBuilder;
import de.dietzm.booksintoapps.utils.ResponseBuilder;
import de.dietzm.booksintoapps.utils.UUIDUtil;

@Path("/project")
public class ProjectAPI {
	
	
	
	
	@GET
	@Path("/get")
	@Produces("application/json")
	public Response getProjectByAccessKey(@QueryParam("accesskey")String accessKey) {
		
		DAO<Project> dao = DAOFactory.getEntityManager("Project");
		List<Project> project = dao.query("accessKey", accessKey);
				
		
		if(project.size() == 1){
			return ResponseBuilder.createObjectJSONResponse(project.get(0), true);
		} else {
			return Response.serverError().build();	
		}
	}
	
	
	@POST
	@Path("/login")
	@Produces("application/json")
	public Response loginToProject(@FormParam("accesskey")String accessKey, @FormParam("password")String password, @Context HttpServletRequest request)  {

		DAO<Project> dao = DAOFactory.getEntityManager("Project");
		List<Project> projects = dao.query("accessKey", accessKey);
		
		if(projects.size() == 1){
			Project project = projects.get(0);
			if(password != null && project.getPassword() != null && project.getPassword().equals(password)){
				
				HttpSession session = request.getSession();
				session.setAttribute("LOGGED_IN_TO_PROJECT", project.getId());
				
				return ResponseBuilder.createStatusResponse("S","Project Login Successful");
			} else {
				return ResponseBuilder.createStatusResponse("E","AccessKey or Password incorrect " + accessKey);
			}
		} else {
			return ResponseBuilder.createStatusResponse("E","AccessKey or Password incorrect " + accessKey);	
		}	
	}
	
	
	@POST
	@Path("/logout")
	@Produces("application/json")
	public Response logout(@Context HttpServletRequest request)  {
		HttpSession session = request.getSession();
		session.setAttribute("LOGGED_IN_TO_PROJECT", null);
		return ResponseBuilder.createStatusResponse("S","Project Logout Successful");
	}
	
	
	@GET
	@Path("/loggedin")
	@Produces("application/json")
	public Response getLoggedInProject(@Context HttpServletRequest request)  {
		HttpSession session = request.getSession();
		Long projectID = (Long) session.getAttribute("LOGGED_IN_TO_PROJECT");
		
		if(projectID != null && !projectID.equals("")){
			DAO<Project> dao = DAOFactory.getEntityManager("Project");
			Project project = dao.get(projectID);
			
			return ResponseBuilder.createFlexibleResponse("status", "S", "projectID",project.getId().toString(), "title", project.getTitle());
		} else {
			return ResponseBuilder.createStatusResponse("Error","AccessKey or Password incorrect");
		}
	}
	
	/*
	@GET
	@Path("/projects")
	@Produces("application/json")
	public Response getProjects() {

		DAO<Project> dao = DAOFactory.getEntityManager("Project");
		List<Project> project = dao.queryAll();
		
		return ResponseBuilder.createListJSONResponse(project);
	}*/
	
	
	@GET
	@Path("/create")
	@Produces("application/json")
	public Response createProject()  {

		DAO<Project> dao = DAOFactory.getEntityManager("Project");
		Project project = new Project();
		project.setAccessKey("AK" + UUIDUtil.generateUUID());
		project.setAppKey("AP" + UUIDUtil.generateUUID());
		project.setSecret("SC" + UUIDUtil.generateUUID());
		project.setPassword("Init1234");
		project.setTitle("Neues Buch");
		
		Long newId = dao.create(project);
		return ResponseBuilder.createNewObjectCreatedResponse("Project",newId);
	}
	
	
	@GET
	@Path("/updateAll")
	@Produces("application/json")
	public Response updateAll()  {

		DAO<Project> dao = DAOFactory.getEntityManager("Project");
		List<Project> list = dao.queryAll();
		for (Iterator<Project> iterator = list.iterator(); iterator.hasNext();) {
			Project project = (Project) iterator.next();
			dao.update(project);
		}
		
		return ResponseBuilder.createSuccessResponse();
	}
	
	
	@POST
	@Path("/update")
	@Produces("application/json")
	@Consumes("application/json")
	public Response updateContentItem(String jsonData, @Context HttpServletRequest request) {

		
		try {
			Project item = (Project) JSONBuilder.convertJSON2Object(jsonData, Project.class);
			DAO<Project> dao = DAOFactory.getEntityManager("Project");
			dao.update(item);

			return ResponseBuilder.createSuccessResponse();

		} catch (Exception e) {
			return ResponseBuilder.createErrorResponse(e.getMessage());
		}

	}
	


}
