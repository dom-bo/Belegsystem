package de.beuth.sp.belegsystem.db.dao;

import java.util.List;

import de.beuth.sp.belegsystem.lg.Participant;

/**
 * 
 * DAO-Interface für die {@link Participant}-Klasse. Erweitert das generische {@link DAO} um
 * spezifische Methoden für {@link Participant}s.
 * 
 * 
 */
public interface ParticipantDAO extends DAO<Participant> {
	
	/**
	 * Sucht nach einem Participant über den Namen. <br/>
	 * Es wird in der Datenbank alle Participants gesucht (und zurückgegeben),
	 * deren Vor-, Nach- oder Username so <b>anfangen</b> wie der übergebene
	 * Parameter.
	 * 
	 * @param name
	 *            Name nach dem gesucht werden soll.
	 * @return Liste aller Participants, welche den Suchparameter (als Vor-/Nach-/Username) enthalten.
	 */
	List<Participant> searchByName(String name);

}