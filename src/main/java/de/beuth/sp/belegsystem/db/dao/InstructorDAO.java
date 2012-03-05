package de.beuth.sp.belegsystem.db.dao;

import java.util.List;

import de.beuth.sp.belegsystem.lg.Instructor;

/**
 * 
 * DAO-Interface für die {@link Instructor}-Klasse. Erweitert das generische {@link DAO} um
 * spezifische Methoden für {@link Instructor}s.
 * 
 * 
 */
public interface InstructorDAO extends DAO<Instructor> {

	/**
	 * Sucht nach einem Instructor über den Namen. <br/>
	 * Es wird in der Datenbank alle Instructors gesucht (und zurückgegeben),
	 * deren Vor-, Nach- oder Username so <b>anfangen</b> wie der übergebene
	 * Parameter.
	 * 
	 * @param name
	 *            Name nach dem gesucht werden soll.
	 * @return Liste aller Instructors, welche den Suchparameter (als Vor-/Nach-/Username) enthalten.
	 */
	List<Instructor> searchByName(String name);
}