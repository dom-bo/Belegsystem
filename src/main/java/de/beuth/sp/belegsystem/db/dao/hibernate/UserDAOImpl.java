package de.beuth.sp.belegsystem.db.dao.hibernate;

import java.util.List;

import org.hibernate.Query;

import de.beuth.sp.belegsystem.Settings;
import de.beuth.sp.belegsystem.db.dao.UserDAO;
import de.beuth.sp.belegsystem.lg.User;
import de.beuth.sp.belegsystem.util.StringUtil;

/**
 * 
 * Implementation des UserDAO-Interfaces, erweitert generische DAOImpl-Klasse
 * (typisiert auf User) und implementiert die zusätzlichen Methoden des
 * UserDAO-Interface.
 * 
 * 
 */
public class UserDAOImpl extends DAOImpl<User> implements UserDAO {

	@Override
	public User getUserByUsernameAndEncodedPassword(final String username, final String nonEncodedPassword) {		
		//Passwort kodieren
		final String encodedPassword = StringUtil.encodePassword(nonEncodedPassword, Settings.PASSWORD_ENCODING_ALGORITHM);
		Query query = getSession().createQuery(
				"FROM " + User.class.getName() + " " +  
				"WHERE username like :name " + 
				"AND password like :password").setParameter("name", username).setParameter("password", encodedPassword);
		List<?> userlist = query.list();
		if (!userlist.isEmpty()) {
			return (User) userlist.get(0);
		}
		return null;
	}
	
	@Override
	public User getUserByUsername(final String username) {		
		Query query = getSession().createQuery(
				"FROM " + User.class.getName() + " " +  
				"WHERE username like :name").setParameter("name", username);
		List<?> userlist = query.list();
		if (!userlist.isEmpty()) {
			return (User) userlist.get(0);
		}
		return null;
	}
		
}
