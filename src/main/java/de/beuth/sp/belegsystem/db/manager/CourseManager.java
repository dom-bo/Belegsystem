package de.beuth.sp.belegsystem.db.manager;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;

import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.lg.Participant;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Manager für {@link Course}s. Erlaubt die komplexe Transaktion des Löschens
 * eines Kurses.
 * 
 * 
 */
public interface CourseManager extends Manager<Course> {

	/**
	 * Löscht einen Kurs. Ist nur für {@link Admin}s erlaubt.
	 * <p>
	 * <b>ACHTUNG: </b>Führt Commit nicht selber durch, dafür entweder Methode
	 * {@link #deleteAndCommit(Course)} benutzen oder im Anschluss an die
	 * Transaktion {@link #commitTransaction()} aufrufen.
	 * </p>
	 * 
	 * @param course
	 *            der zu löschende Kurs
	 */
	@Restricted(allowedFor = Admin.class)	
	void delete(Course course);

	/**
	 * Löscht einen Kurs. Ist nur für {@link Admin}s erlaubt.
	 * <p>
	 * <b>ACHTUNG: </b>Führt Commit selber durch! Falls Commit nicht gleich im Anschluss an den
	 * Aufruf dieser Methode durchgeführt werdne soll Methode
	 * {@link #delete(Course)} benutzen und im Anschluss an die komplette
	 * Transaktion {@link #commitTransaction()} aufrufen.
	 * </p>
	 * 
	 * @param course
	 *            der zu löschende Kurs
	 */
	@CommitAfter
	@Restricted(allowedFor = Admin.class)
	void deleteAndCommit(Course course);
	
	/**
	 * Entfernt einen {@link Participant} aus einem {@link Course} - unabhängig von der Belegzeit! Ist nur für {@link Admin}s erlaubt.
	 * <p>
	 * <b>ACHTUNG: </b>Führt Commit selber durch! Falls Commit nicht gleich im Anschluss an den
	 * Aufruf dieser Methode durchgeführt werdne soll Methode
	 * {@link #delete(Course)} benutzen und im Anschluss an die komplette
	 * Transaktion {@link #commitTransaction()} aufrufen.
	 * </p>
	 * 
	 * @param course der {@link Course} aus dem der {@link Participant} gelöscht werden soll
	 * @param participant der {@link Participant} der aus dem {@link Course} gelöscht werden soll
	 */
	@Restricted(allowedFor = Admin.class)	
	void removeParticipantFromCourse(Course course, Participant participant);	
	
	/**
	 * Entfernt einen {@link Participant} aus einem {@link Course} - unabhängig von der Belegzeit! Ist nur für {@link Admin}s erlaubt.
	 * <p>
	 * <b>ACHTUNG: </b>Führt Commit selber durch! Falls Commit nicht gleich im Anschluss an den
	 * Aufruf dieser Methode durchgeführt werdne soll Methode
	 * {@link #delete(Course)} benutzen und im Anschluss an die komplette
	 * Transaktion {@link #commitTransaction()} aufrufen.
	 * </p>
	 * 
	 * @param course der {@link Course} aus dem der {@link Participant} gelöscht werden soll
	 * @param participant der {@link Participant} der aus dem {@link Course} gelöscht werden soll
	 */
	@CommitAfter
	@Restricted(allowedFor = Admin.class)	
	void removeParticipantFromCourseAndCommit(Course course, Participant participant);

}