package de.beuth.sp.belegsystem.db.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import de.beuth.sp.belegsystem.db.dao.CourseDAO;
import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Participant;
import de.beuth.sp.belegsystem.lg.Program;
import de.beuth.sp.belegsystem.lg.Term;
import de.beuth.sp.belegsystem.lg.TimeSlot;

/**
 * 
 * Implementation des CourseDAO-Interfaces, erweitert generische DAOImpl-Klasse
 * (typisiert auf Course) und implementiert die zusätzlichen Methoden des
 * CourseDAO-Interface.
 * 
 * 
 */
public class CourseDAOImpl extends DAOImpl<Course> implements CourseDAO {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.beuth.sp.belegsystem.db.dao.CourseDAO#getCourses(de.beuth.sp.belegsystem
	 * .lg.Program, de.beuth.sp.belegsystem.lg.Instructor, int, int)
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Course> getCourses(final Program program,
			final Instructor instructor, final Integer level,
			final Integer studygroup, final Term term, final String search,
			final TimeSlot timeSlot) {

		final Criteria criteria = this.getSession()
				.createCriteria(Course.class);

		// Fügt Bedingung für den Studiengang hinzu
		if (program != null) {
			criteria.add(Restrictions.eq("program", program));
		}
		// Fügt Bedingung für das Semester hinzu
		if (level != null) {
			criteria.add(Restrictions.eq("level", level.intValue()));
		}
		// Fügt Bedingung für den Zug hinzu
		if (studygroup != null) {
			criteria.add(Restrictions.eq("studyGroup", studygroup.intValue()));
		}
		// Fügt Bedingung für den Zug hinzu
		if (term != null) {
			criteria.add(Restrictions.eq("term", term));
		}
		if (timeSlot != null) {
			criteria.createCriteria("lessons")
					.createCriteria("timeSlot")
					.add(Restrictions.eq("hourOfDay", timeSlot.getHourOfDay()))
					.add(Restrictions.eq("minuteOfHour", timeSlot.getMinuteOfHour()))
					.add(Restrictions.eq("dayOfWeek", timeSlot.getDayOfWeek()));
		}		

		if (search != null) {
			criteria.add(Restrictions.or(Restrictions.or(
					Restrictions.like("courseIdentifier", search + "%"),
					Restrictions.like("title", search + "%")), Restrictions
					.like("description", search + "%")));
		}

		// Fügt Bedingung für den Dozenten(Instructur) hinzu
		if (instructor != null) {
			criteria.createCriteria("instructors").add(
					Restrictions.eq("id", instructor.getId()));
		}
		return criteria.list();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.beuth.sp.belegsystem.db.dao.CourseDAO#removeParticipantFromCourse(de.beuth.sp.belegsystem.lg.Course, de.beuth.sp.belegsystem.lg.Participant)
	 */
	@Override
	public void removeParticipantFromCourse(Course course, Participant participant) {
		getSession()
		.createSQLQuery(
				"DELETE FROM participant_course " +
				"WHERE courses_id = :course.id " +
				"AND participants_id = :participant.id")
				.setString("course.id", course.getId().toString())
				.setString("participant.id", participant.getId().toString())
		.executeUpdate();				
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.beuth.sp.belegsystem.db.dao.CourseDAO#clearParticipants(de.beuth.sp.belegsystem.lg.Course)
	 */
	@Override
	public void clearParticipants(Course course) {
		getSession()
				.createSQLQuery(
						"DELETE FROM participant_course WHERE courses_id = :id")
				.setString("id", course.getId().toString()).executeUpdate();
	}
}
