package de.beuth.sp.belegsystem.lg;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import multex.MultexUtil;
import multex.extension.AnnotatedExc;
import multex.extension.ExcMessage;

import org.apache.commons.lang.NullArgumentException;

/**
 * Rolle des Studenten. Ein Participant ist ein Teilnehmer am Belegsystem, in
 * diesem Fall ein/e Student/in. Ein Participant hat Kurse belegt und ist in ein
 * Program (studiengang) ab einem bestimmten Datum (registered) eingeschrieben.
 * 
 * 
 */
@Entity
public class Participant extends Role {

	private static final long serialVersionUID = 2967866866635218653L;

	/**
	 * Liste mit allen Kursen eines Studenten
	 */
	@ManyToMany
	private Set<Course> courses = new HashSet<Course>();

	/**
	 * Studiengang des Studenten
	 */
	@ManyToOne
	private Program program;

	@Temporal(TemporalType.DATE)
	private Calendar registered;

	public Set<Course> getCourses() {
		return Collections.unmodifiableSet(courses);
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public Calendar getRegistered() {
		return registered;
	}

	public void setRegistered(Calendar registered) {
		
		this.registered = registered;
	}

	/**
	 * Einen konkreten Kurs belegen. Anhand der Funktion kann ein
	 * Student/Teilnehmer die Kurse belegen. Anschließend ist er im System
	 * eingeschrieben.
	 * 
	 * @param course
	 *   		Der Kurs der zu belegen ist.
	 * 
	 * @return  Rückmeldung, ob die Belegung erfolgreich ist.
	 * @throws CourseAlreadyAddedExc 
	 * 			Exception wird geworfen, wenn der Kurs schon belegt wurde
	 * @throws CourseCanNotBeBookedExc
	 * 			Exception wird geworfen, wenn sich das Buchungsdatum außerhalb des
	 * 			Buchungszeitraums befindet
	 */
	public void addCourse(final Course course) throws CourseAlreadyAddedExc, CourseCanNotBeBookedExc {
		if(course == null)
			throw new NullArgumentException("course");

		// Prüfen, ob der Kurs schon gebucht wurde
		if(hasCourse(course))
			throw MultexUtil.create(CourseAlreadyAddedExc.class, course);	
		
		if (!checkBookingUpdatePossible(course)) {
			throw MultexUtil.create(CourseCanNotBeBookedExc.class, course, course.getTerm().getBookingPeriodStart().getTime(), course.getTerm().getBookingPeriodEnd().getTime());
		}
		
		this.courses.add(course);
		if (!course.getParticipants().contains(this)) {
			course.addParticipant(this);
		}
	}
	
	@ExcMessage("Der Kurs ({0}) wurde schon belegt.")
	public static final class CourseAlreadyAddedExc extends AnnotatedExc{
		private static final long serialVersionUID = 6058115007333224777L;
	}
	
	@ExcMessage("Der Kurs ({0}) kann nicht mehr entfernt werden da nicht mehr innerhalb des Buchungszeitraums ({1,date,short} - {2,date,short}).")
	public static final class CourseCanNotBeRemovedExc extends AnnotatedExc{
		private static final long serialVersionUID = 6058115007333224777L;
	}

	@ExcMessage("Der Kurs ({0}) kann nicht mehr belegt werden da nicht mehr innerhalb des Buchungszeitraums ({1,date,short} - {2,date,short}).")
	public static final class CourseCanNotBeBookedExc extends AnnotatedExc{
		private static final long serialVersionUID = -1015503391818524307L;
	}
	
	/**
	 * Es wird geprüft, ob ein konkreter Kurs schon 
	 * belegt wurde.
	 * 
	 * @param course
	 * 		Kurs, der geprüft werden soll
	 * @return
	 * 		Rückmeldung true, wenn der Kurs schon 
	 * 		belegt worden ist. False wenn er noch nicht belegt
	 * 		wurde.
	 */
	public boolean hasCourse(final Course course){
		if(course == null)
			throw new NullArgumentException("course");
		if(this.courses.contains(course)){
			return true;
		}
		return false;
	}
	
	/**
	 * Einen belegten Kurs stornieren. Anhand der Funktion kann ein
	 * Student/Teilnehmer einen Kurs stornieren.
	 * 
	 * @param course
	 *            Der Kurs der zu stornieren ist.
	 * 
	 * @return Rückmeldung, ob die Stornierung erfolgreich ist.
	 * 
	 */
	public void cancelCourse(final Course course) {
		if(course == null)
			throw new NullArgumentException("course");
		if (!checkBookingUpdatePossible(course)) {
			throw MultexUtil.create(CourseCanNotBeRemovedExc.class, course, course.getTerm().getBookingPeriodStart().getTime(), course.getTerm().getBookingPeriodEnd().getTime());
		}
		
		this.courses.remove(course);
		if (course.getParticipants().contains(this)) {
			course.removeParticipant(this);
		}
	}
	
	/**
	 * Prüft, ob sich der Benutzer im erlaubten Buchungszeitraum befindet		
	 * 
	 * @param course
	 * 			Der Kurs, der geprüft werden soll
	 * 				
	 * @return Rückmeldung true, wenn sich der Kurse noch
	 * 			im Buchungszeitrum befindet. False, wenn der
	 * 			Kurs sich nicht mehr im Buchungszeitraum
	 * 			befindet
	 * 			
	 */
	private boolean checkBookingUpdatePossible(final Course course) {		
		if(course == null)
			throw new NullArgumentException("course");

		//aktuelles Datum
		final Calendar currentDate = Calendar.getInstance();

		if (course.getTerm() != null) {
			//Überprüfung
			return course.getTerm().getBookingPeriodStart().before(currentDate) 
					&& course.getTerm().getBookingPeriodEnd().after(currentDate);
		}
		// Falls bookingPeriodStart nicht ermittelt werden konnte weil der
		// Course keinen Term zugeordnet ist oder der Term kein Startdatum
		// besitzt, dann... kann nicht gebucht werden.
		return false;
	}
}
