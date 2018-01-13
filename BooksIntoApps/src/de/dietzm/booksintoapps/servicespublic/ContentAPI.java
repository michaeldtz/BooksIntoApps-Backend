package de.dietzm.booksintoapps.servicespublic;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.sun.jersey.core.util.Base64;

import de.dietzm.booksintoapps.db.ContentItem;
import de.dietzm.booksintoapps.db.LastSortNum;
import de.dietzm.booksintoapps.db.Media;
import de.dietzm.booksintoapps.db.base.DAO;
import de.dietzm.booksintoapps.db.base.DAOFactory;
import de.dietzm.booksintoapps.usermgmt.GAEOpenIDUserAccessService;
import de.dietzm.booksintoapps.usermgmt.Session;
import de.dietzm.booksintoapps.utils.JSONBuilder;
import de.dietzm.booksintoapps.utils.ResponseBuilder;

@Path("/content")
public class ContentAPI {

	@GET
	@Path("/list")
	@Produces("application/json")
	public Response getContentItemList(@Context HttpServletRequest request) {

		Long projectID = getLoggedInProject(request);
		if (projectID == null)
			return ResponseBuilder.createErrorResponse("Not Logged In");

		DAO<ContentItem> dao = DAOFactory.getEntityManager("ContentItem");
		List<ContentItem> items = dao.queryAndOrder("projectID", projectID, "sortorder");

		return ResponseBuilder.createListJSONResponse(items, false);
	}

	@POST
	@Path("/create")
	@Produces("application/json")
	public Response createContentItem(@FormParam("title") String title,
			@FormParam("sortorder") String sortorder, @Context HttpServletRequest request) {

		Long projectID = getLoggedInProject(request);
		if (projectID == null)
			return ResponseBuilder.createErrorResponse("Not Logged In");

		DAO<LastSortNum> daoSortNum = DAOFactory.getEntityManager("LastSortNum");
		DAO<ContentItem> dao = DAOFactory.getEntityManager("ContentItem");

		ContentItem newItem = new ContentItem();
		
		newItem.setLastchangeDate(new Date().getTime());
		newItem.setCreationDate(new Date().getTime());
		
		newItem.setTitle(title);
		newItem.setProjectID(projectID);
		newItem.setSortorder(new Integer(sortorder).intValue());

		/*
		 * //Determine Next Sort Num int sortNum = 10; List<LastSortNum>
		 * sortNumArray = daoSortNum.query("projectId", projectID);
		 * if(sortNumArray == null || sortNumArray.size() == 0){ LastSortNum
		 * newnum = new LastSortNum(); newnum.setProjectId(projectID);
		 * daoSortNum.create(newnum); } else { LastSortNum num =
		 * sortNumArray.get(0); num.nextSortNum(); sortNum =
		 * num.getLastSortNum(); daoSortNum.update(num); }
		 * newItem.setSortorder(sortNum);
		 */

		Long newId = dao.create(newItem);
		
		newItem.setId(newId);

		return ResponseBuilder.createNewObjectCreatedResponse("Project", (Object)newItem);

	}

	@POST
	@Path("/update")
	@Produces("application/json")
	@Consumes("application/json")
	public Response updateContentItem(String jsonData, @Context HttpServletRequest request) {

		// JSONObject obj = new JSONObject(jsonData);
		try {
			ContentItem item = (ContentItem) JSONBuilder.convertJSON2Object(jsonData, ContentItem.class);
			DAO<ContentItem> dao = DAOFactory.getEntityManager("ContentItem");
			
			item.setLastchangeDate(new Date().getTime());
			
			
			dao.update(item);

			return ResponseBuilder.createSuccessResponse();

		} catch (Exception e) {
			return ResponseBuilder.createErrorResponse(e.getMessage());
		}

	}

	@POST
	@Path("/delete")
	@Produces("application/json")
	public Response deleteContentItem(@FormParam("id") String id, @Context HttpServletRequest request) {

		Long projectID = getLoggedInProject(request);
		if (projectID == null)
			return ResponseBuilder.createErrorResponse("Not Logged In");

		Long idLong = new Long(id);
		DAO<ContentItem> dao = DAOFactory.getEntityManager("ContentItem");
		ContentItem item = dao.get(idLong);

		if (item != null && item.getProjectID().equals(projectID)) {
			dao.delete(item);
		}

		return ResponseBuilder.createSuccessResponse();

	}

	@POST
	@Path("/uploadmedia")
	@Produces("application/json")
	public Response uploadMedia(@FormParam("filename") String filename,
			@FormParam("content") String fileContent, @Context HttpServletRequest request) {

		Long projectID = getLoggedInProject(request);
		if (projectID == null)
			return ResponseBuilder.createErrorResponse("Not Logged In");

		DAO<Media> dao = new DAO<Media>(Media.class);

		Media media = new Media();
		media.setFilename(filename);
		media.setContent(fileContent);
		media.setProjectID(projectID);

		long newId = 0;

		List<Media> mediaList = dao.query("filename", media.getFilename());
		if (mediaList == null || mediaList.size() == 0) {
			newId = dao.create(media);
		} else {
			for (int i = 0; i < mediaList.size(); i++) {
				if (i == 0) {
					newId = mediaList.get(i).getId();
					media.setId(newId);
					dao.update(media);
				} else {
					dao.delete(mediaList.get(i));
				}
			}
		}

		return ResponseBuilder.createNewObjectCreated(newId);
	}

	@GET
	@Path("/listmedia")
	@Produces("application/json")
	public Response getMediaList(@Context HttpServletRequest request) {

		Long projectID = getLoggedInProject(request);
		if (projectID == null)
			return ResponseBuilder.createErrorResponse("Not Logged In");

		DAO<Media> dao = new DAO<Media>(Media.class);
		List<Media> media = dao.queryAndOrder("projectID", projectID, "filename");

		return ResponseBuilder.createListJSONResponse(media, false);
	}
	
	@POST
	@Path("/deleteMedia")
	@Produces("application/json")
	public Response deleteMediaItem(@FormParam("id") String id, @Context HttpServletRequest request) {

		Long projectID = getLoggedInProject(request);
		if (projectID == null)
			return ResponseBuilder.createErrorResponse("Not Logged In");

		Long idLong = new Long(id);
		DAO<Media> dao = DAOFactory.getEntityManager("Media");
		Media item = dao.get(idLong);

		if (item != null && item.getProjectID().equals(projectID)) {
			dao.delete(item);
		}

		return ResponseBuilder.createSuccessResponse();

	}
	@GET
	@Path("/media/{filename}")
	@Produces("image/png")
	public Response getMedia(@PathParam("filename") String filename, @Context HttpServletRequest request) {

		Long projectID = getLoggedInProject(request);
		if (projectID == null)
			return ResponseBuilder.createErrorResponse("Not Logged In");

		DAO<Media> dao = new DAO<Media>(Media.class);
		List<Media> medias = dao.query("filename", filename);

		for (Media media : medias) {
			if (media.getProjectID().equals(projectID)) {
				String imgContent = media.getContent();

				byte[] imageData = Base64.decode(imgContent);
				return Response.ok(imageData).build();

			}
		}
		return Response.status(404).build();

	}

	@GET
	@Path("/mediathumbnail/{filename}")
	@Produces("image/png")
	public Response getMediaThumbnail(@PathParam("filename") String filename,
			@Context HttpServletRequest request) {

		Long projectID = getLoggedInProject(request);
		if (projectID == null)
			return ResponseBuilder.createErrorResponse("Not Logged In");

		DAO<Media> dao = new DAO<Media>(Media.class);
		List<Media> medias = dao.query("filename", filename);

		for (Media media : medias) {
			if (media.getProjectID().equals(projectID)) {

				String imgContent = media.getContent();

				byte[] imageData = Base64.decode(imgContent);

				ImagesService imgService = ImagesServiceFactory.getImagesService();

				Image img = ImagesServiceFactory.makeImage(imageData);
				Transform resize = ImagesServiceFactory.makeResize(100, 100, false);
				Image thumbnailImg = imgService.applyTransform(resize, img);

				byte[] thumbnailData = thumbnailImg.getImageData();
				
				return Response.ok(thumbnailData).build();

			}
		}
		return Response.status(404).build();

	}

	public Long getLoggedInProject(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Long projectID = (Long) session.getAttribute("LOGGED_IN_TO_PROJECT");

		if (projectID != null && !projectID.equals("")) {
			return projectID;
		} else {
			return null;
		}
	}

}
