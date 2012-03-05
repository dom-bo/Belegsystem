package de.beuth.sp.belegsystem.tapestry.pages.instructor;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.db.dao.InstructorDAO;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Program;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse für die Auflistung aller {@link Instructor}s.
 * 
 */
@Restricted(allowedFor = Admin.class)
public class ListInstructor extends BasePageImpl {

	/**
	 * DAO Klasse für die Persistenz.
	 */
	@Inject
	private InstructorDAO instructorDAO;

	/**
	 * Lokale Variable für die Schleife der Instructors in der TML-Datei.
	 */
	@SuppressWarnings("unused")
	@Property
	private Instructor instructor;
	
	/**
	 * Lokale Variable für die Schleife der Programs für die der Instructor Kurse gibt in der TML-Datei.
	 */
	@Property
	@SuppressWarnings("unused")
	private Program program;

	/**
	 * Gibt alle Instructors des Systems zurück.
	 * 
	 * @return Alle Instructors.
	 */
	public List<Instructor> getInstructors() {
		return instructorDAO.getAll();
	}

	/**
	 * Event-Handler wenn ein Instructor vom Benutzer gelöscht wird.
	 * 
	 * @param instructor
	 *            Der zu löschende Instructor.
	 */
	@CommitAfter
	public void onActionFromDelete(final Instructor instructor) {
		instructorDAO.delete(instructor);
	}

}
