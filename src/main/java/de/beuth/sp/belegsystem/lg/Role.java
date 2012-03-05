package de.beuth.sp.belegsystem.lg;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import de.beuth.sp.belegsystem.lg.User.RoleDuplicateExc;
import de.beuth.sp.belegsystem.lg.base.PersistentImpl;

/**
 * 
 * 
 * Klasse zur Vorlage von verschiedenen Rollen im System. Die Klassen zu
 * Dozenten, Administratoren und Studenten erben von dieser Klasse, um so einem
 * Nutzer mehrere Rollen im System zu ermöglichen. Die Klasse Role selber ist
 * als abstrakte Klasse angelegt, so dass es keine Instanz von ihr geben kann,
 * nur von den Subklassen.
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Role extends PersistentImpl {

	private static final long serialVersionUID = 1838291739284564452L;

	@ManyToOne
	@Cascade({ CascadeType.SAVE_UPDATE })
	private User user;

	public User getUser() {
		return user;
	}

	/**
	 * @throws RoleDuplicateExc
	 *             Exception wird geworfen, wenn eine dem Nutzer bereits
	 *             zugewiese Role nochmals zugeteilt werden soll.
	 */
	public void setUser(final User user) throws RoleDuplicateExc {
		if (!user.getRoles().contains(this)) {
			user.addRole(this);
		}
		this.user = user;
	}

	public String getRoleName() {
		return this.getClass().getSimpleName();
	}

	public String toString() {
		return this.getUser() != null ? this.getUser().getFirstname() + " "
				+ this.getUser().getLastname() : super.toString();
	}

}
