package de.beuth.sp.belegsystem.tapestry.pages.admin;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.db.dao.AdminDAO;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Program;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse für die Auflistung alle Admins. Nur für SuperAdmins erlaubt. 
 * 
 */
@Restricted(allowedFor = Admin.class, onlyForSuperAdmin = true)
public class ListAdmin extends BasePageImpl {

	/**
	 * DAO Klasse für die Persistenz.
	 */
	@Inject
	private AdminDAO adminDAO;

	/**
	 * Lokale Variable für die Schleife der Admins in der TML-Datei.
	 */
	@SuppressWarnings("unused")
	@Property
	private Admin admin;
	
	/**
	 * Lokale Variable für die Schleife der Programs für die der Admin zuständig ist in der TML-Datei.
	 */
	@Property
	@SuppressWarnings("unused")
	private Program program;

	/**
	 * Gibt alle Admins des Systems zurück.
	 * 
	 * @return Alle Admins.
	 */
	public List<Admin> getAdmins() {
		return adminDAO.getAll();
	}

	/**
	 * Event-Handler wenn ein Admin vom Benutzer gelöscht wird.
	 * 
	 * @param admin
	 *            Der zu löschende Admin.
	 */
	@CommitAfter
	public void onActionFromDelete(final Admin admin) {
		adminDAO.delete(admin);
	}

}
