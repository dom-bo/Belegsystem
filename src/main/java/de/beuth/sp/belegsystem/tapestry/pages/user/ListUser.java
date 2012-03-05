package de.beuth.sp.belegsystem.tapestry.pages.user;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.db.dao.UserDAO;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Role;
import de.beuth.sp.belegsystem.lg.User;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse für die Auflistung aller User. Diese Klasse ist nur für Admins
 * erlaubt.
 * 
 * 
 */
@Restricted(allowedFor = { Admin.class })
public class ListUser extends BasePageImpl {
	
	/**
	 * DAO Klasse für die Persistenz.
	 */
	@Inject
	private UserDAO userDAO;
	
	/**
	 * Lokale Variable für die Schleife von Users in der TML-Datei
	 */
	@SuppressWarnings("unused")
	@Property
	private User user;
	
	/**
	 * Lokale Variable für die Schleife von User.Roles in der TML-Datei
	 */
	@Property
	private Role role;
	
	@Inject
	private Messages messages;
	
	/**
	 * Gibt alle Users des Systems zurück
	 * 
	 * @return Alle Users
	 */
	public List<User> getUsers() {
		return userDAO.getAll();
	}
	
	/**
	 * Event-Handler wenn ein User vom Benutzer gelöscht wird.
	 * 
	 * @param user
	 *            Der zu löschende User
	 */
	@CommitAfter
	public void onActionFromDelete(final User user) {				
		userDAO.delete(user);		
	}
	
	public String getRolename() {
		return messages.get(role.getRoleName());
	}

}
