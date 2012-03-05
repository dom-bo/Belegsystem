package de.beuth.sp.belegsystem.tapestry.pages.course;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import de.beuth.sp.belegsystem.db.dao.CourseDAO;
import de.beuth.sp.belegsystem.db.dao.InstructorDAO;
import de.beuth.sp.belegsystem.db.dao.ProgramDAO;
import de.beuth.sp.belegsystem.db.dao.TermDAO;
import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.lg.Course.CourseFullExc;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Lesson;
import de.beuth.sp.belegsystem.lg.Participant;
import de.beuth.sp.belegsystem.lg.Participant.CourseAlreadyAddedExc;
import de.beuth.sp.belegsystem.lg.Program;
import de.beuth.sp.belegsystem.lg.Term;
import de.beuth.sp.belegsystem.lg.TimeSlot;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse für die Buchungsansicht des Studenten. Nur für
 * {@link Participant}s (=Studenten) erlaubt.
 * 
 * 
 */
@Import(library = "BookCourse.js")
@Restricted(allowedFor = { Participant.class })
public class BookCourse extends BasePageImpl {
	static final private Integer[] LEVELS = { 1, 2, 3, 4, 5, 6 };
	static final private Integer[] STUDYGROUPS = { 1, 2 };

	@Persist
	private boolean initialised;

	/**
	 * Attribut speichert den ausgewählten Studiengang der Suche. Tapestry setzt
	 * dieses Attribut aufgrund der Annotation.
	 * 
	 */
	@Property
	@Persist
	private Program program;

	/**
	 * Attribut speichert den ausgewählten Zeitschlitz der Suche. Tapestry setzt
	 * dieses Attribut aufgrund der Annotation.
	 * 
	 */
	@Property
	@Persist
	private TimeSlot timeSlot;

	/**
	 * Attribut speichert das ausgewählte Semester der Suche. Tapestry setzt
	 * dieses Attribut aufgrund der Annotation.
	 * 
	 */
	@Property
	@Persist
	private Integer level;

	@SuppressWarnings("unused")
	@Property
	private Integer[] studygroups = STUDYGROUPS;

	@Property
	@Persist
	private Integer studygroup;

	/**
	 * Attribut speichert den ausgewählten Dozenten der Suche. Tapestry setzt
	 * dieses Attribut aufgrund der Annotation.
	 * 
	 */
	@Property
	@Persist
	private Instructor instructor;

	/**
	 * Loop-Variable
	 */
	@SuppressWarnings("unused")
	@Property
	private Instructor loopinstructor;

	/**
	 * Attribut speichert das ausgewählten Semester der Suche. Tapestry setzt
	 * dieses Attribut aufgrund der Annotation.
	 * 
	 */
	@Property
	@Persist
	private Term term;

	/**
	 * Speichert den Suchtext des Benutzers.
	 */
	@Property
	@Persist
	private String search;

	/**
	 * Lokale Variable für die TML (speichert während einer Schleife den Wert):
	 */
	@Property
	private Course course;

	/**
	 * Lokale Variable für die TML (speichert während einer Schleife den Wert):
	 */
	@Property
	private Lesson lesson;

	/**
	 * DAO-Klasse um den persistenten Benutzer zu laden.
	 */
	@Inject
	private ProgramDAO programDAO;

	/**
	 * DAO-Klasse um den persistenten Benutzer zu laden.
	 */
	@Inject
	private InstructorDAO instructorDAO;

	/**
	 * DAO-Klasse um den persistenten Benutzer zu laden.
	 */
	@Inject
	private TermDAO termDAO;

	/**
	 * DAO-Klasse um den persistenten Benutzer zu laden.
	 */
	@Inject
	private CourseDAO courseDAO;

	@Inject
	private Request request;

	/**
	 * Hilfsklasse um auf die Properties (Strings) zur Lokalisierung
	 * zuzugreifen.
	 */
	@Inject
	private Messages messages;

	/**
	 * Tapestry Ajax-Zone
	 */
	@Component
	private Zone listZone;

	/**
	 * Tapestry Ajax-Zone
	 */
	@Component
	private Zone formZone;

	/**
	 * Tapestry Ajax-Zone
	 */
	@Component
	private Zone timetableZone;

	/**
	 * Der Student für welchen diese Seite aufgebaut wird.
	 */
	private Participant participant;

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

	private boolean canceled;

	/**
	 * Gibt alle Studiengänge für die GUI zurück.
	 * 
		 * @return
	 */
	public List<Program> getAllPrograms() {
		return programDAO.getAll();
	}

	/**
	 * Gibt alle Dozenten für die GUI zurück
	 * 
		 * @return
	 */
	public List<Instructor> getAllInstructors() {
		return instructorDAO.getAll();
	}

	/**
	 * 
		 * @return
	 */
	public List<Term> getAllTerms() {
		return termDAO.getAll();
	}

	/**
	 * Gibt eine Liste mit Kursen zurück, die entsprechend zu den Suchparameter
	 * zurückgegeben werden.
	 * 
		 * @return
	 */
	public List<Course> getSearchCourses() {
		return courseDAO.getCourses(program, instructor, level, studygroup,
				term, search, timeSlot);
	}

	/**
	 * Wird vor dem rendern der Seite aufgrufen und initialisiert die
	 * Startwerte.
	 */
	void setupRender() {
		if (!initialised) {
			term = termDAO.getTermAtDate(Calendar.getInstance());
			program = participant.getProgram();
			search = null;
			level = null;
			studygroup = null;
			instructor = null;
			initialised = true;
			timeSlot = null;
		}
	}

	/**
	 * Holt nach dem Aufbau der Seite den aktuellen User.
	 */
	void onActivate() {
		participant = getLoggedInUser().getRoleOfType(Participant.class);
	}

	/**
	 * 
		 */	
	void onSuccess() {
		if (canceled) {
			initialised = false;
			setupRender();
		}
		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(listZone).addRender(formZone);
		}
	}

	/**
	 * 
		 */	
	void onSelectedFromCancel() {
		canceled = true;
	}

	/**
	 * 
		 */	
	void onValueChangedFromProgram(Program program) {
		this.program = program;
		// da wir auch die Zahl der Fachsemester möglicherweise updaten
		ajaxResponseRenderer.addRender(listZone).addRender(formZone);
	}

	/**
	 * 
		 */	
	Object onValueChangedFromLevel(Integer level) {
		this.level = level;
		return listZone.getBody();
	}

	/**
	 * 
		 */	
	Object onValueChangedFromStudygroup(Integer studygroup) {
		this.studygroup = studygroup;
		return listZone.getBody();
	}

	Object onValueChangedFromInstructor(Instructor instructor) {
		this.instructor = instructor;
		return listZone.getBody();
	}

	/**
	 * 
		 */	
	Object onValueChangedFromTerm(Term term) {
		this.term = term;
		return listZone.getBody();
	}

	/**
	 * 
		 */	
	Object onSearchChanged() {
		search = request.getParameter("param");
		return listZone.getBody();
	}

	/**
	 * Wenn nicht mehr nach Blöcken gesucht werden soll muss das Attribut auf
	 * null gesetzt werden.
	 * 
		 * @return
	 */
	Object onResetTimeSlot() {
		this.timeSlot = null;
		return listZone.getBody();
	}

	/**
	 * Generiert einen lokalisierten String, wenn auf ein Block geklickt wurde
	 * (um einen Kurs zu suchen).
	 * 
		 * @return Der lokalisierte String als Warnmeldung.
	 */
	public String getActiveTimeSlotSelectionWarning() {
		String timeSlotString = messages
				.get(timeSlot.getDayOfWeek().toString())
				+ " "
				+ messages.format("timeslot_timeformat",
						timeSlot.getHourOfDay(), timeSlot.getMinuteOfHour(),
						timeSlot.getEndingHourOfDay(),
						timeSlot.getEndingMinuteOfHour());

		return messages.format("timeslot-warning", timeSlotString);
	}

	/**
	 * 
		 */	
	public String getLessonTimeSlot() {
		TimeSlot timeSlot = lesson.getTimeSlot();
		return messages.get(timeSlot.getDayOfWeek().toString())
				+ " "
				+ messages.format("timeslot_timeformat",
						timeSlot.getHourOfDay(), timeSlot.getMinuteOfHour(),
						timeSlot.getEndingHourOfDay(),
						timeSlot.getEndingMinuteOfHour());
	}

	/**
	 * 
		 */	
	public String getNumbersOfWorkload() {
		return String.format(messages.get("workload-format"), course
				.getParticipants().size(), course.getMaxParticipants());
	}

	/**
	 * Generiert einen JavaScript-Code (Array) welcher den JavaScript-Funktionen
	 * übergeben werden kann. Das ist notwendig, um eine beliebige Anzahl von
	 * Kursen bzw. Blöcken im Stundenplan zu markieren (highlighting).
	 * 
		 * @return
	 */
	public String getTimeSlotIdArray() {

		final Lesson lessons[] = new Lesson[course.getLessons().size()];
		course.getLessons().toArray(lessons);

		StringBuilder stringBuilder = new StringBuilder("[");
		for (int i = 0; i < lessons.length; i++) {

			TimeSlot timeSlot = lessons[i].getTimeSlot();
			stringBuilder.append("'");
			stringBuilder.append(timeSlot.getDayOfWeek());
			stringBuilder.append("_");
			stringBuilder.append(timeSlot.getHourOfDay());
			stringBuilder.append(timeSlot.getMinuteOfHour());
			stringBuilder.append("'");

			if (i < lessons.length - 1) {
				stringBuilder.append(", ");
			}
		}

		stringBuilder.append("]");
		return stringBuilder.toString();
	}

	/**
		 * @param course
	 */
	@CommitAfter
	void onBooking(final Course course) {
		try {
			course.addParticipant(participant);
		} catch (CourseFullExc e) {
			throw new RuntimeException(e);
		} catch (CourseAlreadyAddedExc e) {
			throw new RuntimeException(e);
		}
		if (request.isXHR())
			ajaxResponseRenderer.addRender(listZone).addRender(timetableZone);
	}

	/**
	 * 
		 */	
	@CommitAfter
	void onRemoveBooking(final Course course) {
		course.removeParticipant(participant);
		if (request.isXHR())
			ajaxResponseRenderer.addRender(listZone).addRender(timetableZone);
	}

	/**
	 * Event-Handler wenn nach einem TimeSlot gesucht wird (klick auf einen
	 * Block im Stundenplan). Das Event wird von der Komponente weitergereicht.
	 * 
		 * @param timeSlot
	 * @return
	 */
	Object onSearchForTimeSlot(TimeSlot timeSlot) {
		this.timeSlot = timeSlot;
		return listZone.getBody();
	}

	/**
	 * Prüft ob der Kurs bereits vom Participant gebucht wurde. Wird für die
	 * JavaScript-Funktionen benötigt und um zu bestimmen, ob "stornieren"
	 * angezeigt wird oder nicht.
	 * 
		 * @return
	 */
	public boolean isCourseAlreadyBooked() {
		return participant.hasCourse(course);
	}

	/**
	 * Prüft ob ein Kurs buchbar ist oder nicht (ob er sich mit einem bereits
	 * gebuchten Kurs überschneidet). Wird verwendet um der JavaScript-Funktion
	 * ein true oder false zu übergeben (um die Farbe für das Highlighting zu
	 * bestimmen).
	 * 
		 * @return True wenn sich der Kurs nicht mit einem anderen schneidet.
	 */
	public boolean isBookable() {
		for (Lesson lesson : course.getLessons()) {
			final TimeSlot timeSlot = lesson.getTimeSlot();

			for (Course userCourse : participant.getCourses()) {
				if (!userCourse.equals(course)) {
					for (Lesson userLesson : userCourse.getLessons()) {
						if (userLesson.getTimeSlot().equals(timeSlot)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	/**
	 * 
		 */	
	public Integer[] getLevels() {
		if (program != null) {
			if (level != null && level > program.getLevels()) {
				level = null;
			}
			return Arrays.copyOf(LEVELS, program.getLevels());
		}
		return LEVELS;
	}
}
