package de.beuth.sp.belegsystem.tapestry.pages.program;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.db.dao.ProgramDAO;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Program;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse für die Auflistung aller Studiengänge
 * Diese Klasse ist nur für Admin erlaubt
 * 
 */
@Restricted(allowedFor = Admin.class)
public class ListProgram extends BasePageImpl {
	
	/**
	 * DAO Klasse für die Persistenz
	 */
	@Inject
	private ProgramDAO programDAO;
	
	/**
	 * Lokale Variable für die Schleife in der TML-Datei
	 */
	@SuppressWarnings("unused")
	@Property
	private Program program;
	
	/**
	 * Gibt alle Studiengänge des Systems zurück
	 * 
	 * @return Alle Studiengänge
	 */
	public List<Program> getPrograms() {
		return programDAO.getAll();
	}
	
	/**
	 * Event-Handler wenn ein Studiengang gelöscht wird
	 * 
	 * @param program
	 *            Der zu löschende Studiengang
	 */
	@CommitAfter
	public void onActionFromDelete(final Program program) {
		programDAO.delete(program);
	}

}
