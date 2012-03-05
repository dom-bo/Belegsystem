package de.beuth.sp.belegsystem.lg;

import javax.persistence.Entity;

import de.beuth.sp.belegsystem.lg.base.PersistentImpl;

/**
 * Klasse zum Festhalten der verwandten Zeitschlitze für Unterrichtseinheiten.
 * Hierbei wird ein Wochenschema angewandt. Ein Timeslot findet an einem
 * bestimmten Tag in der Woche zu einer bestimmten Zeit statt (Stunde/Minute)
 * und hat eine definierte Länge. Kurse im System können auch mehrere
 * Zeitschlitze haben, z.B. Doppelblöcke.
 * 
 */
@Entity
public class TimeSlot  extends PersistentImpl {

	private static final long serialVersionUID = -4727324830072060268L;

	public enum DayOfWeek {
		SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
	}

	/**
	 * Tag, an dem sich der Zeitschlitz befindet
	 */
	private DayOfWeek dayOfWeek;
	
	/**
	 * Die Stunde an dem Tag
	 */
	private Integer hourOfDay;
	
	/**
	 * Die Minute
	 */
	private Integer minuteOfHour;
	
	/**
	 * Die Dauer des Zeitschlitzes
	 */
	private Integer durationInMinutes;

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(final DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public Integer getHourOfDay() {
		return hourOfDay;
	}
		
	public void setHourOfDay(final Integer hourOfDay) {
		this.hourOfDay = hourOfDay;
	}

	public Integer getMinuteOfHour() {
		return minuteOfHour;
	}
	
	public void setMinuteOfHour(final Integer minuteOfHour) {
		this.minuteOfHour = minuteOfHour;
	}

	public Integer getDurationInMinutes() {
		return durationInMinutes;
	}

	public void setDurationInMinutes(final Integer durationInMinutes) {
		this.durationInMinutes = durationInMinutes;
	}

	public Integer getEndingHourOfDay() {
		return (hourOfDay + ((minuteOfHour + durationInMinutes)/60)) % 24;
	}	
	
	public Integer getEndingMinuteOfHour() {
		return (minuteOfHour + durationInMinutes) % 60;
	}
	
	public Integer getStartingMinuteOfDay() {
		return getHourOfDay() * 60 + getMinuteOfHour();
	}
	
	public Integer getEndingMinuteOfDay() {
		return getEndingHourOfDay() * 60 + getEndingMinuteOfHour();
	}
	
	@Override
	public String toString() {		
		return String.format("%s %02d:%02d", dayOfWeek, hourOfDay, minuteOfHour);
	}

	/**
	 * Prüft, ob sich die ein Zeitschlitz mit einem 
	 * anderen Zeitschlitz überlagert. 
	 * 
	 * @param other
	 * @return
	 */
	public boolean conflicts(TimeSlot other) {
		if (this.equals(other)) {
			return true;
		}
		if (this.getDayOfWeek().equals(other.getDayOfWeek())) {
			//Überprüfung ähnlich wie bei Lesson
			if ((this.getStartingMinuteOfDay() < other.getStartingMinuteOfDay() && this.getEndingMinuteOfDay() > other.getStartingMinuteOfDay()) ||
				(this.getStartingMinuteOfDay() > other.getStartingMinuteOfDay() && this.getStartingMinuteOfDay() < other.getEndingMinuteOfDay())) {
				return true;
			}
		}
		return false;
	}	

	

}