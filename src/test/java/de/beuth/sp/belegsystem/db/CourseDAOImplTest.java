package de.beuth.sp.belegsystem.db;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.beuth.sp.belegsystem.TestUtil;
import de.beuth.sp.belegsystem.db.dao.CourseDAO;
import de.beuth.sp.belegsystem.db.dao.InstructorDAO;
import de.beuth.sp.belegsystem.db.dao.ProgramDAO;
import de.beuth.sp.belegsystem.db.dao.TermDAO;
import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Program;
import de.beuth.sp.belegsystem.lg.Term;
import de.beuth.sp.belegsystem.lg.User;
import de.beuth.sp.belegsystem.lg.User.RoleDuplicateExc;

/**
 *
 */
public class CourseDAOImplTest extends TestCase {
	private Course course;
	private CourseDAO courseDAO;
	private ProgramDAO programDAO;
	private InstructorDAO instructorDAO;
	private TermDAO termDAO;
	
	@Before
	public void setUp() {
		courseDAO = TestUtil.getService(CourseDAO.class);
		instructorDAO = TestUtil.getService(InstructorDAO.class);
		programDAO = TestUtil.getService(ProgramDAO.class);
		termDAO = TestUtil.getService(TermDAO.class);
		
		course = new Course();
	}

	/**
	 * Testet das einfach speichern eines course Objekts und die find Methode
	 * des DAOs
	 */
	@Test
	public void testCreateAndFind() {
		courseDAO.saveOrUpdate(course);
		assertEquals(courseDAO.find(course.getId()), course);
	}

	/**
	 * 
	 */
	@Test
	public void testList() {
		courseDAO.saveOrUpdate(course);
		assertFalse(courseDAO.getAll().isEmpty());
	}

	/**
	 * Testet das Löschen eines course Objekts
	 */
	@Test
	public void testDelete() {
		courseDAO.saveOrUpdate(course);
		courseDAO.delete(course);
		assertNull(courseDAO.find(course.getId()));
	}

	/**
		 */
	@Test
	public void testGetCourse(){
		final int LEVEL = 3;
		final int STUDYGROUP = 3;
		final String COURSEIDENTIFIER = "PR1";
		final String TITLE = "Programmieren 1";
		final String DESCRIPTION = "Grundlagen der Programmierung";
		
		Program program = new Program();
		program.setName("MI-BA");
		program.setDescription("Medieninformatik Bachelor");
		program.setLevels(6);
		programDAO.saveOrUpdate(program);
		
		Calendar startDate = new GregorianCalendar();
		Calendar endDate = new GregorianCalendar();
		endDate.add(Calendar.MONTH, 6);
		Term term = new Term();
		term.setStartDate(startDate);
		term.setEndDate(endDate);
		termDAO.saveOrUpdate(term);
		
		Instructor instructor = new Instructor();
		final User user = new User();
		user.setFirstname("test");
		user.setLastname("test");
		user.setEmail("test@test.com");
		user.setPassword("test");
		user.setPhone("123456");
		user.setUsername(UUID.randomUUID().toString());
		try {
			instructor.setUser(user);
		} catch (final RoleDuplicateExc e) {}
		instructorDAO.saveOrUpdate(instructor);
		
		course.setProgram(program);
		course.setTerm(term);
		course.addInstructor(instructor);
		course.setLevel(LEVEL);
		course.setStudyGroup(STUDYGROUP);
		course.setCourseIdentifier(COURSEIDENTIFIER);
		course.setTitle(TITLE);
		course.setDescription(DESCRIPTION);
		
		courseDAO.saveOrUpdate(course);
		
		assertEquals(courseDAO.getCourses(program, instructor, LEVEL, STUDYGROUP, term, TITLE,null).get(0),
				course);
		
	}
	
	@After
	public void tearDown() {
		courseDAO.rollbackTransaction();
		instructorDAO.rollbackTransaction();
		termDAO.rollbackTransaction();
		programDAO.rollbackTransaction();
	}
	
	
}
