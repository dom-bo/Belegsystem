package de.beuth.sp.belegsystem.lg;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import multex.MultexUtil;
import multex.extension.AnnotatedExc;
import multex.extension.ExcMessage;

import org.apache.commons.lang.NullArgumentException;

/**
 * Rolle des Dozenten. Der Dozent ist der Leiter eines Kurses, er unterrichtet
 * ihn. Die Klasse stellt Verwaltungsmethoden bereit, um seine Kurse bzw. deren
 * Teilnehmer zu verwalten.
 * 
 * 
 */
@Entity
public class Instructor extends Role {

	private static final long serialVersionUID = -6751382561890166653L;

	/**
	 * Alle Kurse bei dem der Instructor als Instructor eingetragen ist.
	 */
	@ManyToMany
	private Set<Course> courses = new HashSet<Course>();

	/**
	 * Alle {@link Program}s für die der Dozent {@link Course}s gibt.
	 * Unidirektionale Verknüpfung.
	 */
	@OneToMany
	private Set<Program> programs = new HashSet<Program>();

	public Set<Course> getCourses() {
		return Collections.unmodifiableSet(courses);
	}

	/**
	 * Fügt einen Kurs hinzu, bei dem der Instructor als Instructor eingetragen
	 * ist oder werden soll. Die Methode überprüft, ob der Kurs bereits den
	 * Instructor enthält, wenn nicht wird er dem Kurs hinzugefügt (birektionale
	 * Verbindung).<br/>
	 * Außerdem fügt es das {@link Program} dem {@link #programs} -Set hinzu
	 * falls der Instructor bisher keine Kurse dafür gegeben hat.
	 * 
	 * @param course
	 *            Der neue Kurs des Instructors.
	 */
	public void addCourse(final Course course) throws NullArgumentException {
		if (course == null)
			throw new NullArgumentException("course");
		courses.add(course);
		if (!course.getInstructors().contains(this)) {
			course.addInstructor(this);
		}
		if (course.getProgram()!=null && !programs.contains(course.getProgram())) {
			addProgram(course.getProgram());
		}
	}

	/**
	 * Entfernt einen Kurs, bei dem der Instructor eingetragen war ist noch ist.
	 * Die Methode überprüft, ob der Instructor immer noch bei dem Kurs
	 * eingetragen ist und entfernt ihn ggf..
	 * 
	 * @param course
	 *            Der alte Kurs des Instructors.
	 */
	public void removeCourse(final Course course) throws NullArgumentException {
		if (course == null)
			throw new NullArgumentException("course");
		courses.remove(course);
		if (course.getInstructors().contains(this)) {
			course.removeInstructor(this);
		}
	}

	public Set<Program> getPrograms() {
		return Collections.unmodifiableSet(programs);
	}

	/**
	 * Fügt ein {@link Program} hinzu, für den der Instructor einen Kurs gibt bzw. geben soll.
	 * 
	 * @param course
	 *            Das neu Program für das der Instructor einen Kurs gibt bzw. geben soll
	 */
	public void addProgram(final Program program) throws NullArgumentException {
		if (program == null)
			throw new NullArgumentException("program");
		programs.add(program);
	}

	/**
	 * Entfernt einen {@link Program}, für das der Instructor {@link Course}s geben soll(te).
	 * 
	 * @param course
	 *            Das alte Program für das der Instructor Kurse geben sollte.
	 */
	public void removeProgram(final Program program) throws NullArgumentException {
		if (program == null)
			throw new NullArgumentException("program");
		for (final Course course : courses) {
			if (program.equals(course.getProgram())) {
				throw MultexUtil.create(InstructorProgramCannotBeRemovedExc.class, this.getUser().getUsername(), program.getName());
			}
		}
		programs.remove(program);
	}
	
	@ExcMessage("Der assozierte Studiengang {1} kann bei Dozent {0} nicht entfernt werden da er noch einen Kurs in diesem Studiengang aufweist.")
	public static final class InstructorProgramCannotBeRemovedExc extends AnnotatedExc{
		private static final long serialVersionUID = 4392161153196912083L;
	}	

}