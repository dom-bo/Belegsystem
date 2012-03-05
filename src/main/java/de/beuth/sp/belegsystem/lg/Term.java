package de.beuth.sp.belegsystem.lg;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.apache.commons.lang.NullArgumentException;
import org.apache.tapestry5.beaneditor.Validate;

import de.beuth.sp.belegsystem.Settings;
import de.beuth.sp.belegsystem.exceptions.ExcUtil;
import de.beuth.sp.belegsystem.exceptions.ExcUtil.StartDateAfterEndDateExc;
import de.beuth.sp.belegsystem.lg.base.PersistentImpl;
import de.beuth.sp.belegsystem.util.CalendarUtil;
import de.beuth.sp.belegsystem.util.CalendarUtil.TimeDetail;

/**
 * Klasse zur Abstraktion eines Semesters oder ähnlichen Zeitfensters. Hierzu
 * besitzt die Klasse ein Start- und ein Enddatum. Sie besitzt außerdem eine
 * Liste, der im definierten Zeitraum stattfindenen Kurse.
 * 
 * 
 */
@Entity
public class Term extends PersistentImpl {

	private static final long serialVersionUID = -315945651444725602L;
	
	
	/**
	 * Bezeichnung eines Semesters 
	 */
	private String label;
	
	/**
	 * Zeitpunkt, wann das Semester startet
	 */
	private Calendar startDate;
	
	/**
	 * Zeitpunkt, wann das Semester endet
	 */
	private Calendar endDate;

	/**
	 * Liste aller Kurse zu einem Term
	 */
	@OneToMany(mappedBy = "term")
	private Set<Course> courses = new HashSet<Course>();

	public Calendar getStartDate() {
		return startDate;
	}

	/**
	 * Setzt den Start Zeitpunkt für das Semester.
	 * Davor wird geprüft, ob sich der Start Zeitpunkt
	 * vor dem End Zeitpunkt des Semesters befindet.
	 * 
	 * @param startDate
	 *            StartDatum für das Semester
	 * @throws StartDateAfterEndDateException
	 * 				Wird geworfen, wenn das StartDatum hinter dem EndDatum liegt
	 */
	@Validate(Settings.VALIDATE_REQUIRED)
	public void setStartDate(final Calendar startDate) throws StartDateAfterEndDateExc {
		ExcUtil.checkForStartDateAfterEndDateExc(startDate, this.endDate);
		CalendarUtil.setToZeroDetailAndBelow(startDate, TimeDetail.HOUR);
		this.startDate = startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	/**
	 * Setzt den End Zeitpunkt für das Semester.
	 * Davor wird geprüft, ob sich der Start Zeitpunkt
	 * vor dem End Zeitpunkt des Semesters befindet.	 
	 * @param endDate
	 *            EndDatum für das Semester
	 *            
	 * @throws StartDateAfterEndDateException 
	 * 			Wird geworfen, wenn das StartDatum hinter dem EndDatum liegt.
	 */
	@Validate(Settings.VALIDATE_REQUIRED)
	public void setEndDate(final Calendar endDate) throws StartDateAfterEndDateExc {
		ExcUtil.checkForStartDateAfterEndDateExc(this.startDate, endDate);
		CalendarUtil.setToZeroDetailAndBelow(endDate, TimeDetail.HOUR);
		this.endDate = endDate;
	}

	public Set<Course> getCourses() {
		return Collections.unmodifiableSet(courses);
	}
	
	/**
	 * Einen konkreten Kurs zu einem Semester hinzufügen.
	 * Anhand dieser Methode kann ein Kurs einem Semester 
	 * hinzugefügt werden. 
	 * 
	 * @param course
	 * 			Ein konkreter Kurs, der dem Semester hinzugefügt werden soll
	 */
	public void addCourse(final Course course) {
		if(course == null)
			throw new NullArgumentException("course");
		if(!this.courses.contains(course))
			this.courses.add(course);
		if (!this.equals(course.getTerm())) {
			course.setTerm(this);
		}
	}
	
	/**
	 * Einen konkreten Kurs aus einem Semester entfernen.
	 * Anhand dieser Methode kann ein Kurs aus einem Semester
	 * entfernt werden. 
	 * 
	 * @param course
	 * 			Ein konkreter Kurs, der aus dem Semester entfernt werden soll
	 */
	public void removeCourse(final Course course) {
		if(course == null)
			throw new NullArgumentException("course");
		this.courses.remove(course);
		if (this.equals(course.getTerm())) {
			course.setTerm(null);
		}
	}	
	
	public String getLabel() {
		return label;
	}
	
	@Validate(Settings.VALIDATE_REQUIRED)
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Es wird ein Start-Zeitpunkt zum Buchen eines 
	 * Semester zurückgegeben. Dieser Start-Zeitpunkt 
	 * befindet sich vor dem Start-Zeitpunkt des Semesters.
	 * Die Dauer dieser Zeitspanne kann in der Klasse Settings 
	 * hinterlegt werden. Wenn der Start-Zeitpunkt des Semesters noch nicht gesetzt wurde,
	 * 	wird null zurückgegeben. 
	 * 
	 * @return Rückgabewert ist ein Zeitraum, ab wann das Semester belegt werden
	 * 			kann. Wenn der Start-Zeitpunkt des Semesters noch nicht gesetzt wurde,
	 * 			wird null zurückgegeben.
	 */
	public Calendar getBookingPeriodStart() {
		// Buchungszeitraum vor Beginn des Semester (halbe Länge der
		// CourseBookingDays wird abgezogen)
		if (getStartDate() != null) {
			Calendar bookingPeriodStartCal = (Calendar) getStartDate().clone();
			bookingPeriodStartCal.add(Calendar.DAY_OF_MONTH, -1*(Settings.getCourseBookingDays()/2));
			return bookingPeriodStartCal;
		}
		return null;
	}
	
	
	/**
	 * Es wird ein End-Zeitpunkt zum Buchen eines 
	 * Semester zurückgegeben. Dieser End-Zeitpunkt 
	 * befindet sich nach dem Start-Zeitpunkt des Semesters.
	 * Die Dauer dieser Zeitspanne kann in der Klasse Settings 
	 * hinterlegt werden. Wenn der Start-Zeitpunkt des Semesters noch nicht gesetzt wurde,
	 * 	wird null zurückgegeben. 
	 * 
	 * @return Rückgabewert ist ein Zeitraum, bis wann das Semester belegt werden
	 * 			kann. Wenn der Start-Zeitpunkt des Semesters noch nicht gesetzt wurde,
	 * 			wird null zurückgegeben.
	 */
	public Calendar getBookingPeriodEnd() {
		// Buchungszeitraum nach Beginn des Semester (die andere halbe Länge der
		// CourseBookingDays)		
		if (getStartDate() != null) {
			Calendar bookingPeriodEndCal = (Calendar) getStartDate().clone();
			bookingPeriodEndCal.add(Calendar.DAY_OF_MONTH, Settings.getCourseBookingDays()/2);
			return bookingPeriodEndCal;
		}
		return null;		
	}	
		
	@Override
	public String toString() {
		return label;
	}

	public String getLongFormat() {
		final String pattern = "dd.MM.yyyy";
		return label + " (" + new SimpleDateFormat(pattern).format(getStartDate().getTime()) + " - " +
				new SimpleDateFormat(pattern).format(getEndDate().getTime()) + ")";		
	}

	

}