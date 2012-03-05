package de.beuth.sp.belegsystem.tapestry.pages.admin;

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

import de.beuth.sp.belegsystem.db.dao.AdminDAO;
import de.beuth.sp.belegsystem.db.dao.ProgramDAO;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Program;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse zum Editieren und Erstellen eines Admins. Nur für SuperAdmins erlaubt.
 * 
 * 
 */
@Restricted(allowedFor = Admin.class, onlyForSuperAdmin = true)
public class EditAdmin extends BasePageImpl {

	/**
	 * Wenn ein Admin editiert werden soll, wird dieser an die Page
	 * übergeben (wenn dieser null ist wird er in getAdmin erstellt ->
	 * neuer Admin).
	 */
	@PageActivationContext
	private Admin admin;

	/**
	 * DAO Klasse für die Persistenz.
	 */
	@Inject
	private AdminDAO adminDAO;

	/**
	 * DAO Klasse für die Programs. Benötigt zur Auswahl der Programs für die
	 * der Admin zuständig ist.
	 */
	@Inject
	private ProgramDAO programDAO;

	/**
	 * Lokale Variable für die Schleife der Programs für die der Admin zuständig ist in der TML-Datei.
	 */
	@Property
	@SuppressWarnings("unused")
	private Program program;

	/**
	 * Speichert die aktuell festgelegten Programs für die der Admin zuständig ist. Alle Programs in diesem Set werden ihm beim Speichern dann
	 * zugewiesen.
	 */
	@Persist
	@Property
	private Set<Program> administeredProgramsOfAdmin;

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
	 * Enthält die BeanEditForm von Tapestry für den Admin.
	 */
	@InjectComponent
	private BeanEditForm adminEditForm;

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
	 * Für die Oberfläche um festzustellen, ob ein Admin editiert oder neu
	 * erstellt wird (für die Beschriftung des Buttons).
	 */
	private boolean create = false;

	@InjectComponent
	private Zone programsZone;

	void setupRender() {
		administeredProgramsOfAdmin = new HashSet<Program>(admin.getAdministeredPrograms());
	}

	/**
	 * Wird beim aktivieren der Seite aufgerufen und prüft, ob ein Admin
	 * übergeben wurde.
	 */
	void onActivate() {
		create = admin == null;
	}

	public Admin getAdmin() throws IOException {
		if (admin == null) {
			admin = new Admin();
		}
		return admin;
	}

	/**
	 * Spezielle Validierung für das Passwort wird in der Validierungsmethode
	 * für das Formular vorgenommen.
	 */
	void onValidateFromAdminEditForm() {
		// Neuer Admin (version==null) und kein Passwort gesetzt?
		if (admin.getVersion() == null && passwordValue == null) {
			adminEditForm.recordError(password, messages.get("password-required"));
		} else if (passwordValue != null && !passwordValue.equals(passwordConfirmationValue)) {
			adminEditForm.recordError(password, messages.get("passwords-notequal"));
		}
	}

	/**
	 * Wenn das Formular erfolgreich geprüft wurde. Der Admin wird dann
	 * persistent gespeichert.
	 * 
	 * @return Objekt der nächsten Seite die aufgerufen werden soll
	 *         (ListAdmin).
	 */
	@CommitAfter
	Object onSuccessFromAdminEditForm() {
		// Falls ein neues Passwort vorliegt (Validierung war ja erfolgreich)
		if (passwordValue != null) {
			// ...wird es gesetzt.
			admin.getUser().setPassword(passwordValue);
		}

		// Programs synchronisieren:
		Set<Program> previousPrograms = new HashSet<Program>(admin.getAdministeredPrograms());
		for (Program program : previousPrograms) {
			if (!administeredProgramsOfAdmin.contains(program)) {
				admin.removeAdministeredProgram(program);
			}
		}
		for (Program program : administeredProgramsOfAdmin) {
			admin.addAdministeredPrograms(program);
		}

		// speichern!
		adminDAO.saveOrUpdate(admin);

		// und zur ListAdmin-Page navigieren
		return ListAdmin.class;
	}
	
	public boolean isSuperAdmin() {
		return admin.isSuperAdmin();
	}
	
	public void setSuperAdmin(boolean value) {
		admin.setSuperAdmin(value);
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
		administeredProgramsOfAdmin.add(program);
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
		administeredProgramsOfAdmin.remove(program);
		return programsZone.getBody();
	}
	
	/**
	 * Anhand des Inputs (Admin null oder nicht) wird das Label für den
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
