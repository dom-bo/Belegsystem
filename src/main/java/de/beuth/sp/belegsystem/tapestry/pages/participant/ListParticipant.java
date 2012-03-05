package de.beuth.sp.belegsystem.tapestry.pages.participant;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.db.dao.ParticipantDAO;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Participant;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse für die Auflistung alle Instructors.
 * 
 */
@Restricted(allowedFor = Admin.class)
public class ListParticipant extends BasePageImpl {

	/**
	 * DAO Klasse für die Persistenz.
	 */
	@Inject
	private ParticipantDAO participantDAO;

	/**
	 * Lokale Variable für die Schleife in der TML-Datei.
	 */
	@SuppressWarnings("unused")
	@Property
	private Participant participant;

	/**
	 * Gibt alle Participants des Systems zurück.
	 * 
	 * @return Alle Participants.
	 */
	public List<Participant> getParticipants() {
		return participantDAO.getAll();
	}

	/**
	 * Event-Handler wenn ein Participant vom Benutzer gelöscht wird.
	 * 
	 * @param instructor
	 *            Der zu löschende Instructor.
	 */
	@CommitAfter
	public void onActionFromDelete(final Participant participant) {
		participantDAO.delete(participant);
	}

}
