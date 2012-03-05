package de.beuth.sp.belegsystem.tapestry.pages.course;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.ActionLink;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.db.manager.CourseManager;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Participant;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse zur Anzeige der {@link Participant}s eines {@link Course}.
 * Enthält auch einen Entfernen-{@link ActionLink} der aber nur von einem
 * {@link Admin} erfolgreich benutzt werden kann, ein {@link Instructor} hat für
 * den Aufruf der
 * {@link CourseManager#removeParticipantFromCourse(Course, Participant)}
 * -Methode nicht genügend Rechte.
 * 
 * 
 */
@Restricted(allowedFor = {Instructor.class, Admin.class}, onlyIfAssociatedToProgram = true)
public class CourseParticipantList extends BasePageImpl {
	
	@PageActivationContext
	@Property
	private Course course;
	
	/**
	 * Row-Variable für das Grid
	 */
	@SuppressWarnings("unused")
	@Property
	private Participant participant;
	
	@Inject
	private CourseManager courseManager;
	
	@InjectComponent
	private Zone participantListZone; 
	
	Object onActivate() {
		if (course==null) {
			return InstructorCourseView.class;
		}
		return null;
	}
	
	@CommitAfter
	Object onActionFromRemoveParticipant(Participant participant) {
		courseManager.removeParticipantFromCourse(course, participant);
		return participantListZone.getBody();
	}
	
}
