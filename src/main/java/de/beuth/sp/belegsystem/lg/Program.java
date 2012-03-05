package de.beuth.sp.belegsystem.lg;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import multex.MultexUtil;
import multex.extension.AnnotatedExc;
import multex.extension.AnnotatedFailure;
import multex.extension.ExcMessage;
import de.beuth.sp.belegsystem.exceptions.ExcUtil;
import de.beuth.sp.belegsystem.exceptions.ExcUtil.ProgramLevelBelowCourseLevelExc;
import de.beuth.sp.belegsystem.lg.base.PersistentImpl;

/**
 * Klasse für ein "Programm", z.B. ein Studiengang. Die Studiengänge werden hier
 * eindeutig definiert und mit Parametern zu ihrem Inhalt, ihrer Dauer (levels)
 * und beinhalteten Faechern verarbeitet.
 * 
 */
@Entity
public class Program extends PersistentImpl {

	private static final long serialVersionUID = -7327198272663100761L;
	
	private static final int DEFAULT_LEVELS = 6;

	@OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval=true)
	private Set<Course> courses = new HashSet<Course>();

	private String name;
	private String description;
	private Integer levels = DEFAULT_LEVELS;	

	public Set<Course> getCourses() {
		return Collections.unmodifiableSet(courses);
	}

	public void addCourse(final Course course) {
		ExcUtil.checkForLevels(this, this.getLevels(), course, course.getLevel());
		this.courses.add(course);
		if (!this.equals(course.getProgram())) {
			course.setProgram(this);
		}
	}
	
	/**
	 * Entfernt einen Kurs. Ein bereits bestehender Kurs wird aus einem Studiengang entfernt.
	 * 
	 * @param course
	 *            Zu entfernender Kurs
	 * @throws StillRunningCourseWithParticipantsDeletionExc
	 *             Diese Exception wird geworfen falls der Kurs noch läuft und
	 *             noch Teilnehmer auf ihn eingetragen sind.
	 */
	public void removeCourse(final Course course) throws StillRunningCourseWithParticipantsDeletionExc {
		long actualTime = Calendar.getInstance().getTimeInMillis();
		if (!course.getParticipants().isEmpty()) {
			for (Lesson lesson : course.getLessons()) {
				if (lesson.getEndDate().getTimeInMillis() > actualTime) {
					throw MultexUtil.create(StillRunningCourseWithParticipantsDeletionExc.class, course.getCourseIdentifier());
				}
			}
		}
		this.courses.remove(course);
		if (this.equals(course.getProgram())) {	
			course.setProgram(null);
		}
	}
	
	@ExcMessage("Kurs {1} kann nicht gelöscht werden, da er noch läuft und Teilnehmer auf ihn eingetragen sind.")
	public static final class StillRunningCourseWithParticipantsDeletionExc extends AnnotatedExc {
		private static final long serialVersionUID = 8514159594585264736L;
	}	
	
	@ExcMessage("Fehler beim Entfernen des Kurses {0}. " +
			"Sollte eigentlich nicht passieren da sie nur geworfen wird wenn wir den Studiengang beim Kurs auf null setzen.")
	public static class RemoveCourseFailure extends AnnotatedFailure {
		private static final long serialVersionUID = -7519095781080011315L;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Integer getLevels() {
		return levels;
	}

	public void setLevels(final Integer levels) throws ProgramLevelBelowCourseLevelExc {
		for (final Course course : this.courses) {
			ExcUtil.checkForLevels(this, levels, course, course.getLevel());
		}
		this.levels = levels;
	}
	
	
	@Override
	public String toString() {
		return name;
	}

}
