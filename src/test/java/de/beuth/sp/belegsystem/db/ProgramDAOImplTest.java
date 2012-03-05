package de.beuth.sp.belegsystem.db;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.beuth.sp.belegsystem.TestUtil;
import de.beuth.sp.belegsystem.db.dao.ProgramDAO;
import de.beuth.sp.belegsystem.lg.Program;

/**
 * 
 *
 */
public class ProgramDAOImplTest extends TestCase {
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
	public void testCreateAndFind() {
		programDAO.saveOrUpdate(program);
		assertEquals(programDAO.find(program.getId()), program);
	}
	
	@Test
	public void testList() {
		programDAO.saveOrUpdate(program);
		assertFalse(programDAO.getAll().isEmpty());
	}
	
	@Test
	public void testDelete() {
		programDAO.saveOrUpdate(program);
		programDAO.delete(program);
		assertNull(programDAO.find(program.getId()));
	}	
	
	@After
	public void tearDown() {
		programDAO.rollbackTransaction();
	}

}
