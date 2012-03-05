package de.beuth.sp.belegsystem.tapestry.pages.course;

import java.util.Set;

import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.db.dao.CourseDAO;
import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Participant;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.pages.Index;
import de.beuth.sp.belegsystem.tapestry.pages.participant.ListParticipant;
import de.beuth.sp.belegsystem.tapestry.services.AccessFilter;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * 
 * Page-Klasse zum Bearbeiten eines Kurses durch einen der dort eingetragenen
 * Instructors. Diese Page-Klasse dient dazu, dem Instructor zu ermoeglichen,
 * die Kursbeschreibung in einem seiner Kurse zu bearbeiten.
 * 
 */
@Restricted(allowedFor = { Instructor.class })
public class InstructorCourseEdit extends BasePageImpl {

	@PageActivationContext
	@Property
	private Course course;

	@Inject
	private CourseDAO courseDAO;

	@SuppressWarnings("unused")
	@Property
	private Participant participant;

	public Set<Participant> getParticipants() {
		return course.getParticipants();
	}

	public String getCourseDescription() {
		return course.getDescription();
	}

	
	/**
	 * @return Rückgabewert ist hier quasi die Weiterleitung zur nächsten Website mit gleichzeitiger Sicherheitsabfrage.
	 */
	@CommitAfter
	Object onSuccessFromDescriptionForm() {
		courseDAO.saveOrUpdate(course);
		if (AccessFilter.checkAccess(getLoggedInUser(),
				ListParticipant.class.getAnnotation(Restricted.class))) {
			return ListParticipant.class;
		} else {
			return Index.class;
		}
	}
}
