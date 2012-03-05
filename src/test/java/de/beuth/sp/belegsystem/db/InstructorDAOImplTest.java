package de.beuth.sp.belegsystem.db;

import java.util.UUID;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.beuth.sp.belegsystem.TestUtil;
import de.beuth.sp.belegsystem.db.dao.InstructorDAO;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.User;
import de.beuth.sp.belegsystem.lg.User.RoleDuplicateExc;

/**
 * 
 * Test der InstructorDAO Klasse.
 * 
 * 
 */
public class InstructorDAOImplTest extends TestCase {
	
	/**
	 * 
	 */	
	private Instructor instructor;

	/**
	 * 
	 */	
	private InstructorDAO instructorDAO;

	/**
	 * 
	 */	
	@Override
	@Before
	public void setUp() {
		instructorDAO = TestUtil.getService(InstructorDAO.class);

		instructor = new Instructor();
		final User user = new User();
		user.setFirstname("first");
		user.setLastname("last");
		user.setEmail("test@test.com");
		user.setPassword("test");
		user.setPhone("123456");
		user.setUsername("user_" + UUID.randomUUID().toString());
		try {
			instructor.setUser(user);
		} catch (final RoleDuplicateExc e) {
			// tritt nie auf (kein DB Test!)
		}

		instructorDAO.saveOrUpdate(instructor);
	}

	/**
	 * 
	 */	
	@Override
	@After
	public void tearDown() {
		instructorDAO.rollbackTransaction();
	}

	/**
	 * 
	 */	
	@Test
	public void testFind() {
		assertEquals(instructorDAO.find(instructor.getId()), instructor);
	}

	/**
	 * 
	 */	
	@Test
	public void testListEmpty() {
		assertFalse(instructorDAO.getAll().isEmpty());
	}

	/**
	 * 
	 */	
	@Test
	public void testSearchByName() {
		assertFalse(instructorDAO.searchByName("first").isEmpty());
		assertFalse(instructorDAO.searchByName("last").isEmpty());
		assertFalse(instructorDAO.searchByName("user").isEmpty());
		// assertTrue(instructorDAO.searchByName(UUID.randomUUID().toString()).isEmpty());
	}

	/**
	 * 
	 */	
	@Test
	public void testDelete() {
		instructorDAO.delete(instructor);
		assertNull(instructorDAO.find(instructor.getId()));
	}
}
