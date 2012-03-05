package de.beuth.sp.belegsystem.db.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import de.beuth.sp.belegsystem.db.dao.TimeSlotDAO;
import de.beuth.sp.belegsystem.lg.TimeSlot;
import de.beuth.sp.belegsystem.lg.TimeSlot.DayOfWeek;

/**
 * 
 * Implementation des TimeSlotDAO-Interfaces, erweitert generische DAOImpl-Klasse
 * (typisiert auf TimeSlot) und implementiert die zusätzlichen Methoden des
 * TimeSlotDAO-Interface.
 * 
 * 
 */
public class TimeSlotDAOImpl extends DAOImpl<TimeSlot> implements TimeSlotDAO {
	
	@Override
	public TimeSlot getTimeSlot(DayOfWeek dayOfWeek, int hourOfDay, int minuteOfHour) {
		@SuppressWarnings("unchecked")
		final List<TimeSlot>  timeSlots = this.getSession().
											createQuery("FROM " + TimeSlot.class.getName() + " " +
														"WHERE dayOfWeek = :dayOfWeek " +
														"AND hourOfDay = :hourOfDay " +
														"AND minuteOfHour = :minuteOfHour")
														.setParameter("dayOfWeek", dayOfWeek)
														.setParameter("hourOfDay", hourOfDay)
														.setParameter("minuteOfHour", minuteOfHour)
														.list();
		if (!timeSlots.isEmpty()) {
			return timeSlots.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TimeSlot> getTimeSlotsForDay(DayOfWeek dayOfWeek) {
		return this.getSession().createQuery("FROM " + TimeSlot.class.getName() + " " +
											 "WHERE dayOfWeek = :dayOfWeek ")
											 .setParameter("dayOfWeek", dayOfWeek)
											 .list();
	}
	
	@SuppressWarnings("unchecked")
	@Override	
	public List<TimeSlot> getTimeSlotsForTime(Integer hours, Integer minutes) {
		
		final Criteria criteria = this.getSession().createCriteria(TimeSlot.class);
		criteria.add(Restrictions.eq("hourOfDay", hours));
		criteria.add(Restrictions.eq("minuteOfHour", minutes));
		
		return criteria.list();	
	}

}
