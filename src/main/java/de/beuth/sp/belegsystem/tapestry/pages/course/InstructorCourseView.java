package de.beuth.sp.belegsystem.tapestry.pages.course;

import java.util.Set;

import org.apache.tapestry5.annotations.Property;

import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse zur Standardansicht des Instructors. Diese Page-Klasse dient
 * dazu, auf der Standardansicht des Instructor die jeweiligen Kurse des
 * Instructors zur Verfügung zu stellen.
 * 
 */

@Restricted(allowedFor = { Instructor.class })
public class InstructorCourseView extends BasePageImpl {

	@SuppressWarnings("unused")
	@Property
	private Course course;

	/**
	 * @return gibt ein Set mit den Kursen des jeweiligen Dozenten zurück. Um
	 *         das Set zurück zu geben, muss zunächst nochmal die Session des
	 *         eingeloggten Instructors neu geladen werden.
	 */
	public Set<Course> getCourses() {
		Instructor instructor = this.getLoggedInUser().getRoleOfType(
				Instructor.class);
		return instructor.getCourses();
	}
}
