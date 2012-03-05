package de.beuth.sp.belegsystem.lg;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.apache.commons.lang.NullArgumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.beuth.sp.belegsystem.Settings;
import de.beuth.sp.belegsystem.TestUtil;
import de.beuth.sp.belegsystem.db.dao.ParticipantDAO;
import de.beuth.sp.belegsystem.lg.Participant.CourseCanNotBeBookedExc;

/**
 *
 */
public class ParticipantTest extends TestCase {

	private ParticipantDAO participantDAO;
	private Participant participant;

	@Before
	public void setUp() {
		participantDAO = TestUtil.getService(ParticipantDAO.class);
		participant = new Participant();
	}

	@After
	public void tearDown() {
		participantDAO.rollbackTransaction();
	}

	@Test
	public void testAddCourse() {
		
		// Test ob ein Kurs übergeben wird
		try {
			this.participant.addCourse(null);
			fail("NullArgumentException not expected");
		} catch (NullArgumentException e) {
		}

		Calendar startDate = new GregorianCalendar();
		startDate.add(Calendar.DAY_OF_MONTH, -1*(Settings.getCourseBookingDays()));
		Term term = new Term();
		term.setStartDate(startDate);
		
		// Test ob ein Kurs außerhalb des Buchungszeitraum liegt
		Course course = new Course();
		course.setTerm(term);	
		try {
			participant.addCourse(course);
			participantDAO.saveOrUpdate(participant);
			fail("CourseCanNotBeBookedExc not expected");
		} catch (CourseCanNotBeBookedExc e) {
		}
		
		startDate.add(Calendar.DAY_OF_MONTH, Settings.getCourseBookingDays());
		term.setStartDate(startDate);
		
		// Test ob ein Kurs hinzugefügt werden kann
		course = new Course();
		course.setTerm(term);	
		try {
			participant.addCourse(course);
			participantDAO.saveOrUpdate(participant);
		} catch (CourseCanNotBeBookedExc e) {
			fail("CourseCanNotBeBookedExc expected");
		}
		
		// Test ob der Kurs auch wirklich drin ist
		if(participant.getCourses().size() != 1) 
			fail("getCourse() must return 1");
		if (!participant.getCourses().contains(course)) {
			fail("course musst be in courses");
		}
	}
	
	/**
	 * Prüft ob Kurse mehrmals 
	 * hinzugefügt werden können
	 */
	@Test
	public void testHasCourse(){
		try {
			this.participant.addCourse(null);
			fail("NullArgumentException not expected");
		} catch (NullArgumentException e) {
		}

		Calendar startDate = new GregorianCalendar();
		Term term = new Term();
		term.setStartDate(startDate);
		
		Course course = new Course();
		course.setTerm(term);	
		if(participant.hasCourse(course))
			fail("Method must return false");

		participant.addCourse(course);
		
		if(!participant.hasCourse(course))
			fail("Method must return true");
	}
	
	/**
	 * Test zum stornieren eines Kurses
	 */
	@Test
	public void testCancelCouse(){
		try {
			this.participant.cancelCourse(null);
			fail("NullArgumentException not expected");
		} catch (NullArgumentException e) {
		}
		
		
		Calendar startDate = new GregorianCalendar();
		Term term = new Term();
		term.setStartDate(startDate);
		
		Course course = new Course();
		course.setTerm(term);	
		
		
		participant.addCourse(course);
		
		participant.cancelCourse(course);
		
		if(participant.getCourses().size() > 0) 
			fail("getCourse() must return 0 ");
	}
}
