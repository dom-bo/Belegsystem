package de.beuth.sp.belegsystem.db.manager.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.db.dao.TimeSlotDAO;
import de.beuth.sp.belegsystem.db.manager.TimeSlotManager;
import de.beuth.sp.belegsystem.lg.TimeSlot;
import de.beuth.sp.belegsystem.lg.TimeSlot.DayOfWeek;

/**
 * Implementation des TimeSlotManager-Interfaces, erweitert generische ManagerImpl-Klasse
 * (typisiert auf TimeSlot) und implementiert die zusätzlichen Methoden des
 * TimeSlotManager-Interface.
 * 
 *
 */
public class TimeSlotManagerImpl extends ManagerImpl<TimeSlot> implements TimeSlotManager {
	
	@Inject
	private TimeSlotDAO timeSlotDAO;

	@Override
	public TimeSlotDAO getDAO() {
		return timeSlotDAO;
	}

	/* (non-Javadoc)
	 * @see de.beuth.sp.belegsystem.db.manager.TimeSlotManager#getAllSortedTimeSlots()
	 */
	@Override
	public List<TimeSlot> getAllSortedTimeSlots() {
		List<TimeSlot> timeslots = timeSlotDAO.getAll();
		Collections.sort(timeslots, new Comparator<TimeSlot>() {
			public int compare(TimeSlot o1, TimeSlot o2) {
				int result = o1.getDayOfWeek().compareTo(o2.getDayOfWeek());
				if (result==0) {
					result = o1.getHourOfDay().compareTo(o2.getHourOfDay());
				}
				return result;
			}
		});
		return timeslots;
	}
	
	/* (non-Javadoc)
	 * @see de.beuth.sp.belegsystem.db.manager.TimeSlotManager#getSortedTimeSlotsForDay(de.beuth.sp.belegsystem.lg.TimeSlot.DayOfWeek)
	 */
	@Override
	public List<TimeSlot> getSortedTimeSlotsForDay(DayOfWeek dayOfWeek) {
		List<TimeSlot> timeslots = timeSlotDAO.getTimeSlotsForDay(dayOfWeek);
		Collections.sort(timeslots, new TimeSlotComparator());
		return timeslots;
	}
	
	/* (non-Javadoc)
	 * @see de.beuth.sp.belegsystem.db.manager.TimeSlotManager#getSortedTimeSlotsForTime(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<TimeSlot> getSortedTimeSlotsForTime(Integer hours, Integer minutes) {
		List<TimeSlot> timeslots = timeSlotDAO.getTimeSlotsForTime(hours, minutes);
		Collections.sort(timeslots, new TimeSlotComparator());
		return timeslots;
	}	
	
	
	private static final class TimeSlotComparator implements Comparator<TimeSlot> {
		public int compare(TimeSlot o1, TimeSlot o2) {
			int result = o1.getDayOfWeek().compareTo(o2.getDayOfWeek());
			if (result==0) {
				result = o1.getHourOfDay().compareTo(o2.getHourOfDay());
			}
			return result;
		}
	}
}
