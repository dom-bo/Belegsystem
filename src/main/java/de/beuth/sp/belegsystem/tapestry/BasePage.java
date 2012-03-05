package de.beuth.sp.belegsystem.tapestry;

import de.beuth.sp.belegsystem.lg.User;

public interface BasePage {

	User getLoggedInUser();

	String getFlashMessage();

	boolean isUserLoggedIn();

	String getExceptionMessage();

}