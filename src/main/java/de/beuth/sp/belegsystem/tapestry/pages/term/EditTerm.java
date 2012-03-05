package de.beuth.sp.belegsystem.tapestry.pages.term;

import multex.MultexUtil;
import multex.extension.AnnotatedExc;
import multex.extension.ExcMessage;

import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.db.dao.TermDAO;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Term;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse für die Bearbeitung eines Semesters.
 * 
 * 
 */
@Restricted(allowedFor = { Admin.class })
public class EditTerm extends BasePageImpl {

	/**
	 * DAO Klasse des Semesters
	 */
	@Inject
	private TermDAO termDAO;

	/**
	 * Attribute, in dem das aktuelle Semester
	 * gespeichert wird
	 */
	@PageActivationContext
	@Property
	private Term term;
	
	@Inject
	private Messages messages;
	
	private boolean create = false; // für submit-label
	
	void onActivate() {
		create = term==null;
	}
	
	/**
	 * Prüft, ob alle Bedingungen zum Anlegen oder Speichern
	 * eines Terms erfüllt sind. 
	 * Es wird geprüft, ob für den angegebenen Zeitraum schon
	 * ein Semester vorhanden ist
	 * 
	 * @throws TermAlreadyExistsExc
	 * 					Wird ausgelöst, wenn ein Zeitraum ausgewählt wurde,
	 * 					für den schon ein Semester vorhanden ist.
	 */
	void onValidate() throws TermAlreadyExistsExc {
		// Prüft, ob der Term für den Zeitraum schon existiert
		final Term alreadyExistingTerm = termDAO.existsTerm(term.getStartDate(), term.getEndDate());
		if (alreadyExistingTerm != null && !term.equals(alreadyExistingTerm)) {
			throw MultexUtil.create(TermAlreadyExistsExc.class, 
					term.getStartDate().getTime(), term.getEndDate().getTime(), 
					alreadyExistingTerm.getStartDate().getTime(), alreadyExistingTerm.getEndDate().getTime());
		}
	}

	/**
	 * 
	 * @return
	 * @throws TermAlreadyExistsExc
	 */
	@CommitAfter
	Object onSuccess() throws TermAlreadyExistsExc {
		termDAO.saveOrUpdate(term);
		return ListTerm.class;
	}

	@ExcMessage("Semester mit Startdatum {0,date,short} und Enddatum {1,date,short} " +
							   "kann nicht angelegt werden da bereits ein Semester mit Startdatum " +
							   "{2,date,short} und Enddatum {3,date,short} vorhanden ist")
	public static final class TermAlreadyExistsExc extends AnnotatedExc {
		private static final long serialVersionUID = -3664259512716676890L;
	}
	
	public String getSubmitLabelOfForm() {
		if (create == true) {
			return messages.get("submit-create-label");
		} else {
			return messages.get("submit-edit-label");
		}		
	}
}
