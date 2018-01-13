package de.dietzm.booksintoapps.db.base;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.cmd.Query;

import de.dietzm.booksintoapps.db.ContentItem;
import de.dietzm.booksintoapps.db.LastSortNum;
import de.dietzm.booksintoapps.db.Media;
import de.dietzm.booksintoapps.db.Project;
import de.dietzm.booksintoapps.usermgmt.model.ApplicationUser;
import de.dietzm.booksintoapps.usermgmt.model.UserGroup;
import de.dietzm.booksintoapps.usermgmt.model.UserGroupAssignment;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class DAO<E extends AbstractBaseEntity>  {

	static {
		
		ObjectifyService.run(new VoidWork() {
		    public void vrun() {
		    	registerWithName("ContentItem", ContentItem.class);
		    	registerWithName("LastSortNum", LastSortNum.class);
				registerWithName("Project", Project.class);
				registerWithName("Media", Media.class);
				registerWithName("User", ApplicationUser.class);
				registerWithName("UserGroup", UserGroup.class);
				registerWithName("UserGroupAssignment", UserGroupAssignment.class);
		    }
		});
		
		
		//registerWithName("Invitation", Invitation.class);
	}

	private static HashMap<String, Class> registeredClasses;

	public static void registerWithName(String name, Class<? extends AbstractBaseEntity> clz) {
		if (registeredClasses == null)
			registeredClasses = new HashMap<String, Class>();

		if (!registeredClasses.containsKey(name)) {
			registeredClasses.put(name, clz);
			ObjectifyService.register(clz);
		}
	}

	public static DAO getByName(String name) {
		if (registeredClasses == null)
			registeredClasses = new HashMap<String, Class>();
		if (registeredClasses.containsKey(name)) {
			Class clz = registeredClasses.get(name);
			return new DAO(clz);

		}
		return null;
	}

	private Class clz;

	public DAO(Class<? extends AbstractBaseEntity> clz) {
		super();
		this.clz = clz;
		
	}

	public E get(Long id) {
		try {
			return (E) ofy().load().type(clz).id(id).now();
		} catch (NotFoundException e) {
			return null;
		}
	}

	public List<E> get(Long... ids) {
		Map<Long, E> fetched = ofy().load().type(clz).ids(ids);
		Iterator<E> it = fetched.values().iterator();
		ArrayList<E> list = new ArrayList<E>();
		while (it.hasNext()) {
			E obj = it.next();
			list.add(obj);
		}
		return list;
	}

	public Long create(E object) {
		Key<E> key = ofy().save().entity(object).now();
		return key.getId();
	}
	
	public void update(E object) {
		ofy().save().entity(object).now();
	}

	public void delete(E... objects) {
		ofy().delete().entities((Object[]) objects).now();
	}

	public void deleteIDs(Long... ids) {
		for (int i = 0; i < ids.length; i++) {
			ofy().delete().type(clz).id(ids[i]).now();
		}	
	}

	public void delete(List<E> repoContentList) {
		ofy().delete().entities(repoContentList).now();
	}

	public List<E> queryAll() {
		//Query<E> queryResult = ;
		Iterator<E> it = ofy().load().type(clz).iterator();
		ArrayList<E> list = new ArrayList<E>();
		while (it.hasNext()) {
			E obj = it.next();
			list.add(obj);
		}
		return list;
	}

	public List<E> query(String condition, Object value) {
		Query<E> queryResult = ofy().load().type(clz).filter(condition, value);
		Iterator<E> it = queryResult.iterator();
		ArrayList<E> list = new ArrayList<E>();
		while (it.hasNext()) {
			E obj = it.next();
			list.add(obj);
		}

		return list;

	}

	public List<E> queryAndOrder(String condition, Object value, String orderField) {
		Iterator<E> it = ofy().load().type(clz).filter(condition, value).order(orderField).iterator();
		ArrayList<E> list = new ArrayList<E>();
		while (it.hasNext()) {
			E obj = it.next();
			list.add(obj);
		}

		return list;

	}

}