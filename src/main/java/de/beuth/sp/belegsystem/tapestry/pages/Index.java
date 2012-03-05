package de.beuth.sp.belegsystem.tapestry.pages;

import de.beuth.sp.belegsystem.Settings;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Participant;
import de.beuth.sp.belegsystem.lg.User;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;

/**
 * Index-Page unserer Anwendung. Dient nur dazu auf die jeweiligen Startseiten
 * eines Users abhängig von seiner Rolle weiterzuleiten.
 * 
 */
public class Index extends BasePageImpl {
	
	Object onActivate() {
		if (isUserLoggedIn()) {
			User user = getLoggedInUser();
			if (user.hasRoleOfType(Admin.class)) {
				return Settings.getDefaultPageAdmin();
			}
			if (user.hasRoleOfType(Instructor.class)) {
				System.out.println(Settings.getDefaultPageInstructor());
				return Settings.getDefaultPageInstructor();
			}
			if (user.hasRoleOfType(Participant.class)) {
				return Settings.getDefaultPageParticipant();
			}			
		}
		return Login.class;
	}	
}
