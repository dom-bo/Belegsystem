package de.beuth.sp.belegsystem.db;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.beuth.sp.belegsystem.TestUtil;
import de.beuth.sp.belegsystem.db.dao.LessonDAO;
import de.beuth.sp.belegsystem.lg.Lesson;

public class LessonDAOImplTest extends TestCase {
	private Lesson lesson;
	private LessonDAO lessonDAO;


	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	@Before
	public void setUp() {
		lessonDAO = TestUtil.getService(LessonDAO.class);
		lesson = new Lesson();
	}

	/**
	 * Testet das einfache speichern eines Lesson Objekts und
	 * die find Methode des DAOs
	 */
	@Test
	public void testCreateAndFind() {
		lessonDAO.saveOrUpdate(lesson);
		assertEquals(lessonDAO.find(lesson.getId()), lesson);
	}

	/**
	 * 
	 */
	@Test
	public void testList() {
		lessonDAO.saveOrUpdate(lesson);
		assertFalse(lessonDAO.getAll().isEmpty());
	}

	/**
	 * Testet das Löschen eines Lesson Objekts
	 */
	@Test
	public void testDelete() {
		lessonDAO.saveOrUpdate(lesson);
		lessonDAO.delete(lesson);
		assertNull(lessonDAO.find(lesson.getId()));
	}

	@Override
	@After
	public void tearDown() {
		lessonDAO.rollbackTransaction();
	}
}
