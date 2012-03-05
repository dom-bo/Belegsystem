package de.beuth.sp.belegsystem.tapestry.components;

import java.util.List;
import java.util.Set;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.db.manager.TimeSlotManager;
import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Lesson;
import de.beuth.sp.belegsystem.lg.Participant;
import de.beuth.sp.belegsystem.lg.TimeSlot;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;

/**
 * Eine "Reihe" des Stundenplans (Schedule). Eine Reihe ist ein Zeitschlitz, der
 * an jedem Tag einmalig stattfindet (er wird durch die Anfangszeit bestimmt).
 * Die Aufteilung in Reihen ist durch die Definition einer Tabelle in HTML
 * bestimmt.
 * 
 * Die Reihe ist dafür zuständig, für jeden Tag für diesen Zeitschlitz zu
 * überprüfen, ob ein Kurs stattfindet, um ihn anzuzeigen.
 * 
 * 
 */
public class ScheduleRow {

	/**
	 * Managerklasse um auf die Zeitschlitze zuzugreifen.
	 */
	@Inject
	private TimeSlotManager timeSlotManager;

	/**
	 * Hilfsklasse um auf die Properties (Strings) zur Lokalisierung
	 * zuzugreifen.
	 */
	@Inject
	private Messages messages;

	/**
	 * Der Zeitschlitz der von der Komponente während der Schleife in der TML
	 * geprüft wird. Das Attribut dient als Input für die Methode
	 * isCourseBooked.
	 */
	@Property
	private TimeSlot timeSlot;

	/**
	 * Anfangszeit (Stunde), wird als Parameter an die Komponente übergeben.
	 */
	@Property
	@Parameter(required = true)
	private Integer hours;

	/**
	 * Anfangszeit (Minuten), wird als Parameter an die Komponente übergeben.
	 */
	@Property
	@Parameter(required = true)
	private Integer minutes;

	/**
	 * Alle Kurse des Users, wird als Parameter an die Komponente übergeben (von
	 * Schedule).
	 */
	@Property
	@Parameter(required = true)
	private Set<Course> courses;

	/**
	 * Lokale Variable für die Speicherung während der Schleife in der TML. Die
	 * TML ruft die Methode isCourseBooked auf, welche dieses Attribut setzt,
	 * damit sie in der TML zur Verfügung steht.
	 */
	@SuppressWarnings("unused")
	@Property
	private Course currentCourse;

	/**
	 * Lokale Variable für die Speicherung während der Schleife in der TML. Die
	 * TML ruft die Methode isCourseBooked auf, welche dieses Attribut setzt,
	 * damit sie in der TML zur Verfügung steht.
	 */
	@SuppressWarnings("unused")
	@Property
	private Lesson currentLesson;

	/**
	 * Lokale Variable für die Speicherung während der Schleife in der TML. Die
	 * TML ruft die Methode isCourseBooked auf, welche dieses Attribut setzt,
	 * damit sie in der TML zur Verfügung steht.
	 */
	@SuppressWarnings("unused")
	@Property
	private String currentInstructors;

	/**
	 * Notwendig um auf den eingeloggten User zuzugreifen (für die Überprüfung
	 * der Rolle, da der User nicht an die Komponente übergeben wird, nur eine
	 * Liste seiner Kurse. Die Kurse werden von der übergeordneten Komponente
	 * übergeben damit sie nicht für jede Reihe neu geladen werden müssen).
	 */
	@Inject
	private ComponentResources resources;

	/**
	 * Formatiert die übergebene Zeit wie in den Einstellungen definiert (für
	 * die Anzeige des Stundenplans).
	 * 
	 * @return
	 */
	public String getFormattedTime() {
		return String.format(messages.get("schedule-timeformat"), hours,
				minutes);
	}

	/**
	 * Für den Zeitschlitz an jedem Tag wird geprüft, ob der Benutzer einen Kurs
	 * gebucht hat. Wenn ja, werden die lokalen Attribute (currentCourse,
	 * currentLesson und currentInstructors) mit Werten gefüllt, damit sie in
	 * der TML angezeigt werden können. Diese Methode wird direkt aus der TML
	 * heraus aufgerufen.
	 * 
	 * Vor dem Aufruf muss das Attribute timeSlot gesetzt werden (es dient als
	 * Input für die Methode).
	 * 
	 * @return True wenn der Benutzer einen Kurs an dem TimeSlot gebucht hat.
	 */
	public boolean isCourseBooked() {
		currentCourse = null;
		currentLesson = null;
		for (Course course : courses) {
			for (Lesson lesson : course.getLessons()) {
				if (lesson.getTimeSlot().equals(timeSlot)) {
					currentCourse = course;
					currentLesson = lesson;

					final Instructor instructors[] = new Instructor[course
							.getInstructors().size()];

					course.getInstructors().toArray(instructors);

					final StringBuilder stringBuilder = new StringBuilder();
					for (int i = 0; i < instructors.length; i++) {
						stringBuilder.append(instructors[i].getUser()
								.getLastname());
						if (i < instructors.length - 1) {
							stringBuilder.append(", ");
						}
					}

					currentInstructors = stringBuilder.toString();
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Lädt alle TimeSlots für die übergebene Zeit (für alle Tage).
	 * 
	 * @return Liste alle TimeSlots.
	 */
	public List<TimeSlot> getTimeSlots() {
		return timeSlotManager.getSortedTimeSlotsForTime(hours, minutes);
	}

	public String getTimeSlotId() {
		final StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(timeSlot.getDayOfWeek());
		stringBuilder.append("_");
		stringBuilder.append(timeSlot.getHourOfDay());
		stringBuilder.append(timeSlot.getMinuteOfHour());

		return stringBuilder.toString();
	}

	/**
	 * Prüft ob der Benutzer ein Participant ist.
	 * 
	 * @return True wenn der Benutzer ein Participant ist.
	 */
	public boolean isParticipant() {
		return ((BasePageImpl) resources.getPage()).getLoggedInUser()
				.hasRoleOfType(Participant.class);
	}

	/**
	 * Event-Methode damit übergeordnete Komponenten das Ereignis abfangen
	 * können, sobald der Benutzer einen Kurs storniert (wird hier nicht
	 * behandelt!).
	 * 
	 * @param course
	 *            Der Kurs der storniert wird.
	 * @return False wenn das Event nicht behandelt wird, True andersfalls.
	 */
	boolean onRemoveBooking(final Course course) {
		// only return false, bubbles up, and will be handled above (atm:
		// BookCourse)
		return false;
	}

	/**
	 * Event-Methode damit übergeordnete Komponenten das Ereignis abfangen
	 * können, sobald der Benutzer auf einen Block klickt um zu suchen (wird
	 * hier nicht behandelt!).
	 * 
	 * @param course
	 *            Der Zeitschlitz auf den geklickt wurde.
	 * @return False wenn das Event nicht behandelt wird, True andersfalls.
	 */
	boolean onSearchForTimeSlot(final TimeSlot timeSlot) {
		// only return false, bubbles up, and will be handled above (atm:
		// BookCourse)
		return false;
	}

}
