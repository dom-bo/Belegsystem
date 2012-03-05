package de.beuth.sp.belegsystem.db.manager;

import de.beuth.sp.belegsystem.db.dao.DAO;
import de.beuth.sp.belegsystem.lg.base.Persistent;

/**
 * Generisches Interface für Manager-Klassen die für komplexere Transaktionen
 * und spezielle Aufbereitung von Daten zuständig sind.
 * <p>
 * Jede Fachklasse bei der entweder...
 * <ul>
 * <li>komplexe Transaktionen durchgeführt werden sollten</li> 
 * <li>bei der Daten speziell aufbereitet werden sollen (z.B. bestimmt sortiert)
 * </li>
 * </ul>
 * sollte ein eigener Manager zugeordnet sein.
 * </p>
 * 
 * 
 * @param <T>
 *            Die Fachklasse für die der Manager zuständig ist.
 */
public interface Manager<T extends Persistent> {
	
	DAO<T> getDAO();

	/**
	 * Commitet eine Transaktion
	 */
	void commitTransaction();

	/**
	 * Rollt eine angefangene Transaktion zurück
	 */
	void rollbackTransaction();

}