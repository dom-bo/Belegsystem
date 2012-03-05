package de.beuth.sp.belegsystem.tapestry.pages.instructor;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.Field;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.ActionLink;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.db.dao.InstructorDAO;
import de.beuth.sp.belegsystem.db.dao.ProgramDAO;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Program;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse zum Editieren und Erstellen eines Instructors.
 * 
 * 
 */
@Restricted(allowedFor = Admin.class, onlyIfAssociatedToProgram = true)
public class EditInstructor extends BasePageImpl {

	/**
	 * Wenn ein Instructor editiert werden soll, wird dieser an die Page
	 * übergeben (wenn dieser null ist wird er in getInstructor erstellt ->
	 * neuer Instructor).
	 */
	@PageActivationContext
	private Instructor instructor;

	/**
	 * DAO Klasse für die Persistenz.
	 */
	@Inject
	private InstructorDAO instructorDAO;

	/**
	 * DAO Klasse für die Programs. Benötigt zur Auswahl der Programs für die
	 * der Instructor Kurse geben soll.
	 */
	@Inject
	private ProgramDAO programDAO;

	/**
	 * Lokale Variable für die Schleife der Programs für die der Instructor Kurse gibt in der TML-Datei.
	 */
	@Property
	@SuppressWarnings("unused")
	private Program program;

	/**
	 * Speichert die aktuell festgelegten Programs für die der Instrcutor Kurse
	 * geben soll. Alle Programs in diesem Set werden ihm beim Speichern dann
	 * zugewiesen.
	 */
	@Persist
	@Property
	private Set<Program> programsOfInstructor;

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
	 * Enthält die BeanEditForm von Tapestry für den Instructor.
	 */
	@InjectComponent
	private BeanEditForm instructorEditForm;

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
	 * Für die Oberfläche um festzustellen, ob ein Instructor editiert oder neu
	 * erstellt wird (für die Beschriftung des Buttons).
	 */
	private boolean create = false;

	@InjectComponent
	private Zone programsZone;

	void setupRender() {
		programsOfInstructor = new HashSet<Program>(instructor.getPrograms());
	}

	/**
	 * Wird beim aktivieren der Seite aufgerufen und prüft, ob ein Instructor
	 * übergeben wurde.
	 */
	void onActivate() {
		create = instructor == null;
	}

	public Instructor getInstructor() throws IOException {
		if (instructor == null) {
			instructor = new Instructor();
		}
		return instructor;
	}

	/**
	 * Spezielle Validierung für das Passwort wird in der Validierungsmethode
	 * für das Formular vorgenommen.
	 */
	void onValidateFromInstructorEditForm() {
		// Neuer Instructor (version==null) und kein Passwort gesetzt?
		if (instructor.getVersion() == null && passwordValue == null) {
			instructorEditForm.recordError(password, messages.get("password-required"));
		} else if (passwordValue != null && !passwordValue.equals(passwordConfirmationValue)) {
			instructorEditForm.recordError(password, messages.get("passwords-notequal"));
		}
	}

	/**
	 * Wenn das Formular erfolgreich geprüft wurde. Der Instructor wird dann
	 * peristent gespeichert.
	 * 
	 * @return Objekt der nächsten Seite die aufgerufen werden soll
	 *         (ListInstructor).
	 */
	@CommitAfter
	Object onSuccessFromInstructorEditForm() {
		// Falls ein neues Passwort vorliegt (Validierung war ja erfolgreich)
		if (passwordValue != null) {
			// ...wird es gesetzt.
			instructor.getUser().setPassword(passwordValue);
		}

		// Programs synchronisieren:
		Set<Program> previousPrograms = new HashSet<Program>(instructor.getPrograms());
		for (Program program : previousPrograms) {
			if (!programsOfInstructor.contains(program)) {
				instructor.removeProgram(program);
			}
		}
		for (Program program : programsOfInstructor) {
			instructor.addProgram(program);
		}

		// speichern!
		instructorDAO.saveOrUpdate(instructor);

		// und zur ListInstrcutor-Page navigieren
		return ListInstructor.class;
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

	/**
	 * Gibt alle Studiengänge für die GUI zurück.
	 * 
	 */
	public List<Program> getAllPrograms() {
		return programDAO.getAll();
	}

	/**
	 * Wird aufgerufen wenn ein Program in der Selectbox selektiert wurde
	 * 
	 * @param program
	 *            das selektierte Program
	 * @return den Body der {@link #programsZone} der damit neu gerendert wird
	 */
	Object onValueChangedFromSelectAddProgram(Program program) {
		programsOfInstructor.add(program);
		return programsZone.getBody();
	}

	/**
	 * Wird aufgerufen wenn auf den removeProgram-{@link ActionLink} geklickt
	 * wird.
	 * 
	 * @param program
	 *            das Program das entfernt werden soll
	 * @return den Body der {@link #programsZone} der damit neu gerendert wird
	 */
	Object onActionFromRemoveProgram(Program program) {
		programsOfInstructor.remove(program);
		return programsZone.getBody();
	}

}
