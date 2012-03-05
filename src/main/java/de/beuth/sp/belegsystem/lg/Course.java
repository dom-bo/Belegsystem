package de.beuth.sp.belegsystem.lg;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import multex.MultexUtil;
import multex.extension.AnnotatedExc;
import multex.extension.ExcMessage;

import org.apache.commons.lang.NullArgumentException;
import org.apache.tapestry5.beaneditor.Validate;

import de.beuth.sp.belegsystem.Settings;
import de.beuth.sp.belegsystem.db.manager.CourseManager;
import de.beuth.sp.belegsystem.exceptions.ExcUtil;
import de.beuth.sp.belegsystem.exceptions.ExcUtil.ProgramLevelBelowCourseLevelExc;
import de.beuth.sp.belegsystem.lg.Participant.CourseAlreadyAddedExc;
import de.beuth.sp.belegsystem.lg.Participant.CourseCanNotBeBookedExc;
import de.beuth.sp.belegsystem.lg.base.PersistentImpl;

/**
 * Konkreter Kurs im System. Die Klasse abstrahiert ein Kurs und dessen
 * Zuordnungen. Sie verwaltet die Eigenschaften mit eindeutiger Kennung,
 * Beschreibung, Kursteilnehmer und Dozenten sowie spezifische Parameter für den
 * Ablauf (wann er stattfindet usw.).
 * 
 */
@Entity
public class Course extends PersistentImpl {

	private static final long serialVersionUID = 7032040591903555064L;

	private static final int DEFAULT_MAX_PARTICIPANTS = 30;
	private static final int DEFAULT_LEVEL = 1;
	private static final int DEFAULT_MAX_INSTRUCTORS = 3;

	private String courseIdentifier;
	private String description;
	private Integer maxParticipants = DEFAULT_MAX_PARTICIPANTS;
	private Integer level = DEFAULT_LEVEL;
	private Integer studyGroup;

	private Integer maxInstructors = DEFAULT_MAX_INSTRUCTORS;
	private String title;

	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Lesson> lessons = new HashSet<Lesson>();

	@ManyToMany(mappedBy = "courses", cascade = CascadeType.PERSIST)
	private Set<Instructor> instructors = new HashSet<Instructor>();

	@ManyToOne(cascade = CascadeType.PERSIST)
	private Program program;

	@ManyToOne
	private Term term;

	@ManyToMany(mappedBy = "courses")
	private Set<Participant> participants = new HashSet<Participant>();

	public String getCourseIdentifier() {
		return this.courseIdentifier;
	}

	public void setCourseIdentifier(final String courseIdentifier) {
		this.courseIdentifier = courseIdentifier;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Integer getMaxParticipants() {
		return this.maxParticipants;
	}

	public void setMaxParticipants(final Integer maxParticipants) {
		this.maxParticipants = maxParticipants;
	}

	public Integer getLevel() {
		return this.level;
	}

	public void setLevel(final Integer level) throws ProgramLevelBelowCourseLevelExc {
		ExcUtil.checkForLevels(program, (program != null ? program.getLevels() : null), this, level);
		this.level = level;
	}

	public Integer getStudyGroup() {
		return studyGroup;
	}

	public void setStudyGroup(final Integer studyGroup) {
		this.studyGroup = studyGroup;
	}

	public Integer getMaxInstructors() {
		return maxInstructors;
	}

	public void setMaxInstructors(Integer maxInstructors) {
		this.maxInstructors = maxInstructors;
	}

	/**
	 * Fügt einen Dozent zu einem Kurs hinzu (instructors). Der übergebene
	 * Dozent(Instructor) darf nicht Null sein.
	 * 
	 * @param instructor
	 *            Dozent der dem Kurs hinzugefügt werden soll, darf nicht NULL
	 *            sein
	 * @throws TooMuchInstructorsForCourseExc
	 *             Wenn die maximale Anzahl an Dozenten für diesen Kurs
	 *             (maxInstructors) durch das Hinzufügen dieses Dozenten
	 *             überschritten werden würde, dann wird diese Exception
	 *             geworfen (und der Dozent nicht als Instructor für diesen Kurs
	 *             hinzugefügt).
	 */
	public void addInstructor(final Instructor instructor) throws TooMuchInstructorsForCourseExc {
		if (instructor == null)
			throw new IllegalArgumentException();
		if (instructors.size() >= maxInstructors) {
			throw MultexUtil.create(TooMuchInstructorsForCourseExc.class, instructor.toString(), this.toString(),
					maxInstructors);
		}
		this.instructors.add(instructor);
		if (!instructor.getCourses().contains(this)) {
			instructor.addCourse(this);
		}
	}

	/**
	 * Löscht einen Dozent von einem Kurs (instructors). Der übergebene Dozent
	 * darf nicht Null sein
	 * 
	 * @param instructor
	 *            Dozent der aus dem Kurs gelöscht werden soll, darf nicht NULL
	 *            sein
	 * @throws InstructorNotFoundExc
	 *             Wenn der Instructor dem Kurs nicht zugeordnet war.
	 * 
	 */
	public void removeInstructor(final Instructor instructor) throws InstructorNotFoundExc {
		if (instructor == null)
			throw new IllegalArgumentException();
		if (!instructors.contains(instructor))
			throw MultexUtil.create(InstructorNotFoundExc.class, instructor.toString(), this.getCourseIdentifier());
		this.instructors.remove(instructor);
		if (instructor.getCourses().contains(this)) {
			instructor.removeCourse(this);
		}
	}

	@ExcMessage("Lehrkraft {0} nicht gefunden als Lehrkraft für den Kurs {1}")
	public static final class InstructorNotFoundExc extends AnnotatedExc {
		private static final long serialVersionUID = -43543534543453L;
	}

	/**
	 * Fügt eine Unterichtseinheit dem Kurs hinzu. Eine bereits bestehende
	 * Unterichtseinheit kann diesem Kurs hinzugefügt werden.
	 * 
	 * @param lesson
	 *            Die Unterrichtseinheit die hinzugefügt werden soll.
	 * @return boolean True wenn die Unterrichtseinheit der Menge an
	 *         Unterrichtseinheiten neu hinzugefügt wurde, false falls sie nicht
	 *         neu hinzugefügt wurde da bereits enthalten.
	 * @throws TimeConflictExc
	 *             Falls die Unterrichtseinheit in einem zeitlichen Konflikt mit
	 *             einer schon vorhanden Einheit steht wird diese Exception
	 *             geworfen (und die Unterrichtseinheit nicht hinzugefügt).
	 */
	public boolean addLesson(final Lesson lesson) throws LessonTimeConflictExc {
		boolean result = false;
		if (!this.lessons.contains(lesson)) {
			for (final Lesson existingLesson : this.lessons) {
				if (lesson.conflicts(existingLesson)) {
					throw MultexUtil.create(LessonTimeConflictExc.class, existingLesson, lesson);
				}
			}
			result = this.lessons.add(lesson);
		}
		if (!this.equals(lesson.getCourse())) {
			lesson.setCourse(this);
		}
		return result;
	}

	@ExcMessage("Die Unterrichtseinheit {0} steht in zeitlichem Konflikt mit der Unterrichtseinheit {1}")
	public static final class LessonTimeConflictExc extends AnnotatedExc {
		private static final long serialVersionUID = 5690295742073015367L;
	}

	public void removeLesson(final Lesson lesson) {
		this.lessons.remove(lesson);
		if (this.equals(lesson.getCourse())) {
			lesson.setCourse(null);
		}
	}

	public Set<Lesson> getLessons() {
		return Collections.unmodifiableSet(lessons);
	}

	public Set<Instructor> getInstructors() {
		return Collections.unmodifiableSet(this.instructors);
	}

	@ExcMessage("Dozent {0} konnte dem Kurs {1} nicht hinzugefügt werden da bereits die Maximlalanzahl von {2} Dozenten auf den Kurs eingetragen sind.")
	public static final class TooMuchInstructorsForCourseExc extends AnnotatedExc {
		private static final long serialVersionUID = 3625976354387357942L;
	}

	@Validate(Settings.VALIDATE_REQUIRED)
	public Program getProgram() {
		return this.program;
	}

	public void setProgram(final Program newProgram) throws ProgramLevelBelowCourseLevelExc {
		ExcUtil.checkForLevels(newProgram, (newProgram != null ? newProgram.getLevels() : null), this, this.getLevel());
		if (program != null && !program.equals(newProgram) && program.getCourses().contains(this)) {
			program.removeCourse(this);
		}
		program = newProgram;
		if (program != null && !program.getCourses().contains(this)) {
			program.addCourse(this);
		}
	}

	public Term getTerm() {
		return this.term;
	}

	@Validate(Settings.VALIDATE_REQUIRED)
	public void setTerm(final Term term) {
		if (term == null && this.term != null) {
			if (this.term.getCourses().contains(this)) {
				this.term.removeCourse(this);
			}
		}
		this.term = term;
		if (!term.getCourses().contains(this)) {
			term.addCourse(this);
		}
	}

	public Set<Participant> getParticipants() {
		return Collections.unmodifiableSet(participants);
	}

	/**
	 * @param participant
	 * @throws CourseFullExc
	 * @throws CourseAlreadyAddedExc
	 */
	public void addParticipant(final Participant participant) throws CourseFullExc, CourseAlreadyAddedExc,
			CourseCanNotBeBookedExc {
		if (participant == null) {
			throw new NullArgumentException("participant");
		}
		if (participants.size() >= maxParticipants) {
			throw MultexUtil.create(CourseFullExc.class, participant.toString(), this.getCourseIdentifier(), this
					.getParticipants().size());
		}
		participants.add(participant);
		if (!participant.getCourses().contains(this)) {
			participant.addCourse(this);
		}
	}

	@ExcMessage("Teilnehmer {0} konnte nicht hinzugefügt werden, "
			+ "da der Kurs {1} mit {2} eingetragenen Teilnehmern bereits vollständig belegt ist.")
	public static final class CourseFullExc extends AnnotatedExc {
		private static final long serialVersionUID = 7004753544988179547L;
	}

	/**
	 * Entfernt einen {@link Participant} aus einem Course. Nur während
	 * Belegzeit möglich da die Methode aufruft
	 * {@link Participant#cancelCourse(Course)} womit der {@link Participant}
	 * den {@link Course} storniert - was nur während der Belegzeit möglich ist.
	 * Falls der Participant unabhängig von der Belegzeit aus dem Kurs entfernt
	 * werden soll muss
	 * {@link CourseManager#removeParticipantFromCourse(Course, Participant)
	 * (Course)} oder
	 * {@link CourseManager#removeParticipantFromCourseAndCommit(Course, Participant)
	 * (Course)} aufgerufen werden - diese darf aber nur im Context eines
	 * angemeldeten Admins benutzt werden.
	 * 
	 * @param participant
	 */
	public void removeParticipant(final Participant participant) {
		if (participant == null) {
			throw new NullArgumentException("participant");
		}
		// TODO mögliche fachliche Exception - falls Kurs bereits läuft
		participants.remove(participant);
		if (participant.getCourses().contains(this)) {
			participant.cancelCourse(this);
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return courseIdentifier;
	}
}