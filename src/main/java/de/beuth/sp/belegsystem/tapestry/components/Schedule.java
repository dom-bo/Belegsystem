package de.beuth.sp.belegsystem.tapestry.components;

import java.util.Set;

import multex.MultexUtil;
import multex.extension.AnnotatedExc;
import multex.extension.ExcMessage;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Participant;
import de.beuth.sp.belegsystem.lg.User;

/**
 * Klasse für die Schedule-Komponente. Besteht im Prinzip nur aus einer Liste
 * der Unter-Komponente ScheduleRow.
 * 
 * Die Schedule-Komponente ist in zwei Komponenten aufgeteilt, um sie möglichst
 * flexibel zu gestalten (so ist es einfach möglich, weitere Tage bzw. Zeiträume
 * in der TML-Datei zu definieren, ohne Veränderungen an der Logik vorzunehmen).
 * In der TML-Datei wird für jeden Zeitschlitz eine ScheduleRow definiert. Die
 * Aufteilung in Reihen hat ua. mit dem HTML-Syntax bzw. der Definition von
 * Tabellen zu tun.
 * 
 * 
 */
public class Schedule {

	@ExcMessage("Der Benutzer {0} hat eine ungültige Rolle, der Stundenplan kann nicht erstellt werden")
	public static final class InvalidRoleExc extends AnnotatedExc {
		private static final long serialVersionUID = -6869018945610798739L;
	}

	/**
	 * Der Benutzer dem dieser Stundenplan gehört. Er wird der Komponente als
	 * Parameter übergeben.
	 */
	@Property
	@Parameter(required = true)
	private User user;

	/**
	 * Gibt alle Kurs des übergebenen Benutzer zurück. Da die Rolle unklar ist
	 * (kann Instructor oder Participant sein), muss vorher eine Überprüfung
	 * durchgeführt werden.
	 * 
	 * @return Liste alle Kurse.
	 * @throws Exception
	 */
	public Set<Course> getUserCourses() throws InvalidRoleExc {
		final Participant participant = user.getRoleOfType(Participant.class);
		if (participant != null) {
			return participant.getCourses();
		}

		final Instructor instructor = user.getRoleOfType(Instructor.class);
		if (instructor != null) {
			return instructor.getCourses();
		}

		throw MultexUtil.create(InvalidRoleExc.class, user.getUsername());
	}

}
