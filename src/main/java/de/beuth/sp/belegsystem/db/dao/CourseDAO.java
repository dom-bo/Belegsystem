/**
 * 
 */
package de.beuth.sp.belegsystem.db.dao;

import java.util.List;

import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Participant;
import de.beuth.sp.belegsystem.lg.Program;
import de.beuth.sp.belegsystem.lg.Term;
import de.beuth.sp.belegsystem.lg.TimeSlot;

/**
 * 
 * DAO-Interface für die {@link Course}-Klasse. Erweitert das generische {@link DAO} um
 * spezifische Methoden für {@link Course}s.
 * 
 * 
 */
public interface CourseDAO extends DAO<Course> {

	/**
	 * 
	 * Diese Funktion sucht alle Kurse heraus, bei denen die übergebenen
	 * Suchkriterien übereinstimmen und gibt sie als Collection zurück. Die
	 * einzelnen Parameter können auch null sein, dann werden sie bei der Suche
	 * nicht herangezogen.
	 * 
	 * @param program
	 *            das "Programm", d.h. das Studienfach
	 * @param instructor
	 *            ein "Instructor", d.h. der Dozent
	 * @param level
	 *            das vorgesehene Semester im Studienfach
	 * @param studygroup
	 *            der Zug im Studienfach
	 * @param term
	 *            das konkrete Semester in dem der Kurs stattfindet
	 * @param search
	 *            ein zu suchender Kursname. Hierbei wird die Suche per
	 *            Teilstring (er muss aber damit anfangen, also Suche gemäß
	 *            "name*") unterstützt, Groß/Kleinschreibung spielt keine Rolle.
	 *            Die Übereinstimmungsüberprüfung setzt den übergebenen Namen
	 *            als anfänglichen String voraus um irreführende Ergebnisse zu
	 *            vermeiden (z.B. Falls man nach "The" eingibt werden Kurse wie
	 *            "Theoretische Informatik" gefunden und nicht "Mathematik").
	 * 
	 * @return eine Liste von Kursen
	 * 
		 * 
	 */
	List<Course> getCourses(final Program program, final Instructor instructor,
			final Integer level, final Integer studygroup, final Term term,
			final String search, final TimeSlot timeSlot);

	
	/**
	 * 
	 * Entfernt einen {@link Participant} aus einem {@link Course} in der Datenbank / persistenten Schicht.
	 * 
	 * @param course der {@link Course} aus dem der {@link Participant} gelöscht werden soll
	 * @param participant der {@link Participant} der aus dem {@link Course} gelöscht werden soll
	 * 
		 */
	void removeParticipantFromCourse(Course course, Participant participant);	
	
	/**
	 * 
	 * Löscht alle Participamnts eines Course in der Datenbank / persistenten Schicht.
	 * 
	 * @param course der {@link Course} dessen {@link Participant}s gelöscht werden sollen
	 * 
		 */
	void clearParticipants(Course course);

}
