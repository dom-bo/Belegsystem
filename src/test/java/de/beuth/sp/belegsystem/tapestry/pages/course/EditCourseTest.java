package de.beuth.sp.belegsystem.tapestry.pages.course;

import junit.framework.TestCase;

import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.test.PageTester;
import org.junit.After;
import org.junit.Before;

import de.beuth.sp.belegsystem.TestUtil;
import de.beuth.sp.belegsystem.db.dao.CourseDAO;
import de.beuth.sp.belegsystem.db.dao.ProgramDAO;
import de.beuth.sp.belegsystem.db.dao.TermDAO;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.lg.Program;
import de.beuth.sp.belegsystem.lg.Term;
import de.beuth.sp.belegsystem.lg.User;
import de.beuth.sp.belegsystem.lg.User.RoleDuplicateExc;

public class EditCourseTest extends TestCase {
	private PageTester pageTester;
	private Course course;
	private CourseDAO courseDAO;
	private TermDAO termDAO;
	private ProgramDAO programDAO;

	@Before
	public void setUp() throws RoleDuplicateExc {
		courseDAO = TestUtil.getRegistry().getService(CourseDAO.class);
		termDAO = TestUtil.getRegistry().getService(TermDAO.class);
		programDAO = TestUtil.getRegistry().getService(ProgramDAO.class);
		pageTester = TestUtil.getPageTester();
		course = new Course();
		User mockUser = new User();
		mockUser.addRole(new Admin());
		pageTester.getService(ApplicationStateManager.class).set(User.class, mockUser);		
	}
	
	public void testEditCourse() {

		Program program = new Program();
		program.setDescription("Medieninformatik BA");
		program.setName("MD BA");
		program.setLevels(6);
		programDAO.saveOrUpdate(program);
		
		Term term = new Term();
		termDAO.saveOrUpdate(term);

		course.setCourseIdentifier("SWP");
		course.setDescription("Software Projekt");
		course.setLevel(5);
		course.setProgram(program);
		course.setStudyGroup(2);
		course.setTerm(term);
		courseDAO.saveOrUpdate(course);
		
		
		
		Document doc = pageTester.renderPage("course/edit/" + course.getId());
		
		//do some tests!
		System.out.println(doc.getDocument());
	}
	
	

	@After
	public void tearDown() {
		courseDAO.rollbackTransaction();
	}
}
