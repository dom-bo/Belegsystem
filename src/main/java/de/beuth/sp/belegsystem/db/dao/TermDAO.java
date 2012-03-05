package de.beuth.sp.belegsystem.db.dao;

import java.util.Calendar;

import de.beuth.sp.belegsystem.lg.Term;

/**
 * 
 * DAO-Interface für die {@link Term}-Klasse. Erweitert das generische {@link DAO} um
 * spezifische Methoden für {@link Term}s.
 * 
 * 
 */
public interface TermDAO extends DAO<Term> {
	/**
	 * Prüft, ob ein Term für ein Zeitraum(Start, Ende) schon
	 * existiert.
	 * 
	 * @param startDatE
	 * @param endDate
	 * @return
	 */
	public Term existsTerm(Calendar startDate, Calendar endDate);

	/**
	 * Gibt für einen Zeitpunkt einen Term zurück
	 * 
	 * @param cal
	 * 		Der Zeitpunkt, an dem geschaut wird, ob ein Term vorhanden ist
	 * 
	 * @return Rückgabewert ist ein Term, sofern einer gefunden wurde. 
	 * 			Wenn nicht, wird null zurückgegeben
	 */
	public Term getTermAtDate(Calendar cal);
}
