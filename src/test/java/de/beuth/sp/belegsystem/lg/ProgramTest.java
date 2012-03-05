package de.beuth.sp.belegsystem.lg;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.beuth.sp.belegsystem.TestUtil;
import de.beuth.sp.belegsystem.db.dao.ProgramDAO;
import de.beuth.sp.belegsystem.exceptions.ExcUtil.ProgramLevelBelowCourseLevelExc;

/**
 * 
 *
 */
public class ProgramTest extends TestCase {
	
	private Program program;
	private ProgramDAO programDAO;
		
	@Before
	public void setUp() {
		programDAO = TestUtil.getService(ProgramDAO.class);
		program = new Program();
		program.setName("MI-BA");
		program.setDescription("Medieninformatik Bachelor");
		program.setLevels(6);
	}	
	
	@Test
	public void testCourseLinking() {
		//einen kurs erstellen und hinzufügen und überprüfen ob er drin ist
		final Course course = new Course();
		course.setCourseIdentifier("Testcourse1 in MI-BA");
		course.setLevel(5);
		program.addCourse(course);
		programDAO.saveOrUpdate(program);
		program = programDAO.find(program.getId());
		assertTrue(program.getCourses().contains(course));

		//zweiten Kurs erstellen und hinzufügen und überprüfen ob dann wirklich beide drin sind
		final Course course2 = new Course();
		course2.setCourseIdentifier("Testcourse2 in MI-BA");
		course2.setLevel(5);
		program.addCourse(course2);
		programDAO.saveOrUpdate(program);
		program = programDAO.find(program.getId());
		assertTrue(program.getCourses().contains(course));
		assertTrue(program.getCourses().contains(course2));
		assertTrue(program.getCourses().size()==2);
		
		//zweiten kurs wieder entfernen und überprüfen ob er wirklich nicht mehr drin ist
		program.removeCourse(course2);
		program = programDAO.find(program.getId());		
		assertFalse(program.getCourses().contains(course2));
		//und Anzahl von Kursen nur noch eins
		assertTrue(program.getCourses().size()==1);
		
		//ersten kurs auch noch entfernen
		program.removeCourse(course);
		program = programDAO.find(program.getId());		
		//kurs nicht mehr drin?
		assertFalse(program.getCourses().contains(course));
		//und Courses-Set jetzt wieder leer?
		assertTrue(program.getCourses().isEmpty());
		
	}
	
	@Test
	public void testProgramLevelBelowCourseLevelExc() { 
		try {
			Course course = new Course();
			course.setCourseIdentifier("Testcourse1 in MI-BA");
			course.setLevel(5);
			program.addCourse(course);			
		} catch (ProgramLevelBelowCourseLevelExc e) {
			fail("ProgramLevelBelowCourseLevelExc not expected: Kurs hat niedrigeres Fachsemester (level) als Programm an Fachsemestern (levels)");
		}
		
		try {
			Course course = new Course();
			course.setCourseIdentifier("Testcourse1 in MI-BA");
			course.setLevel(7);
			program.addCourse(course);
			fail("ProgramLevelBelowCourseLevelExc expected: Kurs hat höheres Fachsemester (level) als Programm an Fachsemestern (levels)");
		} catch ( ProgramLevelBelowCourseLevelExc e) {}
		
		try {
			program.setLevels(4);
			fail("ProgramLevelBelowCourseLevelExc expected: Fachsemesterzahl des Programm auf Wert unter dem eines Kurses reduziert.");
		} catch ( ProgramLevelBelowCourseLevelExc e) {}		
	}	

	@After
	public void tearDown() {
		programDAO.rollbackTransaction();
	}
	

}
