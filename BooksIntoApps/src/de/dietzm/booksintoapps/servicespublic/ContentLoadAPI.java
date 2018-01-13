package de.dietzm.booksintoapps.servicespublic;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.sun.jersey.core.util.Base64;

import de.dietzm.booksintoapps.db.ContentItem;
import de.dietzm.booksintoapps.db.Media;
import de.dietzm.booksintoapps.db.Project;
import de.dietzm.booksintoapps.db.base.DAO;
import de.dietzm.booksintoapps.db.base.DAOFactory;
import de.dietzm.booksintoapps.utils.JSONBuilder;
import de.dietzm.booksintoapps.utils.ResponseBuilder;

@Path("/load")
public class ContentLoadAPI {

	public Media getMediaByFilename(Long projectID, String filename) {
		DAO<Media> dao = DAOFactory.getEntityManager("Media");
		List<Media> medias = dao.query("filename", filename);

		for (Media media : medias) {
			if (media.getProjectID().equals(projectID)) {
				String imgContent = media.getContent();
				// byte[] imageData = Base64.decode(imgContent);
				return media;

			}
		}
		return null;
	}

	@GET
	@Path("/contentlist")
	@Produces("application/json")
	public Response getContentItemList(@QueryParam("appkey") String appkey,
			@QueryParam("secret") String secret, @QueryParam("max") int max, @QueryParam("skip") int skip) {

		DAO<Project> daoProject = DAOFactory.getEntityManager("Project");
		List<Project> projects = daoProject.query("appKey", appkey);
		if (projects.size() == 1) {

			Project project = projects.get(0);

			if (project.getSecret().equals(secret)) {
				Long projectID = project.getId();

				DAO<ContentItem> daoContent = DAOFactory.getEntityManager("ContentItem");
				List<ContentItem> contentItems = daoContent
						.queryAndOrder("projectID", projectID, "sortorder");

				/*
				 * DAO<Media> daoMedia = DAOFactory.getEntityManager("Media");
				 * List<Media> mediaItems = daoMedia.query("projectID",
				 * projectID);
				 */

				StringBuffer buffer = new StringBuffer();
				// ArrayList<ContentItem> finalizedItems = new
				// ArrayList<ContentItem>();
				int cnt = 0;

				for (ContentItem contentItem : contentItems) {

					cnt++;
					if (skip > 0 && cnt <= skip)
						continue;

					if (max > 0 && cnt > max)
						break;

					String content = contentItem.getContent();
					int idx = -1;
					while ((idx = content.indexOf("/services/content/media/")) >= 0) {
						System.out.println("FOUND at " + idx);
						String sub1 = content.substring(idx);
						int idx2 = sub1.indexOf("\"");
						String repString = sub1.substring(0, idx2);
						String imgString = repString.substring(24);

						System.out.println("sub2: " + repString + " with image " + imgString);

						Media media = getMediaByFilename(projectID, imgString);
						if (media != null) {
							String imgContent = media.getContent();
							String ext = imgString.substring(imgString.indexOf(".") + 1);
							content = content.replace(repString, "data:image/" + ext + ";base64,"
									+ imgContent);

						}
					}

					contentItem.setContent(content);
					try {
						String jsonContentItem = JSONBuilder.convertObjectToJSON(contentItem, false).toString();
						if(buffer.length() > 0)
							buffer.append(",");
						buffer.append(jsonContentItem);
					} catch (JSONException e) {
						e.printStackTrace();
						return ResponseBuilder.createErrorResponse(e.toString());
					}

					// finalizedItems.add(contentItem);
				}

				return Response.ok("[" + buffer.toString() + "]").build();

		
			}
		}

		return ResponseBuilder.createErrorResponse("Project not found");
	}

	@GET
	@Path("/contentchangeslist")
	@Produces("application/json")
	public Response getContentItemList(@QueryParam("appkey") String appkey,
			@QueryParam("secret") String secret, @QueryParam("since") long sincedate) {

		DAO<Project> daoProject = DAOFactory.getEntityManager("Project");
		List<Project> projects = daoProject.query("appKey", appkey);
		if (projects.size() == 1) {

			Project project = projects.get(0);

			if (project.getSecret().equals(secret)) {
				Long projectID = project.getId();

				DAO<ContentItem> daoContent = DAOFactory.getEntityManager("ContentItem");
				List<ContentItem> contentItems = daoContent
						.queryAndOrder("projectID", projectID, "sortorder");

				/*
				 * DAO<Media> daoMedia = DAOFactory.getEntityManager("Media");
				 * List<Media> mediaItems = daoMedia.query("projectID",
				 * projectID);
				 */

				StringBuffer buffer = new StringBuffer();

				for (ContentItem contentItem : contentItems) {

					if(contentItem.getLastchangeDate() == null || contentItem.getLastchangeDate() <= sincedate)
						continue;
					
					String content = contentItem.getContent();
					int idx = -1;
					while ((idx = content.indexOf("/services/content/media/")) >= 0) {
						System.out.println("FOUND at " + idx);
						String sub1 = content.substring(idx);
						int idx2 = sub1.indexOf("\"");
						String repString = sub1.substring(0, idx2);
						String imgString = repString.substring(24);

						System.out.println("sub2: " + repString + " with image " + imgString);

						Media media = getMediaByFilename(projectID, imgString);
						if (media != null) {
							String imgContent = media.getContent();
							String ext = imgString.substring(imgString.indexOf(".") + 1);
							content = content.replace(repString, "data:image/" + ext + ";base64,"
									+ imgContent);

						}
					}

					contentItem.setContent(content);
					try {
						String jsonContentItem = JSONBuilder.convertObjectToJSON(contentItem, false).toString();
						if(buffer.length() > 0)
							buffer.append(",");
						buffer.append(jsonContentItem);
					} catch (JSONException e) {
						e.printStackTrace();
						return ResponseBuilder.createErrorResponse(e.toString());
					}

					// finalizedItems.add(contentItem);
				}

				return Response.ok("[" + buffer.toString() + "]").build();

			}
		}

		return ResponseBuilder.createErrorResponse("Project not found");
	}

	@GET
	@Path("/contentoverview")
	@Produces("application/json")
	public Response getContentItemListOverview(@QueryParam("appkey") String appkey,
			@QueryParam("secret") String secret) {

		DAO<Project> daoProject = DAOFactory.getEntityManager("Project");
		List<Project> projects = daoProject.query("appKey", appkey);
		if (projects.size() == 1) {

			Project project = projects.get(0);

			if (project.getSecret().equals(secret)) {
				Long projectID = project.getId();

				DAO<ContentItem> daoContent = DAOFactory.getEntityManager("ContentItem");
				List<ContentItem> contentItems = daoContent
						.queryAndOrder("projectID", projectID, "sortorder");

				/*
				 * DAO<Media> daoMedia = DAOFactory.getEntityManager("Media");
				 * List<Media> mediaItems = daoMedia.query("projectID",
				 * projectID);
				 */

				StringBuffer str = new StringBuffer();

				for (ContentItem contentItem : contentItems) {

					String content = contentItem.getContent();
					int idx = -1;
					while ((idx = content.indexOf("/services/content/media/")) >= 0) {
						System.out.println("FOUND at " + idx);
						String sub1 = content.substring(idx);
						int idx2 = sub1.indexOf("\"");
						String repString = sub1.substring(0, idx2);
						String imgString = repString.substring(24);

						System.out.println("sub2: " + repString + " with image " + imgString);

						Media media = getMediaByFilename(projectID, imgString);
						if (media != null) {
							String imgContent = media.getContent();
							String ext = imgString.substring(imgString.indexOf(".") + 1);
							content = content.replace(repString, "data:image/" + ext + ";base64,"
									+ imgContent);

						}
					}

					int len = content.length();
					str.append(contentItem.getTitle() + ": " + len + "\n");
				}

				return Response.ok(str.toString()).build();

			}
		}

		return ResponseBuilder.createErrorResponse("Project not found");
	}

}
