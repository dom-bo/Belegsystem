package de.beuth.sp.belegsystem.db.dao;

import java.util.List;

import de.beuth.sp.belegsystem.lg.TimeSlot;
import de.beuth.sp.belegsystem.lg.TimeSlot.DayOfWeek;

/**
 * 
 * DAO-Interface für die {@link TimeSlot}-Klasse. Erweitert das generische {@link DAO} um
 * spezifische Methoden für {@link TimeSlot}s.
 * 
 * 
 */
public interface TimeSlotDAO extends DAO<TimeSlot> {

	/**
	 * TODO Java-Doc-Kommentar!
	 * @param dayOfWeek
	 * @param hourOfDay
	 * @param minuteOfHour
	 * @return
	 */
	TimeSlot getTimeSlot(DayOfWeek dayOfWeek, int hourOfDay, int minuteOfHour);
	
	/**
	 * TODO Java-Doc-Kommentar!
	 * @param dayOfWeek
	 * @return
	 */
	List<TimeSlot> getTimeSlotsForDay(DayOfWeek dayOfWeek);
	
	/**
	 * TODO Java-Doc-Kommentar!
	 * @param hours
	 * @param minutes
	 * @return
	 */
	List<TimeSlot> getTimeSlotsForTime(Integer hours, Integer minutes);
	
}
