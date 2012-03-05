package de.beuth.sp.belegsystem.tapestry.pages.participant;

import java.io.IOException;
import java.util.List;

import org.apache.tapestry5.Field;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.db.dao.ParticipantDAO;
import de.beuth.sp.belegsystem.db.dao.ProgramDAO;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Participant;
import de.beuth.sp.belegsystem.lg.Program;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.pages.Index;
import de.beuth.sp.belegsystem.tapestry.services.AccessFilter;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse zum Editieren und Erstellen eines Participant.
 * 
 * 
 */
@Restricted(allowedFor = Admin.class, onlyIfAssociatedToProgram = true)
public class EditParticipant extends BasePageImpl {

	/**
	 * Wenn ein Participant editiert werden soll, wird dieser an die Page
	 * übergeben (wenn dieser null ist wird er in getParticipant erstellt ->
	 * neuer Instructor).
	 */
	@PageActivationContext
	private Participant participant;

	/**
	 * DAO Klasse für die Persistenz.
	 */
	@Inject
	private ParticipantDAO participantDAO;

	/**
	 * DAO Klasse für die Persistenz.
	 */
	@Inject
	private ProgramDAO programDAO;

	/**
	 * Speichert den Wert für das Passwort.
	 */
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String passwordValue;

	/**
	 * Speichert den Wert für das Passwort.
	 */
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String passwordConfirmationValue;

	/**
	 * Enthält die BeanEditForm von Tapestry für den Participant.
	 */
	@InjectComponent
	private BeanEditForm participantEditForm;

	/**
	 * Passwortfeld-Komponente von Tapestry.
	 */
	@InjectComponent
	private Field password;

	/**
	 * Hilfsklasse für den Zugriff auf die lokalisierten Werte.
	 */
	@Inject
	private Messages messages;

	/**
	 * Für die Oberfläche um festzustellen, ob ein Participant editiert oder neu
	 * erstellt wird (für die Beschriftung des Buttons).
	 */
	private boolean create = false; // für submit-label

	/**
	 * Wird beim aktivieren der Seite aufgerufen und prüft, ob ein Participant
	 * übergeben wurde.
	 */
	void onActivate() {
		create = participant == null;
	}

	public Participant getParticipant() throws IOException {
		if (participant == null) {
			participant = new Participant();
		}
		return participant;
	}

	public List<Program> getAllPrograms() {
		return programDAO.getAll();
	}

	/**
	 * Spezielle Validierung für das Passwort wird in der Validierungsmethode
	 * für das Formular vorgenommen.
	 */
	void onValidateFromParticipantEditForm() {
		// Neuer Participant (version==null) und kein Passwort gesetzt?
		if (participant.getVersion() == null && passwordValue == null) {
			participantEditForm.recordError(password,
					messages.get("password-required"));
		} else if (passwordValue != null
				&& !passwordValue.equals(passwordConfirmationValue)) {
			participantEditForm.recordError(password,
					messages.get("passwords-notequal"));
		}
	}

	/**
	 * Wenn das Formular erfolgreich geprüft wurde. Der Participant wird dann
	 * peristent gespeichert.
	 * 
	 * @return Objekt der nächsten Seite die aufgerufen werden soll
	 *         (ListParticipant oder Index).
	 */
	@CommitAfter
	Object onSuccessFromParticipantEditForm() {
		// Falls ein neues Passwort vorliegt (Validierung war ja erfolgreich)
		if (passwordValue != null) {
			// ...wird es gesetzt.
			participant.getUser().setPassword(passwordValue);
		}
		participantDAO.saveOrUpdate(participant);
		if (AccessFilter.checkAccess(getLoggedInUser(),
				ListParticipant.class.getAnnotation(Restricted.class))) {
			return ListParticipant.class;
		} else {
			return Index.class;
		}
	}

	/**
	 * Anhand des Inputs (Instructor null oder nicht) wird das Label für den
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
