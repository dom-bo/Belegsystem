package de.beuth.sp.belegsystem.db.manager;

import java.util.List;

import de.beuth.sp.belegsystem.lg.TimeSlot;
import de.beuth.sp.belegsystem.lg.TimeSlot.DayOfWeek;

/**
 * Manager für TimeSlots. Zuständig für spezielle Aufbereitung der TimeSlots,
 * d.h. in erster Line richtige Sortierung nach Zeit.
 * 
 * 
 */
public interface TimeSlotManager extends Manager<TimeSlot> {

	/**
	 * Gibt alle TimeSlots sortiert nach Zeit (Tag/Uhrzeit) zurück
	 * @return Liste mit allen TimeSlots, sortiert nach Zeit (Tag/Uhrzeit)
	 */
	List<TimeSlot> getAllSortedTimeSlots();
	
	/**
	 * Gibt alle TimeSlots eines bestimmten Tages nach Zeit (Tag/Uhrzeit) zurück
	 * @param dayOfWeek der Tag der Woche
	 * @return Liste mit allen TimeSlots des jeweiligen Tages, sortiert nach Zeit (Tag/Uhrzeit)
	 */
	List<TimeSlot> getSortedTimeSlotsForDay(DayOfWeek dayOfWeek);

	/**
	 * Gibt alle TimeSlots zurück die die übergebene Länge in Stunden und Minuten haben sortiert nach Zeit (Tag/Uhrzeit) zurück 
	 * @param hours die Anzahl von Stunden die die TimeSlots haben sollen
	 * @param minutes die Anzahl von Stunden die die TimeSlots haben sollen
	 * @return Liste mit allen TimeSlots die der übergebenen Länge in Stunden und Minuten entsprechen, sortiert nach Zeit (Tag/Uhrzeit)
	 */
	List<TimeSlot> getSortedTimeSlotsForTime(Integer hours, Integer minutes);

}