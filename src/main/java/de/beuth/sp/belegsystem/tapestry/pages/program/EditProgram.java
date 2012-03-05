package de.beuth.sp.belegsystem.tapestry.pages.program;

import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.db.dao.ProgramDAO;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Program;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse zum editieren und/oder erstellen eines Studiengangs
 * Diese Klasse ist nur für Admin erlaubt die diesen Studiengang administrieren.
 * 
 * 
 */
@Restricted(allowedFor=Admin.class, onlyIfAssociatedToProgram=true)
public class EditProgram extends BasePageImpl {
	
	/**
	 * Wenn ein Studiengang editiert werden soll, wird dieser an die Page
	 * übergeben (wenn dieser null ist wird er in getProgram erstellt als
	 * neuer Studiengang).
	 */
	@PageActivationContext
	private Program program;
	
	/**
	 * Für die Oberfläche um festzustellen, ob ein Studiengang editiert oder 
	 * neu erstellt wird (für die Beschriftung des Buttons).
	 */
	private boolean create = false;
	
	/**
	 * DAO Klasse für die Persistenz.
	 */
	@Inject
	private ProgramDAO programDAO;
	
	/**
	 * Hilfsklasse für den Zugriff auf die lokalisierten Werte.
	 */
	@Inject
	private Messages messages;
	
	/**
	 * Wird beim aktivieren der Seite aufgerufen und prüft, ob ein 
	 * Studiengang übergeben wurde.
	 */
	void onActivate() {
		create = program==null;
	}

	public Program getProgram() {
		return program;
	}
	
	public void setProgram(final Program program) {
		this.program = program;
	}
	
	/**
	 * Wenn das Formular erfolgreich geprüft wurde. Der Studiengang wird dann
	 * peristent gespeichert.
	 * 
	 * @return Objekt der nächsten Seite die aufgerufen werden soll
	 *         (ListProgram).
	 */
	@CommitAfter
	public Object onSuccessFromEditProgramForm() {
		programDAO.saveOrUpdate(program);		
		return ListProgram.class;
	}

	/**
	 * Anhand des Inputs (Program null oder nicht) wird das Label für den
	 * Button bestimmt.
	 * 
	 * @return Die Bezeichnung für den Button.
	 */
	public String getSubmitLabelOfForm() {
		if (create == true) {
			return messages.get("submit-create-label");
		} else {
			return messages.get("submit-edit-label");
		}		
	}
}
