package de.beuth.sp.belegsystem.db.dao.hibernate;

import java.util.List;

import de.beuth.sp.belegsystem.db.dao.InstructorDAO;
import de.beuth.sp.belegsystem.lg.Instructor;

/**
 * 
 * Implementation des InstructorDAO-Interfaces, erweitert generische
 * DAOImpl-Klasse (typisiert auf Instructor) und implementiert die zusätzlichen
 * Methoden des CourseDAO-Interface.
 * 
 * 
 */
public class InstructorDAOImpl extends DAOImpl<Instructor> implements InstructorDAO {

	/**
	 * Überschreibt delete-Methode aus DAOImpl. <br/>
	 * Gilt für alle Role-Klassen: <br/>
	 * Löscht ganzen User gleich mit, falls die Instructor-Rolle die letzte
	 * verbliebene Rolle ist. Ist konzeptionell so vorgesehen, könnte aber auch
	 * in Manager ausgelagert werden so dass im DAO nur die atomare Methode
	 * ausgeführt wird. <br/>
	 * Wir haben uns aber dafür entschieden dass so zu machen da wir diese
	 * beiden Sachen als elementar zusammengehörig begreifen (und wir keine User
	 * ohne Rollen in der DB haben wollen).
	 * 
	 * @see de.beuth.sp.belegsystem.db.dao.hibernate.DAOImpl#delete
	 */
	@Override
	public void delete(Instructor persistentObject) {
		// Falls der Instructor keine andere Rolle mehr hat...
		if (persistentObject.getUser().getRoles().size() == 1) {
			// wird gleich der ganze User gelöscht.
			getSession().delete(persistentObject.getUser());
		} else {
			// ansonsten nur die Instructor-Role
			getSession().delete(persistentObject);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.beuth.sp.belegsystem.db.InstructorDAO#searchByName(de.beuth.sp.
	 * belegsystem.lg.Instructor)
	 */
	@SuppressWarnings("unchecked")
	public List<Instructor> searchByName(String name) {

		return getSession()
				.createQuery(
						"FROM " + Instructor.class.getName() + " " + "WHERE user.username like :name "
								+ "OR user.firstname like :name " + "OR user.lastname like :name "
								+ "OR concat(concat(user.firstname,' '),user.lastname) like :name ")
				.setParameter("name", name + "%").list();

		// Beispiel mit der Criteria-Query API von Hibernate
		/*
		 * return getSession().createCriteria(Instructor.class)
		 * .createCriteria("user") .add(Restrictions.or(
		 * Restrictions.like("username", name, MatchMode.START),
		 * Restrictions.or( Restrictions.like("firstname lastname", name,
		 * MatchMode.START), Restrictions.or( Restrictions.like("firstname",
		 * name, MatchMode.START), Restrictions.like("lastname", name,
		 * MatchMode.START)) ) ) ).list();
		 */

	}

}
