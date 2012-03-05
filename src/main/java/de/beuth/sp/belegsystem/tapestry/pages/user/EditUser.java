package de.beuth.sp.belegsystem.tapestry.pages.user;

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

import de.beuth.sp.belegsystem.db.dao.UserDAO;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Participant;
import de.beuth.sp.belegsystem.lg.User;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse zum editieren und erstellen eines Users. Diese Klasse ist
 * nur für Admin erlaubt.
 * 
 * 
 */
@Restricted(allowedFor=Admin.class, onlyIfAssociatedToProgram = true)
public class EditUser extends BasePageImpl {
	
	/**
	 * Wenn ein User editiert werden soll, wird dieser an die Page
	 * übergeben (wenn dieser null ist wird er in getUser erstellt als
	 * neuer User)
	 */
	@PageActivationContext
	@Property
	private User user;
	
	/**
	 * DAO Klasse für die Persistenz.
	 */
	@Inject
	private UserDAO userDAO;
	
	/**
	 * Speichert den Wert für das Passwort
	 */
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String passwordValue;

	/**
	 * Speichert den Wert für das PasswortConfirm
	 */
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String passwordConfirmationValue;
	
	/**
	 * Enthält die BeanEditForm von Tapestry für den User
	 */
	@InjectComponent
	private BeanEditForm userEditForm;
	
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
	 * Für die Oberfläche um festzustellen, ob ein User editiert oder neu
	 * erstellt wird (für die Beschriftung des Buttons).
	 */
	private boolean create = false;
	
	/**
	 * Wird beim aktivieren der Seite aufgerufen und prüft, ob ein User
	 * übergeben wurde.
	 */
	void onActivate() {
		create = user==null;
	}
	
	/**
	 * Spezielle Validierung für das Passwort wird in der Validierungsmethode 
	 * für das Formular vorgenommen.
	 */
	void onValidateFromUserEditForm() {
		//Neuer User (version==null) und kein Passwort gesetzt?
		if (user.getVersion()==null && passwordValue==null) {
			userEditForm.recordError(password, messages.get("password-required"));
		} else if (passwordValue!=null && !passwordValue.equals(passwordConfirmationValue)) {
			userEditForm.recordError(password, messages.get("passwords-notequal"));
		}
	}

	/**
	 * Wenn das Formular erfolgreich geprüft wurde. Der user wird dann
	 * peristent gespeichert
	 *
	 * @return Objekt der nächsten Seite die aufgerufen werden soll
	 *         (ListUser)
	 */
	@CommitAfter
	Object onSuccessFromUserEditForm() {		
		//Falls ein neues Passwort vorliegt (Validierung war ja erfolgreich)
		if (passwordValue!=null) {
			//...wird es gesetzt.
			user.setPassword(passwordValue);
		}
		
		userDAO.saveOrUpdate(user);		
		return ListUser.class;
	}

	/**
	 * Methode zum Prüfen, ob ein user die Rolle Student besitzt
	 * 
	 * @return true wenn er Student ist
	 */
	public boolean isParticipantRole() {
		return user.hasRoleOfType(Participant.class);
	}
	
	/**
	 * Methode zum Setzen/Entfernen der Student-Rolle bei dem User
	 * 
	 * @param participantRole
	 * 					die Prüfung ob der User Student ist
	 */
	public void setParticipantRole(boolean participantRole) {
		if (!participantRole) {
			user.removeRoleOfType(Participant.class);			
		} else if (!user.hasRoleOfType(Participant.class)) {
			user.addRole(new Participant());
		}
	}
	
	/**
	 * Methode zum Prüfen, ob ein user die Rolle Dozent besitzt
	 * 
	 * @return true wenn er Dozent ist
	 */
	public boolean isInstructorRole() {
		return user.hasRoleOfType(Instructor.class);
	}
	
	/**
	 * Methode zum Editieren die Dozent-Rolle von einem User
	 * 
	 * @param instructorRole
	 * 					die Prüfung ob der User Dozent ist
	 */
	public void setInstructorRole(boolean instructorRole) {
		if (!instructorRole) {
			user.removeRoleOfType(Instructor.class);			
		} else if (!user.hasRoleOfType(Instructor.class)) {
			user.addRole(new Instructor());
		}
	}
	
	/**
	 * Methode zum Prüfen, ob ein user die Rolle Admin besitzt
	 * 
	 * @return true wenn er Admin ist
	 */
	public boolean isAdminRole() {
		return user.hasRoleOfType(Admin.class);
	}
	
	/**
	 * Methode zum Editieren die Admin-Rolle von einem User
	 * 
	 * @param adminRole
	 * 				die Prüfung ob der User Admin ist
	 */
	public void setAdminRole(boolean adminRole) {
		if (!adminRole) {
			user.removeRoleOfType(Admin.class);			
		} else if (!user.hasRoleOfType(Admin.class)) {
			user.addRole(new Admin());
		}
	}	
	
	/**
	 * Anhand des Inputs (User null oder nicht) wird das Label für den
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
