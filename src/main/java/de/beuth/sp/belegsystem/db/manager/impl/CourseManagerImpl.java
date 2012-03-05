package de.beuth.sp.belegsystem.db.manager.impl;

import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.db.dao.CourseDAO;
import de.beuth.sp.belegsystem.db.dao.InstructorDAO;
import de.beuth.sp.belegsystem.db.manager.CourseManager;
import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Participant;

/**
 * Implementation des CourseManager-Interfaces, erweitert generische ManagerImpl-Klasse
 * (typisiert auf Course) und implementiert die zusätzlichen Methoden des
 * CourseManager-Interface.
 * 
 *
 */
public class CourseManagerImpl extends ManagerImpl<Course> implements CourseManager {

	@Inject
	private CourseDAO dao;
	
	@Inject
	private InstructorDAO instructorDAO;

	@Override
	public CourseDAO getDAO() {
		return dao;
	}
		
	@Override	
	public void delete(Course course) {
		getDAO().clearParticipants(course);
		for (Instructor instructor : course.getInstructors()) {
			instructor.removeCourse(course);
			instructorDAO.saveOrUpdate(instructor);
		}		
		getDAO().delete(course);
	}
	
	@Override
	public void deleteAndCommit(Course course) {
		delete(course);
	}
	
	@Override
	public void removeParticipantFromCourse(Course course, Participant participant) {
		getDAO().removeParticipantFromCourse(course, participant);		
	}
	
	@Override
	public void removeParticipantFromCourseAndCommit(Course course, Participant participant) {
		removeParticipantFromCourse(course, participant);		
	}
}
