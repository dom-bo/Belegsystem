package de.beuth.sp.belegsystem.tapestry.pages;

import java.util.List;

import org.apache.tapestry5.Field;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;

import de.beuth.sp.belegsystem.db.dao.UserDAO;
import de.beuth.sp.belegsystem.lg.User;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;

/**
 * Page-Klasse für das Profil eines {@link User}s.
 * 
 * 
 */
public class Profile extends BasePageImpl {

	/**
	 * Benutzer für den das Profil erstellt werden soll.
	 */
	@Property
	private User user;

	/**
	 * DAO Klasse für die Persistenz.
	 */
	@Inject
	private UserDAO userDAO;

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
	 * Enthält die BeanEditForm von Tapestry für den User.
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
	 * Parameter des Kontext (übergibt die vorherige Seite für ein Redirect)
	 */
	private List<String> contextParameters;

	/**
	 * Für das Redirect nach dem Bearbeiten des Profils (von Tapestry).
	 */
	@Inject
	private PageRenderLinkSource renderLinkSource;

	public void onActivate() {
		user = this.getLoggedInUser();
	}

	/**
	 * Wird beim aktivieren der Seite ausgeführt. Speichert die Parameter.
	 * 
	 * @param contextParameters
	 */
	public void onActivate(final List<String> contextParameters) {
		this.contextParameters = contextParameters;
	}

	public List<String> onPassivate() {
		return contextParameters;
	}

	/**
	 * Spezielle Validierung für das Passwort wird in der Validierungsmethode
	 * für das Formular vorgenommen.
	 */
	void onValidateFromUserEditForm() {
		if (passwordValue != null
				&& !passwordValue.equals(passwordConfirmationValue)) {
			userEditForm.recordError(password,
					messages.get("passwords-notequal"));
		}
	}

	@CommitAfter
	Object onSuccessFromUserEditForm() {
		// Falls ein neues Passwort vorliegt (Validierung war ja erfolgreich)
		if (passwordValue != null) {
			// ...wird es gesetzt.
			user.setPassword(passwordValue);
		}
		userDAO.saveOrUpdate(user);

		// ...und redirecten
		Link redirectTo = renderLinkSource.createPageRenderLink(Index.class);

		if (contextParameters != null && contextParameters.size() > 0) {
			Object[] parameters = new Object[contextParameters.size() - 1];
			for (int i = 0; i < parameters.length; i++) {
				parameters[i] = contextParameters.get(i + 1);
			}
			redirectTo = renderLinkSource.createPageRenderLinkWithContext(
					contextParameters.get(0), parameters);
		}
		return redirectTo;
	}

}
