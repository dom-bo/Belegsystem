package de.beuth.sp.belegsystem.lg;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import multex.MultexUtil;
import multex.extension.AnnotatedExc;
import multex.extension.ExcMessage;

import org.apache.tapestry5.beaneditor.Validate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import de.beuth.sp.belegsystem.Settings;
import de.beuth.sp.belegsystem.lg.base.PersistentImpl;
import de.beuth.sp.belegsystem.util.StringUtil;

/**
 * Ein User ist ein Benuter des Systems. Er kann mehrere Rollen aufweisen (Admin
 * / Instructor / Participant), die unterschiedliche Rechte aufweisen. Über
 * diese Klasse werden die Nutzerdaten erfasst (wie z.B. Logindaten).
 * 
 * 
 */
@Entity
public class User extends PersistentImpl {
	
	private static final long serialVersionUID = 6181040976370817030L;

	@ExcMessage("Dem User {0} ist bereits die Rolle '{1}' zugewiesen.")
	public static final class RoleDuplicateExc extends AnnotatedExc {
		private static final long serialVersionUID = 510361264190792477L;		
	}		

	@OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.EAGER)
	@Cascade({CascadeType.SAVE_UPDATE})
	private final Set<Role> roles = new HashSet<Role>();
	
	@Column(unique = true, nullable = false)
	private String username;
	
	private String password;
	
	private String firstname;
	
	private String lastname;
	
	@Column(unique = true)
	private String email;
	
	private String phone;

	public Set<Role> getRoles() {
		return Collections.unmodifiableSet(roles);
	}

	public boolean addRole(final Role role) throws RoleDuplicateExc {
		if (!roles.contains(role)) {
			for (final Role exisitingRole : this.roles) {
				if (exisitingRole.getClass().getName().equals(role.getClass().getName())) {
					throw MultexUtil.create(RoleDuplicateExc.class, this.getUsername(), exisitingRole.getClass().getSimpleName());
				}
			}
		}
		final boolean result = this.roles.add(role);
		if (!this.equals(role.getUser())) {
			role.setUser(this);
		}
		return result;
		
	}

	public boolean removeRole(final Role role) {
		return this.roles.remove(role);
	}
	
	public boolean removeRoleOfType(final Class<? extends Role> roleClass) {
		Iterator<Role> iterator = roles.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getClass().getName().equals(roleClass.getName())) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRoleOfType(Class<? extends Role> roleClass) {
		for (Role role : this.roles) {
			if (role.getClass().getName().equals(roleClass.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRoleOfSimpleType(String roleType) {
		for (Role role : this.roles) {
			if (role.getClass().getSimpleName().equals(roleType)) {
				return true;
			}
		}
		return false;
	}	
	   
	    
	public <R extends Role> R getRoleOfType(Class<R> roleClass) {
		for (Role role : getRoles()) {
			if (role.getClass().equals(roleClass)) {
				return roleClass.cast(role);
			}
		}
		return null;
	}

	public String getUsername() {
		return username;
	}

	@Validate(value = "required")
	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {		
		this.password = StringUtil.encodePassword(password, Settings.PASSWORD_ENCODING_ALGORITHM);
	}

	public String getFirstname() {
		return firstname;
	}

	@Validate(value = "required")
	public void setFirstname(final String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	@Validate(value = "required")
	public void setLastname(final String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	@Validate(value = "required, email")
	public void setEmail(final String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(final String phone) {
		this.phone = phone;
	}
	
	@Override
	public String toString() {
		return getUsername();
	}

}
