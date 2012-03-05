package de.beuth.sp.belegsystem.tapestry.pages.term;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.test.PageTester;
import org.junit.After;
import org.junit.Before;

import de.beuth.sp.belegsystem.TestUtil;
import de.beuth.sp.belegsystem.db.dao.TermDAO;
import de.beuth.sp.belegsystem.exceptions.ExcUtil.StartDateAfterEndDateExc;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Term;
import de.beuth.sp.belegsystem.lg.User;

/**
 *
 */
public class EditTermTest extends TestCase {
	private TermDAO termDAO;
	
	private Term term;
	
	private PageTester pageTester;
	
	@Override
	@Before
	public void setUp() {
		pageTester = TestUtil.getPageTester();
		termDAO = TestUtil.getService(TermDAO.class);
		User mockUser = new User();
		mockUser.addRole(new Admin());
		pageTester.getService(ApplicationStateManager.class).set(User.class, mockUser);		
	}
	
	public void testEditTerm() throws StartDateAfterEndDateExc {
		final Calendar startDate = new GregorianCalendar();
		startDate.set(Calendar.DAY_OF_MONTH, 1);
		startDate.set(Calendar.MONTH, Calendar.FEBRUARY);
		startDate.set(Calendar.YEAR, 2011);

		final Calendar endDate = new GregorianCalendar();
		endDate.set(Calendar.DAY_OF_MONTH, 14);
		endDate.set(Calendar.MONTH, Calendar.JULY);
		endDate.set(Calendar.YEAR, 2011);
		
		term = new Term();
		term.setStartDate(startDate);
		term.setEndDate(endDate);
		
		termDAO.saveOrUpdate(term);	

		final Document doc = pageTester.renderPage("term/edit/" + term.getId());
		
		assertEquals(new SimpleDateFormat("M/d/yyyy").format(startDate.getTime()),
				doc.getElementById("startDate").getAttribute("value"));
	
	}
	@Override
	@After
	public void tearDown() {
		termDAO.rollbackTransaction();
	}
}
