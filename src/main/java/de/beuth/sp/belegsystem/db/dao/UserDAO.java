package de.beuth.sp.belegsystem.db.dao;

import de.beuth.sp.belegsystem.lg.User;

/**
 * 
 * DAO-Interface für die {@link User}-Klasse. Erweitert das generische {@link DAO} um
 * spezifische Methoden für {@link User}s.
 * 
 * 
 */
public interface UserDAO extends DAO<User> {

	/**
	 * Holt User aus DB bei dem username und das (enkodierte) Passwort den
	 * übergebenen Parametern entspricht.
	 * 
	 * @param username
	 * @param password
	 * @return User falls einer gefunden wurde, ansonsten null
	 */
	public User getUserByUsernameAndEncodedPassword(String username, String password);

	/**
	 * Holt User aus DB bei dem der username dem übergebenen Parameter
	 * entspricht.
	 * 
	 * @param username
	 * @return <b>User</b> falls einer gefunden wurde, ansonsten <b>null</b>
	 */
	public User getUserByUsername(String username);

}