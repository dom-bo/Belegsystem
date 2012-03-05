package de.beuth.sp.belegsystem.tapestry.pages.course;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.DiscardAfter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.util.AbstractSelectModel;

import de.beuth.sp.belegsystem.db.dao.CourseDAO;
import de.beuth.sp.belegsystem.db.dao.InstructorDAO;
import de.beuth.sp.belegsystem.db.dao.LessonDAO;
import de.beuth.sp.belegsystem.db.dao.ProgramDAO;
import de.beuth.sp.belegsystem.db.dao.TermDAO;
import de.beuth.sp.belegsystem.db.dao.TimeSlotDAO;
import de.beuth.sp.belegsystem.db.manager.TimeSlotManager;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Lesson;
import de.beuth.sp.belegsystem.lg.Program;
import de.beuth.sp.belegsystem.lg.Term;
import de.beuth.sp.belegsystem.lg.TimeSlot;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Tapestry-Page-Klasse zum Editieren eines {@link Course}. Um möglichst
 * benutzerfreundlich zu sein wird auf dieser Seite intensiv AJAX eingesetzt -
 * daher kommen dann auch viele der Methoden (wie
 * {@link #onValueChangedFromTimeslotLesson(TimeSlotLesson)} usw.) <br/>
 * Dabei handelt es sich um eine komplizierten Formularaufbau bei dem z.B. per
 * Ajax dynamisch weitere Elemente (Unterrichtseinheiten) hinzugefügt werden
 * können.
 * 
 * 
 */
@Restricted(allowedFor = Admin.class, onlyIfAssociatedToProgram = true)
@Import(library = "../../components/js/FilterableCalendar.js")
public class EditCourse extends BasePageImpl {
	
	@PageActivationContext
	@Property
	private Course course;
	
	@Inject
	private CourseDAO courseDAO;
	
	@Inject 
	private ProgramDAO programDAO;
	
	@Inject
	private InstructorDAO instructorDAO;
	
	@Inject
	private TermDAO termDAO;	
	
	@Inject
	private LessonDAO lessonDAO;
	
	/**
	 * Loop-Variable
	 */
	@SuppressWarnings("unused")
	@Property
	private Instructor instructor;
		
	@InjectComponent
	private Zone instructorsZone;
	
	@InjectComponent
	private Zone instructorsSearchZone;	
	
	@Inject
	private Request request;	
	
	@Property
	private List<Instructor> possibleInstructorsToAdd;
	
	@Persist
	@Property
	private Set<Instructor> instructors;
	
	@Property
	private String instructorName;
	
	@Property
	private Lesson lesson;
	
	@Property
	private int lessonIndex;	
	
	@Property
	@SuppressWarnings("unused")
	private LessonEncoder lessonEncoder;

	@Persist
	@Property
	private List<Lesson> lessons;
		
	@Inject
	private TimeSlotDAO timeSlotDAO;
	
	@Inject
	private TimeSlotManager timeSlotManager;	

	@InjectComponent
	private Zone lessonDateZone;
	
	@Property
	private List<TimeSlot> timeslotModel;

	@SuppressWarnings("unused")
	@Property
	private TimeSlotLessonEncoder timeSlotLessonEncoder = new TimeSlotLessonEncoder();
	
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;	
	
	@Inject
	private Messages messages;

	private boolean create = false; // für submit-label
		
	/**
	 * Aktivierung der Seite
	 */
	void onActivate() {
		lessonEncoder = new LessonEncoder();
		timeslotModel = timeSlotManager.getAllSortedTimeSlots();
		if (course==null) {
			create = true;
			course = new Course();
			course.setTerm(termDAO.getTermAtDate(Calendar.getInstance()));
		}
	}

	/**
	 * Einmalige Initialisierung der Seite bei Page-Render-Request
	 */
	@SetupRender
	void init() {
		instructors = new HashSet<Instructor>();
		lessons = new ArrayList<Lesson>();
		if (course!=null) { 
			instructors.addAll(course.getInstructors());
			lessons.addAll(course.getLessons());
			Collections.sort(lessons, new Comparator<Lesson>() {
				@Override
				public int compare(Lesson o1, Lesson o2) {
					int result = o1.getTimeSlot().getDayOfWeek().compareTo(o2.getTimeSlot().getDayOfWeek());
					if (result==0) {
						result = o1.getTimeSlot().getHourOfDay().compareTo(o2.getTimeSlot().getHourOfDay());
					}
					return result;
				}
				
			});
		}
	}
	
	/**
	 * Success-Methode bei erfolgreichem Abschicken und Validieren der
	 * Formulardaten. Course wird mit allen Daten (Lessons) synchronisiert und
	 * gespeichert.
	 * 
	 * @return
	 */
	@CommitAfter
	@DiscardAfter
	Object onSuccessFromCourseForm() {		
		//Instructors abgleichen
		for (final Instructor instructor : instructors) {
			//Re-Initialisierung (find) via db (da die Menge an courses beim instructor lazy initialisiert wird)	
			course.addInstructor(instructorDAO.find(instructor.getId()));
		}	
		final Set<Instructor> oldInstructors = new HashSet<Instructor>(course.getInstructors());
		for (final Instructor instructor : oldInstructors) {			
			if (!instructors.contains(instructor)) {
				course.removeInstructor(instructor);
			}			
		}		
		
		//Lessons abgleichen
		for (final Lesson lesson : lessons) {
			course.addLesson(lesson);
		}
		final Set<Lesson> oldLessons = new HashSet<Lesson>(course.getLessons());
		for (final Lesson lesson : oldLessons) {
			if (!lessons.contains(lesson)) {				
				course.removeLesson(lesson);
			}			
		}	
		
		//Kurs noch speichern!
		courseDAO.saveOrUpdate(course);
		
		return ListCourse.class;
	}
	
	public List<Program> getAllPrograms() {
		return programDAO.getAll();
	}
	
	public List<Term> getAllTerms() {
		return termDAO.getAll();
	}

	/**
	 * Ajax-Methode für Schnellsuche des {@link Instructor}s.
	 * @return
	 */
	Object onInstructorNameChanged() {
		instructorName = request.getParameter("param");
		possibleInstructorsToAdd = instructorDAO.searchByName(instructorName);
		possibleInstructorsToAdd.removeAll(instructors);
		return instructorsSearchZone.getBody();
	}
	
	/**
	 * Ajax-Methode für Auswahl eines {@link Instructor}s aus der Scuhliste.
	 * @param instructor
	 */
	void onActionFromAddInstructor(Instructor instructor) {
		instructors.add(instructor);
		ajaxResponseRenderer.addRender(instructorsZone).addRender(instructorsSearchZone);
	}
	
	/**
	 * Ajax-Methode für das Entfernen eines zugewiesenen {@link Instructor}s.
	 * @param instructor
	 * @return
	 */
	Object onActionFromRemoveInstructor(Instructor instructor) {
		instructors.remove(instructor);
		return instructorsZone.getBody();
	}
	
	public String updateLessonIndex() {
		lessonIndex++;
		return null;
	}
	
	/**
	 * eErstellt und gibt eine neue "Zeile" für die {@link Lesson}s des {@link Course} zurück, d.h. eine neue
	 * Unterrichtseinheit wir ddynamisch per Ajax dem Formular hinzugefügt.
	 * 
	 */
	Lesson onAddRow() {		
		lesson = new Lesson();
		lesson.setCourse(course);
		if (!lessons.isEmpty()) {
			Lesson lastLesson = lessons.get(lessons.size()-1);
			if (lastLesson.getVersion() != null) {
				lesson.setRoom(lastLesson.getRoom());
				lesson.setTimeSlot(lastLesson.getTimeSlot());
				lesson.setStartAndEndDate(lastLesson.getStartDate(), lastLesson.getEndDate());
			}
		}
		lessons.add(lesson);
		lessonIndex = lessons.size()-1;
		return lesson;
	}
	
	void onRemoveRow(Lesson lesson) {
		lessons.remove(lesson);		
	}
		
	private class LessonEncoder implements ValueEncoder<Lesson> {

		@Override
		public String toClient(Lesson value) {
			return value.getId().toString();
		}

		@Override
		public Lesson toValue(String clientValue) {
			return getLessonFromPersistedLessons(clientValue);			
		}		
	}
	
	public TimeSlot getTimeSlot() {
		return lesson.getTimeSlot();
	}
	
	public void setTimeSlot(TimeSlot timeSlot) {
		lesson.setTimeSlot(timeSlot);
	}
	
	public TimeSlotLesson getTimeSlotLesson() {		
		return new TimeSlotLesson(lesson.getTimeSlot(), lesson, lessonIndex);
	}
	
	public void setTimeSlotLesson(final TimeSlotLesson timeSlotLesson) {			
		lesson.setTimeSlot(timeSlotLesson.timeSlot);
	}
	
	void onValueChangedFromTimeslotLesson(TimeSlotLesson timeSlotLesson) {
		lesson = timeSlotLesson.lesson;
		lessonIndex = timeSlotLesson.index;
		ajaxResponseRenderer.addRender("lessonDateZone" + lessonIndex, lessonDateZone.getBody());
	}	
	
	public SelectModel getTimeSlotLessonModel() {
		return new TimeSlotLessonSelectModel(lesson);
	}
	
	/**
	 * Kleine Klasse zum Binding der TimeSlot-Checkbox und der Lesson, auf dass
	 * wir sie bei einem dynamischen Update wieder auslesen können - siehe auch
	 * {@link TimeSlotLessonEncoder}.
	 * 
		 * 
	 */
	public static class TimeSlotLesson {
		public TimeSlot timeSlot;
		public Lesson lesson;
		public int index;
		public TimeSlotLesson(TimeSlot timeSlot, Lesson lesson, int index) {
			this.timeSlot = timeSlot;
			this.lesson = lesson;
			this.index = index;
		}
		public String toString() {
			return timeSlot.toString();
		}
	}
	
	/**
	 * Encoder für die TimeSlot-Checkboxen. Spezieller Encoder wird benötigt da
	 * wir an den {@link TimeSlot} auch die {@link Lesson} binden um sie nicht zu verlieren bei
	 * einem dynamischen Ajax-Update ausgelöst durch das Ändern des {@link TimeSlot}s.
	 * 
		 * 
	 */
	private class TimeSlotLessonEncoder implements ValueEncoder<TimeSlotLesson> {
		@Override
		public String toClient(TimeSlotLesson value) {
			return (value.timeSlot != null ? value.timeSlot.getId() : "null") + "_" + value.lesson.getId() + "_" + value.index;
		}

		@Override
		public TimeSlotLesson toValue(String clientValue) {
			String timeSlotId = clientValue.substring(0, clientValue.indexOf("_"));
			TimeSlot timeSlot = timeSlotDAO.find(UUID.fromString(timeSlotId));
			Lesson lesson = getLessonFromPersistedLessons(clientValue.substring(timeSlotId.length()+1, clientValue.lastIndexOf("_")));
			lesson.setTimeSlot(timeSlot);
			int index = Integer.parseInt(clientValue.substring(clientValue.lastIndexOf("_") + 1));
			return new TimeSlotLesson(timeSlot, lesson, index);
		}
	}
	
	private Lesson getLessonFromPersistedLessons(String lessonId) {
		for (Lesson lesson : lessons) {
			if (lessonId.equals(lesson.getId().toString())) {
				if (lesson.getVersion()==null) {
					return lesson;
				} else {
					return lessonDAO.find(lesson.getId());
				}
			}
		}
		return null;
	}
	
	/**
	 * Select-Model für unsere TimeSlots
	 * 
		 *
	 */
	public class TimeSlotLessonSelectModel extends AbstractSelectModel {

		private Lesson lesson;

		public TimeSlotLessonSelectModel(Lesson lesson) {
			this.lesson = lesson;
		}
		
		@Override
		public List<OptionGroupModel> getOptionGroups() {
			return null;
		}

		@Override
		public List<OptionModel> getOptions() {
			List<OptionModel> options = new ArrayList<OptionModel>();
			for (TimeSlot timeSlot : timeslotModel) {
				String timeSlotDisplay = messages.get(timeSlot.getDayOfWeek().toString()) + " " + messages.format("timeslot_timeformat", timeSlot.getHourOfDay(), timeSlot.getMinuteOfHour(), timeSlot.getEndingHourOfDay(), timeSlot.getEndingMinuteOfHour());
				options.add(new OptionModelImpl(timeSlotDisplay, new TimeSlotLesson(timeSlot, lesson, lessonIndex)));
			}
			return options;
		}
		
	}
	
	public String getSubmitLabelOfForm() {
		if (create == true) {
			return messages.get("submit-create-label");
		} else {
			return messages.get("submit-edit-label");
		}		
	}
				
}
