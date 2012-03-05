package de.beuth.sp.belegsystem.lg;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import multex.MultexUtil;
import multex.extension.AnnotatedExc;
import multex.extension.ExcMessage;
import de.beuth.sp.belegsystem.exceptions.ExcUtil;
import de.beuth.sp.belegsystem.exceptions.ExcUtil.StartDateAfterEndDateExc;
import de.beuth.sp.belegsystem.lg.base.PersistentImpl;
import de.beuth.sp.belegsystem.util.CalendarUtil;
import de.beuth.sp.belegsystem.util.CalendarUtil.TimeDetail;

/**
 * Eine konkrete Unterrichtseinheit. Eine Unterrichtseinheit ist immer einem
 * Kurs zugeordnet und einmalig pro konkreten Kurs. Er gibt das Start- und
 * Enddatum der Einheit an sowie die Wiederholung in Wochen an. Mit Hilfe der
 * Lesson in Kombination mit TimeSlot kann der komplette Stundenplan generiert
 * werden.
 * 
 * 
 */
@Entity
public class Lesson extends PersistentImpl {	

	private static final long serialVersionUID = 5874650307114788821L;

	@ManyToOne
	private Course course;

	@ManyToOne
	private TimeSlot timeSlot;

	@Temporal(TemporalType.DATE)
	private Calendar startDate;

	@Temporal(TemporalType.DATE)
	private Calendar endDate;

	private String room;

	private Integer repetitionInWeeks = 1;

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
		if (course!=null && !course.getLessons().contains(this)) {
			course.addLesson(this);
		}
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public Integer getRepetitionInWeeks() {
		return repetitionInWeeks;
	}

	public void setRepetitionInWeeks(Integer repetitionInWeeks) {
		if (repetitionInWeeks < 0) {
			throw new IllegalArgumentException("RepetitionInWeeks must not be negative!");
		}
		this.repetitionInWeeks = repetitionInWeeks;		
		if (endDate!=null) {			
			//Neuberechnung des Enddatums nötig?
			if (!getSessionDates().contains(endDate)) {
				if (courseTermAvailable()) {
					endDate = (Calendar) getStartDate().clone();
					while (endDate.before(course.getTerm().getEndDate())) {
						endDate.add(Calendar.WEEK_OF_YEAR, repetitionInWeeks);
					}
					endDate.add(Calendar.WEEK_OF_YEAR, -repetitionInWeeks);
				}
			}
		}
	}

	public TimeSlot getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(TimeSlot timeSlot) {		
		this.timeSlot = timeSlot;		
		if (startDate!=null) {
			updateStartDateAccordingToTimeslot(startDate);
		}
		if (endDate!=null) {
			updateEndDateAccordingToTimeSlot(endDate);
		}
	}
	
	public Calendar getStartDate() {
		return startDate;
	}

	public Calendar getComputedStartDate() {
		if (startDate==null) {
			Calendar volatileStartDate = null;
			if (courseTermAvailable()) {
				volatileStartDate = (Calendar) course.getTerm().getStartDate().clone();				
				updateStartDateAccordingToTimeslot(volatileStartDate);
			} else {
				volatileStartDate = CalendarUtil.getCalendarWithZeroAtDetailAndBelow(TimeDetail.HOUR);
			}			
			return volatileStartDate;
		} else {
			return startDate;
		}
	}
	
	public void setStartAndEndDate(Calendar startDate, Calendar endDate) throws StartDateAfterEndDateExc {
		if (startDate!=null) {
			checkIfDateIsTimeSlotCompliant(startDate, timeSlot);
			CalendarUtil.setToZeroDetailAndBelow(startDate, TimeDetail.HOUR);			
		}
		if (endDate!=null) {
			checkIfDateIsTimeSlotCompliant(startDate, timeSlot);
			CalendarUtil.setToZeroDetailAndBelow(startDate, TimeDetail.HOUR);	
		}
		if (startDate != null && endDate != null) {
			ExcUtil.checkForStartDateAfterEndDateExc(startDate, endDate);
		}
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public Calendar getEndDate() {
		return endDate;
	}

	public Calendar getComputedEndDate() {
		if (endDate==null) {
			Calendar volatileEndDate = null;
			if (this.repetitionInWeeks == 0) {
				// keine Wiederholungen, also Enddatum == Startdatum
				volatileEndDate = (Calendar) this.getStartDate().clone();				
			} else {
				volatileEndDate = (Calendar) getStartDate().clone();
				if (courseTermAvailable() && this.timeSlot!=null) {
					while (volatileEndDate.before(course.getTerm().getEndDate())) {
						volatileEndDate.add(Calendar.WEEK_OF_YEAR, repetitionInWeeks);
					}
					volatileEndDate.add(Calendar.WEEK_OF_YEAR, -repetitionInWeeks);					
				}
			}				
			return volatileEndDate;
		} else {	
			return endDate;
		}
	}

	private void checkIfDateIsTimeSlotCompliant(Calendar date, TimeSlot timeSlot) throws DateIsNotTimeSlotCompliantExc {
		if (date.get(Calendar.DAY_OF_WEEK) != timeSlot.getDayOfWeek().ordinal() + 1) {
			Calendar timeSlotCal = Calendar.getInstance();
			timeSlotCal.set(Calendar.DAY_OF_WEEK, timeSlot.getDayOfWeek().ordinal() + 1);
			throw MultexUtil.create(DateIsNotTimeSlotCompliantExc.class,
					date.getTime(),
					timeSlotCal.getTime());
		}
	}
	
	@ExcMessage("Das angegebene Datum ({0,date,EEEE}) passt nicht zum Tag des bestimmten Zeitfensters ({1,date,EEEE}).")
	public static final class DateIsNotTimeSlotCompliantExc extends AnnotatedExc {
		private static final long serialVersionUID = -7738731409697692014L;
	}
	
	public List<Calendar> getSessionDates() {
		final List<Calendar> sessionDates = new LinkedList<Calendar>();
		 Calendar actualSessionDate = (Calendar) this.getComputedStartDate().clone();		
		do {
			sessionDates.add(actualSessionDate);
			Calendar nextSessionDate = Calendar.getInstance();
			nextSessionDate.setTimeInMillis(actualSessionDate.getTimeInMillis());
			nextSessionDate.add(Calendar.WEEK_OF_YEAR, this.getRepetitionInWeeks());
			actualSessionDate = nextSessionDate;
		} while (actualSessionDate.before(this.getComputedEndDate()));
		return sessionDates;
	}
	
	
	private void updateStartDateAccordingToTimeslot(Calendar startDate) {
		if (this.timeSlot!=null && startDate!=null) {
			updateCalendarWithTimeSlot(startDate, this.timeSlot);
			if (courseTermAvailable()) {
				if (startDate.before(course.getTerm().getStartDate())) {
					startDate.add(Calendar.WEEK_OF_YEAR, 1);
				}
			}
		}		
	}
	
	private void updateEndDateAccordingToTimeSlot(Calendar endDate) {
		if (this.timeSlot!=null && endDate!=null) {
			final Calendar oldEndDate = (Calendar) endDate.clone();
			updateCalendarWithTimeSlot(endDate, timeSlot);
			if (courseTermAvailable()) {
				Calendar endDateTerm = course.getTerm().getEndDate();
				if (endDate.after(endDateTerm) && oldEndDate.before(endDateTerm) && repetitionInWeeks!=0) {
					endDate = (Calendar) getStartDate().clone();
					while (endDate.before(endDateTerm)) {
						endDate.add(Calendar.WEEK_OF_YEAR, repetitionInWeeks);
					}
					endDate.add(Calendar.WEEK_OF_YEAR, -repetitionInWeeks);
				}
			}
		}
	}
	
	private void updateCalendarWithTimeSlot(final Calendar cal, final TimeSlot timeSlot) {
		cal.set(Calendar.DAY_OF_WEEK, timeSlot.getDayOfWeek().ordinal() + 1);
	}
	
	private boolean courseTermAvailable() {
		return course!=null && course.getTerm()!=null && course.getTerm().getStartDate() != null;
	}

	/**
	 * Zeitlichen Konflikt zwischen zwei Unterrichtseinheiten (Lessons)
	 * überprüfen. Es wird geprüft ob die Unterrichtseinheit in einem zeitlichen
	 * Konflikt mit einer anderen Einheit steht.
	 * 
	 * @param other
	 *            Die andere Unterrichtseinheit (Lesson).
	 * @return TRUE falls in Konflikt, FALSE falls nicht.
	 */
	public boolean conflicts(Lesson other) {
		if (!this.equals(other)) {
			
			Calendar startDateThisLesson = this.startDate;
			if (startDateThisLesson==null) {
				if (this.getCourse()!=null && this.getCourse().getTerm() != null) {
					startDateThisLesson = this.getCourse().getTerm().getStartDate();
				}
			}
			Calendar endDateThisLesson = this.endDate;
			if (endDateThisLesson==null) {
				if (this.getCourse()!=null && this.getCourse().getTerm() != null) {
					endDateThisLesson = this.getCourse().getTerm().getEndDate();
				}
			}
			Calendar startDateOtherLesson = other.startDate;
			if (startDateOtherLesson==null) {
				if (other.getCourse()!=null && other.getCourse().getTerm() != null) {
					startDateOtherLesson = other.getCourse().getTerm().getStartDate();
				}
			}			
			Calendar endDateOtherLesson = other.endDate;
			if (endDateOtherLesson==null) {
				if (other.getCourse()!=null && other.getCourse().getTerm() != null) {
					endDateOtherLesson = other.getCourse().getTerm().getEndDate();
				}
			}			
			
			long startDateThisLessonInMillis = startDateThisLesson != null ? startDateThisLesson.getTimeInMillis() : 0;
			long endDateThisLessonInMillis = endDateThisLesson != null ? endDateThisLesson.getTimeInMillis() : 0;
			long startDateOtherLessonInMillis = startDateOtherLesson != null ? startDateOtherLesson.getTimeInMillis() : 0;
			long endDateOtherLessonInMillis = endDateOtherLesson != null ? endDateOtherLesson.getTimeInMillis() : 0;
			
			//Wir vergleichen die Zeiträume der Lessons...
			//Mögliche Überlappungen:
			//Konfliktmöglichkeit 1: StartDatum von Lesson1 (L1) vor Startdatum von Lesson2 (L2) _und_ Enddatum von L1 nach StartDatum von L2  
			//Variante1 von K1:
			//L1 .S....E.......
			//L2 ...S....E.....
			//Variante2 von K1:
			//L1 .S.........E..
			//L2 ...S....E.....			
			//Konfliktmöglichkeit 2: StartDatum von Lesson1 (L1) nach Startdatum von Lesson2 (L2) _und_ Startdatum von L1 nach StartDatum von L2
			//Variante1 von K2:
			//L1 ....S..E......
			//L2 ...S.....E....
			//Variante2 von K2:
			//L1 ......S....E..
			//L2 ...S.....E....
			if ((startDateThisLessonInMillis <= startDateOtherLessonInMillis && endDateThisLessonInMillis >= startDateOtherLessonInMillis) || //K1
				(startDateThisLessonInMillis >= startDateOtherLessonInMillis && startDateThisLessonInMillis <= endDateOtherLessonInMillis)) { //K2
				//Die Zeiträume überlappen sich, also überprüfen wir ob sich der TimeSlot überlappt
				if (this.timeSlot!=null) {
					return this.timeSlot.conflicts(other.timeSlot);
				}
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return room + ": " + timeSlot;
	}

}
