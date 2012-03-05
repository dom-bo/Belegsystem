package de.beuth.sp.belegsystem.tapestry.pages.course;

import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Lesson;
import de.beuth.sp.belegsystem.lg.Participant;
import de.beuth.sp.belegsystem.lg.TimeSlot;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse für die Detailansicht eines Kurses, der vom Participant belegt
 * wurde. Die Klasse lädt im Prinzip nur alle Daten und stellt sie der TML zur
 * Verfügung.
 * 
 * 
 */
@Restricted(allowedFor = { Participant.class })
public class DetailView extends BasePageImpl {

	/**
	 * Der Kurs von dem die Eigenschaften angezeigt werden sollen. Wird der Page
	 * als Parameter übergeben.
	 */
	@PageActivationContext
	@Property
	private Course course;

	/**
	 * Hilfsklasse um auf die Properties (Strings) zur Lokalisierung
	 * zuzugreifen.
	 */
	@Inject
	private Messages messages;

	/**
	 * Lokale Variable für TML-Datei, um in einer Schleife durch alle
	 * Instructors zu gehen.
	 */
	@SuppressWarnings("unused")
	@Property
	private Instructor loopinstructor;

	/**
	 * Lokale Variable für TML-Datei, um in einer Schleife durch alle Blöcke des
	 * Kurses zu gehen.
	 */
	@Property
	private Lesson lesson;

	/**
	 * Wandelt den TimeSlot der aktuellen Lesson (in der Schleife der TML) in
	 * einen lesbaren String um.
	 * 
	 * @return Zeitschlitz als lokalisierten String.
	 */
	public String getLessonTimeSlot() {
		final TimeSlot timeSlot = lesson.getTimeSlot();
		return messages.get(timeSlot.getDayOfWeek().toString())
				+ " "
				+ messages.format("timeslot_timeformat",
						timeSlot.getHourOfDay(), timeSlot.getMinuteOfHour(),
						timeSlot.getEndingHourOfDay(),
						timeSlot.getEndingMinuteOfHour());
	}

	/**
	 * Wandelt die Auslastung des Kurses in einen lesbaren String um.
	 * 
	 * @return Auslastung als lokalisierten String.
	 */
	public String getWorkload() {
		return String.format(messages.get("workload-format"), course
				.getParticipants().size(), course.getMaxParticipants());
	}

}
