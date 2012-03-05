package de.beuth.sp.belegsystem.lg;

import java.util.UUID;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.beuth.sp.belegsystem.TestUtil;
import de.beuth.sp.belegsystem.db.dao.CourseDAO;
import de.beuth.sp.belegsystem.exceptions.ExcUtil.ProgramLevelBelowCourseLevelExc;
import de.beuth.sp.belegsystem.lg.Course.LessonTimeConflictExc;
import de.beuth.sp.belegsystem.lg.Course.TooMuchInstructorsForCourseExc;
import de.beuth.sp.belegsystem.lg.TimeSlot.DayOfWeek;

public class CourseTest extends TestCase {

	private Course course;
	private CourseDAO courseDAO;

	@Before
	public void setUp() {
		courseDAO = TestUtil.getService(CourseDAO.class);
		course = new Course();
		course.setCourseIdentifier("Testcourse1 in MI-BA");
		course.setLevel(5);
	}

	@After
	public void tearDown() {
		courseDAO.rollbackTransaction();
	}

	@Test
	public void testLevelInProgram() throws ProgramLevelBelowCourseLevelExc {

		// Studiengang mit ausreichend Semestern wird erstellt
		final Program program = new Program();
		program.addCourse(course);

		course.setLevel(6);

		final Program program2 = new Program();
		program2.setLevels(5);
		try {
			program2.addCourse(course);
			fail("ProgramLevelBelowCourseLevelExc expected");
		} catch (ProgramLevelBelowCourseLevelExc expected) {
		}

		// Fachsemester wird zu hoch gesetzt
		try {
			course.setLevel(7);
			fail("ProgramLevelBelowCourseLevelExc expected");
		} catch (ProgramLevelBelowCourseLevelExc excpected) {
		}
	}

	@Test
	public void testLinkingToProgram() {
		// einen kurs erstellen und hinzufügen und überprüfen ob er drin ist
		Program program = new Program();
		program.setName("MI-BA");
		program.addCourse(course);
		courseDAO.saveOrUpdate(course);
		UUID id = course.getId();
		course = null;
		course = courseDAO.find(id);
		assertTrue(program.equals(course.getProgram()));

		// program auf null setzen... Kurs sollte aus Program entfernt werden
		course.setProgram(null);
		id = course.getId();
		course = null;
		course = courseDAO.find(id);
		assertFalse(program.equals(course.getProgram()));
		assertNull(course.getProgram());
	}

	@Test
	public void testLessonTimeConflictExc() {
		Lesson lesson1 = new Lesson();
		Lesson lesson2 = new Lesson();
		TimeSlot timeslot1 = new TimeSlot();
		timeslot1.setDayOfWeek(DayOfWeek.MONDAY);
		timeslot1.setHourOfDay(10);
		timeslot1.setMinuteOfHour(00);
		timeslot1.setDurationInMinutes(90);
		lesson1.setTimeSlot(timeslot1);
		lesson2.setTimeSlot(timeslot1);
		course.addLesson(lesson1);
		try {// testet auf identische Timeslots
			course.addLesson(lesson2);
			fail("LessonTimeConflictExc");
		} catch (LessonTimeConflictExc expected) {}
		TimeSlot timeslot2 = new TimeSlot();
		timeslot2.setDayOfWeek(DayOfWeek.MONDAY);
		timeslot2.setHourOfDay(10);
		timeslot2.setMinuteOfHour(15);
		timeslot2.setDurationInMinutes(90);
		Lesson lesson3 = new Lesson();
		lesson3.setTimeSlot(timeslot2);
		try {// testet auf Ueberschneidungen
			course.addLesson(lesson3);
			fail("LessonTimeConflictExc");
		} catch (LessonTimeConflictExc expected) {}

		assertTrue(course.getLessons().contains(lesson1));
		assertFalse(course.getLessons().contains(lesson2));
		assertFalse(course.getLessons().contains(lesson3));
	}

	@Test
	public void testAddInstructor() {
		//TODO datenbank dabei testen
		//course = new Course();
		try { // null als Argument => Fehler
			course.addInstructor(null);
			fail("IllegalArgumentException excepted");
		} catch (IllegalArgumentException excepted) {
		}
		User[] users = new User[4];
		Instructor[] instructors = new Instructor[4];
		for (int i = 0; i < users.length; i++) {
			users[i] = new User();
			users[i].setUsername("testuser" + i);
			instructors[i] = new Instructor();
			instructors[i].setUser(users[i]);
		}
		course.addInstructor(instructors[0]);
		course.addInstructor(instructors[1]);
		course.addInstructor(instructors[2]);
		try { // Vierter Instructor => Fehler
			course.addInstructor(instructors[3]);
			fail("TooMuchInstructorsForCourseExc expected");
		} catch (TooMuchInstructorsForCourseExc expected) {
		}
		// Testet, ob Instructors 1-3 aufgenommen wurden und Instructor 4 eben
		// nicht, ebenfalls wird getestet, ob die Instructors selber den
		// testcourse enthalten
		assertTrue(course.getInstructors().contains(instructors[0]));
		assertTrue(instructors[0].getCourses().contains(course));
		assertTrue(course.getInstructors().contains(instructors[1]));
		assertTrue(instructors[1].getCourses().contains(course));
		assertTrue(course.getInstructors().contains(instructors[2]));
		assertTrue(instructors[2].getCourses().contains(course));
		assertFalse(course.getInstructors().contains(instructors[3]));
		assertFalse(instructors[3].getCourses().contains(course));
	}

	@Test
	public void testRemoveInstructor() {
		//course = new Course();
		try {
			course.removeInstructor(null);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}
		Instructor instructor = new Instructor();
		User user = new User();
		user.setUsername("testuser");
		instructor.setUser(user);
		course.addInstructor(instructor);
		assertTrue(course.getInstructors().contains(instructor));
		assertTrue(instructor.getCourses().contains(course));

		course.removeInstructor(instructor);

		assertFalse(course.getInstructors().contains(instructor));
		assertFalse(instructor.getCourses().contains(course));
	}

	@Test
	public void testLinkingToInstructor() {
		Instructor instructor = new Instructor();
		User user = new User();
		user.setUsername("testuser");
		instructor.setUser(user);
	}

	@Test
	public void testAddLesson(){
		TimeSlot timeslotTest = new TimeSlot();
		Lesson lessonTest1 = new Lesson();
		Lesson lessonTest2 = new Lesson();
		lessonTest1.setTimeSlot(timeslotTest);
		lessonTest2.setTimeSlot(timeslotTest);
		course.addLesson(lessonTest1);
		try{
			course.addLesson(lessonTest2);
			fail("Zeitliche überschneidung");
		}catch(LessonTimeConflictExc e){}
		assertTrue(course.getLessons().contains(lessonTest1));
		assertFalse(course.getLessons().contains(lessonTest2));
		course.removeLesson(lessonTest1);
	}
	
	@Test
	public void testRemoveLesson(){
		Lesson lessontest = new Lesson();
		course.addLesson(lessontest);
		assertTrue(course.getLessons().contains(lessontest));
		course.removeLesson(lessontest);
		assertFalse(course.getLessons().contains(lessontest));
	}
}
