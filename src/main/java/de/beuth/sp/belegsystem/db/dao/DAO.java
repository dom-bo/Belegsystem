package de.beuth.sp.belegsystem.db.dao;

import java.io.Serializable;
import java.util.List;

import de.beuth.sp.belegsystem.db.manager.Manager;
import de.beuth.sp.belegsystem.lg.base.Persistent;
import de.beuth.sp.belegsystem.lg.base.PersistentImpl;

/**
 * Generisches DAO Interface für die Entity-Klassen (die {@link PersistentImpl}
 * erweitern, also das Interface {@link Persistent} implementieren).
 * Transaktionsbegrenzung nicht im DAO sondern darüberliegend (>>
 * Oberflächenschicht oder Manager!) commit vornehmen.
 * 
 * 
 * @param <T>
 *            Persistent
 */
public interface DAO<T extends Persistent> {

	/**
	 * Holt ein Objekt aus der DB über die id.
	 * 
	 * @param id
	 *            die id des zu holenden Objektes
	 * @return das Objekt
	 */
	<PK extends Serializable> T find(PK id);
	
	/**
	 * Gibt alle in der DB vorhandenen Objekte der jeweiligen Klasse zurück.
	 * 
	 * @return Liste mit allen Objekten der Klasse.
	 */
	List<T> getAll();	

	/**
	 * Speichert ein (neues) Objekt in der DB ab. <br/>
	 * <b>Achtung:</b> Führt den Commit nicht selber durch - dies in
	 * darüberliegender Schicht vornehmen die die gesamte Transaktion umfasst
	 * (d.h. über den jeweiligen {@link Manager} oder über die
	 * Oberflächenschicht).
	 * 
	 * @param objectToPersist
	 *            das Objekt das gespeichert werden soll
	 */
	void saveOrUpdate(T objectToPersist);

	/**
	 * Löscht ein Objekt über seine id aus der DB. <br/>
	 * <b>Achtung:</b> Führt den Commit nicht selber durch - dies in
	 * darüberliegender Schicht vornehmen die die gesamte Transaktion umfasst
	 * (d.h. über den jeweiligen {@link Manager} oder über die
	 * Oberflächenschicht).
	 * 
	 * @param id
	 *            die id des zu löschenden Objektes
	 */
	<PK extends Serializable> void delete(PK id);

	/**
	 * Löscht ein Objekt aus der DB. <br/>
	 * <b>Achtung:</b> Führt den Commit nicht selber durch - dies in
	 * darüberliegender Schicht vornehmen die die gesamte Transaktion umfasst
	 * (d.h. über den jeweiligen {@link Manager} oder über die
	 * Oberflächenschicht).
	 * 
	 * @param persistentObject
	 *            das zu löschende Objekt
	 */
	void delete(T persistentObject);

	/**
	 * Löscht eine Liste von Objekten. <br/>
	 * <b>Achtung:</b> Führt den Commit nicht selber durch - dies in
	 * darüberliegender Schicht vornehmen die die gesamte Transaktion umfasst
	 * (d.h. über den jeweiligen {@link Manager} oder über die
	 * Oberflächenschicht).
	 * 
	 * @param persistentObjects
	 *            die zu löschenden persitierten Objekte
	 */
	void deleteList(List<T> persistentObjects);

	/**
	 * Commitet eine Transaktion
	 */
	void commitTransaction();

	/**
	 * Rollt eine angefangene Transaktion zurück
	 */
	void rollbackTransaction();	

}
