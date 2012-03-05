package de.beuth.sp.belegsystem.tapestry.base.impl;

import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ExceptionReporter;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

import de.beuth.sp.belegsystem.Settings;
import de.beuth.sp.belegsystem.exceptions.ExcUtil;
import de.beuth.sp.belegsystem.lg.User;
import de.beuth.sp.belegsystem.tapestry.BasePage;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Basisklasse für unsere Page-Seiten.
 * 
 *
 */
@Restricted
public abstract class BasePageImpl implements ExceptionReporter, BasePage {
	
    private String exceptionMessage;
    
    @Inject
    private Messages messages;
    
    @Inject
	private Request request;
    
    @SessionState(create=false)
    private User loggedInUser;
    
    @Override
    public void reportException(Throwable exception) {
    	exceptionMessage = ExcUtil.generateExceptionMessage(exception, messages);
    }
    
    @Override
    public String getExceptionMessage() {
		return exceptionMessage;
	}
    
    @Override
    public String getFlashMessage() {
    	Session session = request.getSession(false);
    	if (session!=null) {
    		return (String) session.getAttribute(Settings.FLASH_MESSAGE_SESSION_ATTRIBUTE);				
    	}
		return null;
	}
    
    void cleanupRender() {
    	Session session = request.getSession(false);
    	if (session!=null) {
    		//löscht flashMessage wieder aus der Session (da ja nur _flash_Message!)
    		session.setAttribute(Settings.FLASH_MESSAGE_SESSION_ATTRIBUTE, null);
    	}
    }
	
    @Override
    public User getLoggedInUser() {
    	return loggedInUser;
    }
    
    @Override
    public boolean isUserLoggedIn() {
    	return loggedInUser != null;
    }
	
}
