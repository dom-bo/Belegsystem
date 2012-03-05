package de.beuth.sp.belegsystem.tapestry.pages.course;

import java.util.Set;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Participant;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse für die Liste alle belegten Kurse eines Studenten. Lädt die
 * persistenten Informationen und stellt sie der TML zur Verfügung.
 * 
 * 
 */
@Restricted(allowedFor = { Participant.class })
public class ParticipantCourseView extends BasePageImpl {

	/**
	 * Lokale Variable für die Schleife in der TML-Datei.
	 */
	@Property
	private Course course;

	/**
	 * Lokale Variable für die Schleife in der TML-Datei.
	 */
	@SuppressWarnings("unused")
	@Property
	private Instructor loopinstructor;

	/**
	 * Hilfsklasse um auf die Properties (Strings) zur Lokalisierung
	 * zuzugreifen.
	 */
	@Inject
	private Messages messages;

	/**
	 * Gibt alle Kurse für den eingelogten User zurück (sofern er ein
	 * Participant ist).
	 * 
	 * @return Alle Kurse des Participant.
	 */
	public Set<Course> getCourses() {
		final Participant participant = this.getLoggedInUser().getRoleOfType(
				Participant.class);
		return participant.getCourses();
	}

	/**
	 * Wandelt die Auslastung des Kurses in einen lesbaren String um.
	 * 
	 * @return Auslastung als lokalisierten String.
	 */
	public String getNumbersOfWorkload() {
		return String.format(messages.get("workload-format"), course
				.getParticipants().size(), course.getMaxParticipants());
	}

	/**
	 * Event-Callback Methode um das Stornieren eines Kurses zu behandelt.
	 * 
	 * @param course
	 *            Der Kurs der storniert werden soll.
	 */
	@CommitAfter
	public void onActionFromRemoveCourse(final Course course) {
		final Participant participant = this.getLoggedInUser().getRoleOfType(
				Participant.class);

		// Kein extra check auf Participant!!! Durch unsere Rollenüberprüfung
		// kann hier gar nichts anderes kommen als ein Participant (wurde vorher
		// schon geprüft)

		participant.cancelCourse(course);
	}
}
