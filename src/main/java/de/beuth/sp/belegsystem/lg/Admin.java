package de.beuth.sp.belegsystem.lg;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import multex.MultexUtil;
import multex.extension.AnnotatedExc;
import multex.extension.ExcMessage;
import de.beuth.sp.belegsystem.exceptions.ExcUtil.StartDateAfterEndDateExc;
import de.beuth.sp.belegsystem.lg.Course.InstructorNotFoundExc;
import de.beuth.sp.belegsystem.lg.Course.TooMuchInstructorsForCourseExc;
import de.beuth.sp.belegsystem.lg.Program.StillRunningCourseWithParticipantsDeletionExc;
import de.beuth.sp.belegsystem.tapestry.services.AccessFilter;
import de.beuth.sp.belegsystem.tapestry.services.AppModule;

/**
 * Rolle des Administrators. Der Administrator (z.B. Mitglied der Verwaltung)
 * besitzt alle Verwaltungsmethoden um grundlegende Daten des Stundenplans dem
 * System hinzuzufügen. Die Klasse wird auch für etwaige
 * Sicherheitsüberprüfungen an den jeweiligen Stellen benötigt.
 * 
 * Einige Methoden dieser Klasse (z.B. {@link #createCourse} oder
 * {@link #createProgram}) werden momentan nicht benötigt da sie anderweitig
 * realisiert wurden. Nämlich direkt über die Oberfläche inklusive zusätzlicher
 * aspektorientierter Sicherheitsüberprüfung mittels unseres
 * {@link AccessFilter} bzw. {@link AppModule#adviseSecurityCheck} bei
 * Speicherung/Verarbeitung soweit benötigt. Für den möglichen zusätzlichen
 * Anwendungsfall der Benutzung hierüber bleiben die Methoden aber erhalten.
 * 
 */
@Entity
public class Admin extends Role {

	private static final long serialVersionUID = -7243964367654380915L;

	@ExcMessage("Lehrkraft {0} kann nicht als Lehrkraft bei dem Kurs {1} gesetzt werden da dem Kurs bereits die Maximalanzahl an Lehrkräften zugewiesen ist.")
	public static final class MaximumReachedExc extends AnnotatedExc {
		private static final long serialVersionUID = -548694515156456L;
	}

	@ExcMessage("Studiengang {0} wird nicht von Admin {1} betreut.")
	public static final class ProgramSecurityExc extends AnnotatedExc {
		private static final long serialVersionUID = 8693705307924220905L;
	}
	
	/**
	 * Ein SuperAdmin darf andere Admins editieren (und damit auch die
	 * administeredPrograms eines Admin verändern)
	 */
	private boolean superAdmin;

	/**
	 * Die {@link Program}s die der Administrator verwaltet.
	 */
	@ManyToMany
	private final Set<Program> administeredPrograms = new HashSet<Program>();
	
	public boolean isSuperAdmin() {
		return superAdmin;
	}
	
	public void setSuperAdmin(boolean superAdmin) {
		this.superAdmin = superAdmin;
	}

	public Set<Program> getAdministeredPrograms() {
		return Collections.unmodifiableSet(administeredPrograms);
	}

	public boolean addAdministeredPrograms(Program program) {
		if (program == null)
			throw new IllegalArgumentException();
		return administeredPrograms.add(program);
	}

	public boolean removeAdministeredProgram(Program program) {
		if (program == null)
			throw new IllegalArgumentException();
		return administeredPrograms.remove(program);
	}

	/**
	 * Erstellt einen neuen Studiengang. Der Administrator hat die Möglichkeit
	 * einen neuen Studiengang dem System hinzuzufügen.
	 * 
	 * @param name
	 *            Name des Studiengangs.
	 * @param description
	 *            Detaillierte Beschreibung des Studiengangs.
	 * @param levels
	 *            Anzahl der Semester in der Regelstudienzeit.
	 * 
	 * @return gibt einen neuen Studiengang zurück
	 */
	public Program createProgram(final String name, final String description, final Integer levels) {
		final Program newProgram = new Program();
		newProgram.setName(name);
		newProgram.setDescription(description);
		newProgram.setLevels(levels);
		return newProgram;
	}

	/**
	 * Erstellt einen neuen Kurs. Nur der Administrator hat die Möglichkeit
	 * einen neuen Kurs dem System hinzuzufügen. <br/>
	 * <b>ACHTUNG:</b> Der geklonte Kurs ist noch nicht persistent gespeichert,
	 * dies muss separat erfolgen da die Logikschicht keine Koppelung an die
	 * Persistenzschicht besitzt.
	 * 
	 * @param courseIdentifier
	 *            Eindeutige Kennung des Kurses.
	 * @param description
	 *            Detaillierte Beschreibung eines Kurses.
	 * @param maxParticipants
	 *            Maximale Anzahl der Teilnehmer.
	 * @param level
	 *            Semester, dem das Fach zugeordnet wird.
	 * @param group
	 *            Spezifische Studentengruppe (z.B. der Zug).
	 * @param program
	 *            Der Studiengang zu dem der Kurs gehört.
	 * @param term
	 *            Das (zeitliche) Semester
	 * @return Der neu erstellte Kurs.
	 */
	public Course createCourse(final String courseIdentifier, final String title, final String description,
			final Integer maxParticipants, final Integer level, final Integer group, final Program program,
			final Term term) {
		if (!administeredPrograms.contains(program))
			throw MultexUtil.create(ProgramSecurityExc.class, program.getName(), this.toString());
		final Course newCourse = new Course();
		newCourse.setCourseIdentifier(courseIdentifier);
		newCourse.setTitle(title);
		newCourse.setDescription(description);
		newCourse.setMaxParticipants(maxParticipants);
		newCourse.setLevel(level);
		newCourse.setStudyGroup(group);
		newCourse.setProgram(program);
		newCourse.setTerm(term);
		return newCourse;
	}

	/**
	 * Klont einen bestehenden Kurs. Der Administrator hat die Möglichkeit Kurse
	 * zu klonen, um unnötigen Arbeitsaufwand zu minimieren. Der Kurs muss
	 * bereits im System vorhanden sein. <br/>
	 * <b>ACHTUNG:</b> Operation nimmt keine persistente Speicherung vor, dies
	 * muss separat erfolgen da die Logikschicht keine Koppelung an die
	 * Persistenzschicht besitzt.
	 * 
	 * @param course
	 *            Der bereits bestehende, zu klonende Kurs.
	 * @param term
	 *            Entsprechendes Semester fuer den neuen Kurs.
	 * @param lessons
	 *            Unterrichtseinheiten, die der neue Kurs aufweisen soll.
	 * @return Den geklonten Kurs.
	 */
	public Course cloneCourse(final Course course, final Term term, final List<Lesson> lessons) {
		if (course == null)
			throw new IllegalArgumentException();
		if (course.getProgram() != null && !administeredPrograms.contains(course.getProgram()))
			throw MultexUtil.create(ProgramSecurityExc.class, course.getProgram().getName(), this.toString());

		final Course newCourse = new Course();

		newCourse.setCourseIdentifier(course.getCourseIdentifier());
		newCourse.setDescription(course.getDescription());
		newCourse.setLevel(course.getLevel());
		newCourse.setMaxParticipants(course.getMaxParticipants());
		newCourse.setProgram(course.getProgram());
		newCourse.setStudyGroup(course.getStudyGroup());

		for (final Instructor instructor : course.getInstructors()) {
			newCourse.addInstructor(instructor);
		}

		newCourse.setTerm(term);

		for (final Lesson lesson : lessons) {
			newCourse.addLesson(lesson);
		}

		return newCourse;
	}

	/**
	 * Fügt einen Dozenten einem Kurs hinzu. Maximale Anzahl an Dozenten pro
	 * Kurs siehe Course.MAX_INSTRUCTORS <br/>
	 * <b>ACHTUNG:</b> Operation nimmt keine persistente Speicherung vor, dies
	 * muss separat erfolgen da die Logikschicht keine Koppelung an die
	 * Persistenzschicht besitzt.
	 * 
	 * @param course
	 *            Kurs, zu dem der Dozent hinzugefügt werden soll.
	 * @param instructor
	 *            Dozent, der hinzugefügt wird.
	 * @throws ProgramSecurityExc
	 *             Wenn der Studiengang des Kurses nicht von diesem Admin
	 *             betreut wird.
	 * @throws TooMuchInstructorsForCourseExc
	 *             Wenn die maximale Anzahl an Dozenten für diesen Kurs
	 *             (maxInstructors) durch das Hinzufügen dieses Dozenten
	 *             überschritten werden würde, dann wird diese Exception
	 *             geworfen (und der Dozent nicht als Instructor für diesen Kurs
	 *             hinzugefügt).
	 */
	public void addInstructorToCourse(final Course course, final Instructor instructor) throws ProgramSecurityExc,
			TooMuchInstructorsForCourseExc {
		if (course.getProgram() != null && !administeredPrograms.contains(course.getProgram()))
			throw MultexUtil.create(ProgramSecurityExc.class, course.getProgram().getName(), this.toString());

		course.addInstructor(instructor);
	}

	/**
	 * Entfernt einen Dozent aus einem Kurs. <br/>
	 * <b>ACHTUNG:</b> Operation nimmt keine persistente Speicherung vor, dies
	 * muss separat erfolgen da die Logikschicht keine Koppelung an die
	 * Persistenzschicht besitzt.
	 * 
	 * @param course
	 *            Kurs, aus dem ein Dozent entfernt wird
	 * @param instructor
	 *            Dozent, der entfernt werden soll
	 * @throws InstructorNotFoundExc
	 *             Wenn der Instructor dem Kurs nicht zugeordnet war.
	 * @throws ProgramSecurityExc
	 *             Wenn der Studiengang des Kurses nicht von diesem Admin
	 *             betreut wird.
	 */
	public void removeInstructor(final Course course, final Instructor instructor) throws InstructorNotFoundExc,
			ProgramSecurityExc {
		if (course.getProgram() != null && !administeredPrograms.contains(course.getProgram()))
			throw MultexUtil.create(ProgramSecurityExc.class, course.getProgram().getName(), this.toString());

		course.removeInstructor(instructor);
	}

	/**
	 * Erstellt ein neues Semester. <br/>
	 * <b>ACHTUNG:</b> Operation nimmt keine persistente Speicherung vor, dies
	 * muss separat erfolgen da die Logikschicht keine Koppelung an die
	 * Persistenzschicht besitzt.
	 * 
	 * @param startDate
	 *            Startdatum des Semesters
	 * @param endDate
	 *            Enddatum des Semesters
	 * @throws StartDateAfterEndDateExc
	 *             Diese Exception wird geworfen wenn das Startdatum für das
	 *             Semester nach dem Enddatum liegt.
	 */
	public void createTerm(final Calendar startDate, final Calendar endDate) throws StartDateAfterEndDateExc {
		final Term newTerm = new Term();
		newTerm.setStartDate(startDate);
		newTerm.setEndDate(endDate);
	}

	/**
	 * Entfernt einen Kurs. Ein bereits bestehender Kurs wird aus einem
	 * Studiengang entfernt. <br/>
	 * <b>ACHTUNG:</b> Operation nimmt keine persistente Speicherung vor, dies
	 * muss separat erfolgen da die Logikschicht keine Koppelung an die
	 * Persistenzschicht besitzt.
	 * 
	 * @param course
	 *            Zu entfernender Kurs
	 * @throws StillRunningCourseWithParticipantsDeletionExc
	 *             Diese Exception wird geworfen falls der Kurs noch läuft und
	 *             noch Teilnehmer auf ihn eingetragen sind.
	 */
	public void removeCourse(final Course course) throws StillRunningCourseWithParticipantsDeletionExc {
		if (course == null)
			throw new IllegalArgumentException();
		if (course.getProgram() != null && !administeredPrograms.contains(course.getProgram()))
			throw MultexUtil.create(ProgramSecurityExc.class, course.getProgram().getName(), this.toString());

		course.getProgram().removeCourse(course);
	}

}
