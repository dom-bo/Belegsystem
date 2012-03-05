package de.beuth.sp.belegsystem.tapestry.pages.instructor;

import junit.framework.TestCase;

import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.test.PageTester;
import org.junit.After;
import org.junit.Before;

import de.beuth.sp.belegsystem.TestUtil;
import de.beuth.sp.belegsystem.db.dao.InstructorDAO;
import de.beuth.sp.belegsystem.exceptions.ExcUtil.StartDateAfterEndDateExc;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.User;
import de.beuth.sp.belegsystem.lg.User.RoleDuplicateExc;

/**
 * 
 *
 */
public class EditInstructorTest extends TestCase {
	private InstructorDAO instructorDAO;
	
	private Instructor instructor;
	
	private PageTester pageTester;
	
	@Override
	@Before
	public void setUp() {	
		instructorDAO = TestUtil.getService(InstructorDAO.class);
		pageTester = TestUtil.getPageTester();
		User mockUser = new User();
		Admin adminRole = new Admin();
		adminRole.setSuperAdmin(true);
		mockUser.addRole(adminRole);
		pageTester.getService(ApplicationStateManager.class).set(User.class, mockUser);
	}
	
	public void testEditTerm() throws StartDateAfterEndDateExc, RoleDuplicateExc {
		instructor = new Instructor();
		
		final User user = new User();
		user.setFirstname("test");
		user.setLastname("test");
		user.setEmail("test@test.com");
		user.setPassword("test");
		user.setPhone("123456");
		user.setUsername("test");
		instructor.setUser(user);	
		
		instructorDAO.saveOrUpdate(instructor);		
		final Document doc = pageTester.renderPage("instructor/edit/" + instructor.getId());
		
		System.out.println(doc.getElementById("firstname"));
		System.out.println(doc.getElementById("lastname"));
		System.out.println(doc.getElementById("email"));
		System.out.println(doc.getElementById("phone"));
		System.out.println(doc.getElementById("username"));
		
		assertEquals(instructor.getUser().getUsername(), doc.getElementById("username").getAttribute("value"));
	
	}
	@Override
	@After
	public void tearDown() {
		instructorDAO.rollbackTransaction();
	}
}
