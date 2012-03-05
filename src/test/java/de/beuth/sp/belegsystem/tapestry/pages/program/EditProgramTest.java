package de.beuth.sp.belegsystem.tapestry.pages.program;

import junit.framework.TestCase;

import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.test.PageTester;
import org.junit.After;
import org.junit.Before;

import de.beuth.sp.belegsystem.TestUtil;
import de.beuth.sp.belegsystem.db.dao.ProgramDAO;
import de.beuth.sp.belegsystem.exceptions.ExcUtil.ProgramLevelBelowCourseLevelExc;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Program;
import de.beuth.sp.belegsystem.lg.User;

public class EditProgramTest extends TestCase {
	private ProgramDAO programDAO;
	
	private Program program;
	
	private PageTester pageTester;
	
	@Override
	@Before
	public void setUp() {
		programDAO = TestUtil.getService(ProgramDAO.class);
		pageTester = TestUtil.getPageTester();
		User mockUser = new User();
		mockUser.addRole(new Admin());
		pageTester.getService(ApplicationStateManager.class).set(User.class, mockUser);		
	}
	
	public void testEditProgram() throws ProgramLevelBelowCourseLevelExc {
		program = new Program();
		program.setName("Medieninformatik MA");
		program.setDescription("Medieninformatik Master");
		program.setLevels(4);
		
		programDAO.saveOrUpdate(program);
		final Document doc = pageTester.renderPage("program/edit/" + program.getId());
		
		System.out.println(doc.getElementById("name"));
		System.out.println(doc.getElementById("description"));
		System.out.println(doc.getElementById("level"));
		
		// noch etwas???
	}
	
	@Override
	@After
	public void tearDown() {
		programDAO.rollbackTransaction();
	}

}
