package de.beuth.sp.belegsystem.lg;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.beuth.sp.belegsystem.exceptions.ExcUtil.StartDateAfterEndDateExc;

/**
 * 
 * 
 *
 */
public class TermTest extends TestCase {
	private Term term;
	private Calendar startDate;
	private Calendar endDate;
	
	@Before
	public void setUp() {
		term = new Term();

		startDate = new GregorianCalendar();
		startDate.set(Calendar.DAY_OF_MONTH, 1);
		startDate.set(Calendar.MONTH, Calendar.JANUARY);
		startDate.set(Calendar.YEAR, 2011);

		endDate = new GregorianCalendar();
		endDate.set(Calendar.DAY_OF_MONTH, 1);
		endDate.set(Calendar.MONTH, Calendar.JULY);
		endDate.set(Calendar.YEAR, 2011);
	}

	/**
	 * Test prüft ob die Methoden setStartDate und setEndDate eines Terms 
	 * gültige Werte annehmen. 
	 *  
	 * 
	 * @throws StartDateAfterEndDateExc
	 */
	@Test
	public void testStartDateBeforEndDate() throws StartDateAfterEndDateExc {
		// StartDatum liegt vor dem EndDatum, hier sollte alles ok sein
		term.setStartDate(startDate);
		term.setEndDate(endDate);
	}
	
	/**
	 * Test prüft ob die Exception StartDateAfterEndDateExc geworfen wird,
	 * wenn sich das Start Datum eines Semester zeitlich hinter dem End-Datum
	 * eines Semesters liegt. 
	 * 
	 * Dabei werden die Methoden setStartDate und setEndDate eines Terms
	 * geprüft.
	 * 
	 * @throws StartDateAfterEndDateExc
	 */
	@Test
	public void testStartDateAfterEndDate() throws StartDateAfterEndDateExc {
		term.setStartDate(startDate);
		term.setEndDate(endDate);			
		
		Calendar startAfterEndDate = (Calendar) endDate.clone();
		startAfterEndDate.add(Calendar.DAY_OF_MONTH, 1);
		
		Calendar endDateBeforeStartDate = (Calendar) startDate.clone();
		endDateBeforeStartDate.add(Calendar.DAY_OF_MONTH, -1);
		
		// StartDatum liegt nach dem EndDatum, hier
		// sollte eine Exception geworfen werden		
		try {
			term.setStartDate(startAfterEndDate);
			fail("StartDateAfterEndDateException sollte geworfen werden");
		} catch (final StartDateAfterEndDateExc e) {
		}

		// EndDatum liegt vor dem StartDatum, hier
		// sollte eine Exception geworfen werden
		try {
			term.setEndDate(endDateBeforeStartDate);
			fail("StartDateAfterEndDateException sollte geworfen werden");
		} catch (final StartDateAfterEndDateExc e) {
		}
	}
}
