package de.beuth.sp.belegsystem.lg;

import java.util.UUID;

import junit.framework.TestCase;

import org.apache.commons.lang.NullArgumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.beuth.sp.belegsystem.lg.Instructor.InstructorProgramCannotBeRemovedExc;
import de.beuth.sp.belegsystem.lg.User.RoleDuplicateExc;

/**
 * 
 * 
 */
public class InstructorTest extends TestCase {

	/**
	 * 
	 */	
	private Instructor instructor;

	/**
	 * 
	 */	
	@Before
	public void setUp() {
		instructor = new Instructor();
		final User user = new User();
		user.setFirstname("first");
		user.setLastname("last");
		user.setEmail("test@test.com");
		user.setPassword("test");
		user.setPhone("123456");
		user.setUsername("user_" + UUID.randomUUID().toString());	
	}

	/**
	 * 
	 */	
	@After
	public void tearDown() {

	}

	/**
	 * 
	 */	
	@Test
	public void testDuplicateRoleForUserException() {
		final Instructor instructor2 = new Instructor();

		final User user = new User();
		user.setUsername("TestInstructorUser");

		try {
			instructor.setUser(user);
		} catch (final RoleDuplicateExc e) {
			fail("RoleDuplicateExc not expected: User hat nur eine Rolle!");
		}

		try {
			instructor2.setUser(user);
			fail("RoleDuplicateExc expected: User hat zwei mal die gleiche Rolle!");
		} catch (final RoleDuplicateExc e) {
		}
	}

	/**
	 * 
	 */	
	@Test
	public void testAddCourse() {	
		final Course course = new Course();
		
		assertFalse(course.getInstructors().contains(this.instructor));

		try {
			this.instructor.addCourse(course);
		} catch (NullArgumentException e) {
			fail("NullArgumentException not expected");
		}

		assertTrue(course.getInstructors().contains(this.instructor));

		try {
			this.instructor.addCourse(null);
			fail("NullArgumentException expected");
		} catch (NullArgumentException e) {
		}
	}

	/**
	 * 
	 */	
	@Test
	public void testRemoveCourse() {
		final Course course = new Course();
		
		course.addInstructor(instructor);

		try {
			this.instructor.removeCourse(course);
		} catch (NullArgumentException e) {
			fail("NullArgumentException not expected");
		}

		assertFalse(course.getInstructors().contains(this.instructor));

		try {
			this.instructor.removeCourse(null);
			fail("NullArgumentException expected");
		} catch (NullArgumentException e) {
		}
	}
	
	@Test
	public void testAddProgram() {
		final Program program = new Program();
		instructor.addProgram(program);
		
		assertTrue(instructor.getPrograms().contains(program));
	}
	
	@Test
	public void removeProgram() {
		final Program program = new Program();
		instructor.addProgram(program);
		
		instructor.removeProgram(program);
		assertFalse(instructor.getPrograms().contains(program));
	}
	
	@Test
	public void removeProgramButStillHavingCourseOfProgram() {
		final Course course = new Course();
		final Program program = new Program();
		program.addCourse(course);
		instructor.addCourse(course);
		
		try {
			instructor.removeProgram(program);
		} catch ( InstructorProgramCannotBeRemovedExc e) {
			fail("InstructorProgramCannotBeRemovedExc expected");
		}
	}

}
