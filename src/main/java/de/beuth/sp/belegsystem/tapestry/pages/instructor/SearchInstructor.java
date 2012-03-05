package de.beuth.sp.belegsystem.tapestry.pages.instructor;

import java.util.List;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.db.dao.InstructorDAO;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse für das Suchen eines {@link Instructor}s.
 * 
 * 
 */
@Restricted(allowedFor = Admin.class)
public class SearchInstructor extends BasePageImpl {

	/**
	 * DAO Klasse für die Persistenz.
	 */
	@Inject
	private InstructorDAO instructorDAO;

	/**
	 * Speichert den vom Benutzer eingegebenen Suchbegriff.
	 */
	@PageActivationContext
	@Property
	private String searchValue;

	/**
	 * Speichert die Form-Komponente.
	 */
	@SuppressWarnings("unused")
	@Component
	private Form form;

	/**
	 * Lokale Variable für die Schleife in der TML-Datei.
	 */
	@SuppressWarnings("unused")
	@Property
	private Instructor instructor;

	/**
	 * Gibt alle Instructors zurück, die den Suchbegriff enthaltn (searchValue).
	 */
	@Cached
	public List<Instructor> getInstructors() {
		return instructorDAO.searchByName(searchValue);
	}

	/**
	 * Wird aufgerufen wenn das Formular erfolgreich verarbeitet wurde.
	 * 
	 * @return Die Seite welche aufgerufen werden soll (SearchInstructor).
	 */
	@CommitAfter
	Object onSuccess() {
		return SearchInstructor.class;
	}

}
