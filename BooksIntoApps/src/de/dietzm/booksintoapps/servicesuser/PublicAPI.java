package de.dietzm.booksintoapps.servicesuser;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import de.dietzm.booksintoapps.db.ContentItem;
import de.dietzm.booksintoapps.db.Project;
import de.dietzm.booksintoapps.db.base.DAO;
import de.dietzm.booksintoapps.db.base.DAOFactory;
import de.dietzm.booksintoapps.utils.ResponseBuilder;

@Path("/publicapi")
public class PublicAPI {

	@GET
	@Path("/listcontent")
	@Produces("application/json")
	public Response getContentItemList(@QueryParam("appkey") String appkey,
			@QueryParam("secret") String secret) {

		DAO<Project> daoProject = DAOFactory.getEntityManager("Project");
		List<Project> projects = daoProject.query("appkey", appkey);
		if (projects.size() == 1) {

			Project project = projects.get(0);

			if (project.getSecret().equals(secret)) {
				Long projectID = project.getId();

				DAO<ContentItem> dao = DAOFactory.getEntityManager("ContentItem");
				List<ContentItem> items = dao.queryAndOrder("projectID", projectID, "sortorder");

				return ResponseBuilder.createListJSONResponse(items, false);
			}
		}

		return ResponseBuilder.createErrorResponse("Project not found");
	}


}
