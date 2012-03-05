package de.beuth.sp.belegsystem.tapestry.pages.course;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.db.manager.CourseManager;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * 
 * Seite mit einer Liste aller Kurse. Nur für Admin zugänglich.
 * 
 *
 */
@Restricted(allowedFor = { Admin.class })
public class ListCourse extends BasePageImpl {

	@Inject
	private CourseManager courseManager;
	
	@SuppressWarnings("unused")
	@Property
	private Course course;
	
	public List<Course> getCourses() {
		return courseManager.getDAO().getAll();
	}
		
	public void onActionFromDelete(final Course course) {			
		courseManager.deleteAndCommit(course);		
	}
}
