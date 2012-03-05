package de.beuth.sp.belegsystem.db;

import java.util.UUID;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.beuth.sp.belegsystem.TestUtil;
import de.beuth.sp.belegsystem.db.dao.ParticipantDAO;
import de.beuth.sp.belegsystem.lg.Participant;
import de.beuth.sp.belegsystem.lg.User;
import de.beuth.sp.belegsystem.lg.User.RoleDuplicateExc;

/**
 * 
 * Test der ParticpantDAO Klasse.
 * 
 * 
 */
public class ParticipantDAOImplTest extends TestCase {
	
	/**
	 * 
	 */	
	private Participant participant;
	
	/**
	 * 
	 */	
	private ParticipantDAO participantDAO;

	/**
	 * 
	 */
	@Before
	public void setUp() {
		participantDAO = TestUtil.getService(ParticipantDAO.class);
		participant = new Participant();

		User user = new User();
		user.setFirstname("first");
		user.setLastname("last");
		user.setEmail("test@test.com");
		user.setPassword("test");
		user.setPhone("123456");
		user.setUsername("user_" + UUID.randomUUID().toString());
		try {
			participant.setUser(user);
		} catch (final RoleDuplicateExc e) {
			// tritt nie auf (kein DB Test!)
		}

		participantDAO.saveOrUpdate(participant);
	}

	/**
	 * 
	 */	
	@After
	public void tearDown() {
		participantDAO.rollbackTransaction();
	}

	/**
	 * 
	 */	
	@Test
	public void testFind() {
		assertEquals(participantDAO.find(participant.getId()), participant);
	}

	/**
	 * 
	 */	
	@Test
	public void testListEmpty() {
		assertFalse(participantDAO.getAll().isEmpty());
	}
	
	/**
	 * 
	 */	
	@Test
	public void testSearchByName() {
		assertFalse(participantDAO.searchByName("first").isEmpty());
		assertFalse(participantDAO.searchByName("last").isEmpty());
		assertFalse(participantDAO.searchByName("user").isEmpty());
		// assertTrue(instructorDAO.searchByName(UUID.randomUUID().toString()).isEmpty());
	}	

	/**
	 * 
	 */	
	@Test
	public void testDelete() {
		participantDAO.delete(participant);
		assertNull(participantDAO.find(participant.getId()));
	}
}
