package de.beuth.sp.belegsystem.db.dao.hibernate;

import java.util.List;

import de.beuth.sp.belegsystem.db.dao.ParticipantDAO;
import de.beuth.sp.belegsystem.lg.Participant;

/**
 * 
 * Implementation des ParticipantDAO-Interfaces, erweitert generische DAOImpl-Klasse
 * (typisiert auf Participant) und implementiert die zusätzlichen Methoden des
 * ParticipantDAO-Interface.
 * 
 * 
 */
public class ParticipantDAOImpl extends DAOImpl<Participant> implements ParticipantDAO {

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
		 * 
	 * @see de.beuth.sp.belegsystem.db.dao.hibernate.DAOImpl#delete
	 */
	@Override
	public void delete(Participant persistentObject) {
		// Falls der Participant keine andere Rolle mehr hat...
		if (persistentObject.getUser().getRoles().size() == 1) {
			// wird gleich der ganze User gelöscht.
			getSession().delete(persistentObject.getUser());
		} else {
			// ansonsten nur die Participant-Role
			getSession().delete(persistentObject);			
		}
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.beuth.sp.belegsystem.db.ParticipantDAO#searchByName(de.beuth.sp.
	 * belegsystem.lg.Participant)
	 */
	@SuppressWarnings("unchecked")
	public List<Participant> searchByName(String name) {
		
		return getSession().createQuery(
					"FROM " + Participant.class.getName() + " " +  
					"WHERE user.username like :name " + 
					"OR user.firstname like :name " +
					"OR user.lastname like :name " + 
					"OR concat(concat(user.firstname,' '),user.lastname) like :name ").setParameter("name", name + "%")
				.list();
		
		// Beispiel mit der Criteria-Query API von Hibernate
		/*
		return getSession().createCriteria(Participant.class)
				.createCriteria("user")
				.add(Restrictions.or(
					Restrictions.like("username", name, MatchMode.START), 
					Restrictions.or(
						Restrictions.like("firstname lastname", name, MatchMode.START),
						Restrictions.or(
							Restrictions.like("firstname", name, MatchMode.START),
							Restrictions.like("lastname", name, MatchMode.START))
						)
					)
				).list();
		*/
		
	}

}
