package de.beuth.sp.belegsystem.db;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.beuth.sp.belegsystem.TestUtil;
import de.beuth.sp.belegsystem.db.dao.TermDAO;
import de.beuth.sp.belegsystem.exceptions.ExcUtil.StartDateAfterEndDateExc;
import de.beuth.sp.belegsystem.lg.Term;

/**
 *
 */
public class TermDAOImplTest extends TestCase {
	private Term term;
	private TermDAO termDAO;


	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	@Before
	public void setUp() {
		termDAO = TestUtil.getService(TermDAO.class);
		term = new Term();
	}

	/**
	 * Testet das einfache speichern eines Term Objekts und
	 * die find Methode des DAOs
	 */
	@Test
	public void testFind() {
		termDAO.saveOrUpdate(term);
		assertEquals(termDAO.find(term.getId()), term);
	}

	/**
	 * 
	 */
	@Test
	public void testList() {
		termDAO.saveOrUpdate(term);
		assertFalse(termDAO.getAll().isEmpty());
	}

	/**
	 * Testet das Löschen eines Term Objekts
	 */
	@Test
	public void testDelete() {
		termDAO.saveOrUpdate(term);
		termDAO.delete(term);
		assertNull(termDAO.find(term.getId()));
	}

	/**
	 * Testet die Methode "existsTerm" des DAOs.
	 * Dazu wird ein Term angelegt/gespeichert. Mit verschiedenen
	 * Zeiträumen (davor, währenddessen, danach) wird getest,
	 * ob dieser Term schon existiert. 
	 */
	@Test
	public void testExistsTerm() {
		Calendar startDate = new GregorianCalendar();
		startDate.set(Calendar.DAY_OF_MONTH, 1);
		startDate.set(Calendar.MONTH, Calendar.FEBRUARY);
		startDate.set(Calendar.YEAR, 2011);

		Calendar endDate = new GregorianCalendar();
		endDate.set(Calendar.DAY_OF_MONTH, 14);
		endDate.set(Calendar.MONTH, Calendar.JULY);
		endDate.set(Calendar.YEAR, 2011);

		try {
			term.setStartDate(startDate);
			term.setEndDate(endDate);
			termDAO.saveOrUpdate(term);
		} catch (final StartDateAfterEndDateExc e) {
			fail("StartDatum liegt vor EndDatum - Keine Exception");
		}

		// Wenn es einen Term mit StartDatum gibt, der zeitlich zwischen Start und EndDatum fällt
		startDate.add(Calendar.DAY_OF_MONTH, 1);
		endDate.add(Calendar.DAY_OF_MONTH, -1);
		// Term wird mit Start- und Endzeitpunkt in DB gefunden
		assertTrue(term.equals(termDAO.existsTerm(startDate, endDate)));

		//  Wenn es einen Term mit EndDatum gibt, der zeitlich zwischen Start und EndDatum fällt
		startDate.add(Calendar.DAY_OF_MONTH, -2);
		// Term wird mit Start- und Endzeitpunkt in DB gefunden
		assertTrue(term.equals(termDAO.existsTerm(startDate, endDate)));

		// Wenn es einen Term gibt, der zeitlich vor Start beginnt und nach EndDatum endet
		endDate.add(Calendar.DAY_OF_MONTH, 3);
		// Term wird mit Start- und Endzeitpunkt in DB gefunden
		assertTrue(term.equals(termDAO.existsTerm(startDate, endDate)));
		
	}

	@Override
	@After
	public void tearDown() {
		termDAO.rollbackTransaction();
	}
}
