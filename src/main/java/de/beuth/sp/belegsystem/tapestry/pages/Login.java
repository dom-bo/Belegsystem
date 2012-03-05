package de.beuth.sp.belegsystem.tapestry.pages;

import java.io.IOException;
import java.util.List;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;

import de.beuth.sp.belegsystem.db.dao.UserDAO;
import de.beuth.sp.belegsystem.lg.User;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Public;

/**
 * Login-Page unserer Anwendung.
 * 
 */
@Public
public class Login extends BasePageImpl {
	
	@Property
	private String username;
	
	private List<String> contextParameters;
	
	@Property
	private String password;
	
	@Inject
	private Messages messages;
	
	@InjectComponent
	private Form loginForm;
	
    @SuppressWarnings("unused")
	@SessionState(create=false)
    private User loggedInUser;
    
    @Inject
    private UserDAO userDAO;
    
    @Inject
    private PageRenderLinkSource renderLinkSource;
    
	public void onActivate(List<String> contextParameters) {
		this.contextParameters = contextParameters;
	}

	public List<String> onPassivate() {
		return contextParameters;
	}
		
	Object recordLoginError() {
		loginForm.recordError(messages.get("login-failure"));
		return Login.class;
	}
	
	Object onSuccessFromLoginForm() throws IOException {
		User user = userDAO.getUserByUsernameAndEncodedPassword(username, password);		
		
		if (user == null) {
			return recordLoginError();
		}
		
		//Authentifzierung hat geklappt, also User speichern...
		this.loggedInUser = user;
		
		//...und redirecten
		Link redirectTo = renderLinkSource.createPageRenderLink(Index.class);
		
		if (contextParameters!=null && contextParameters.size()>0) {
			Object[] parameters = new String[contextParameters.size()-1];
			for (int i = 0; i < parameters.length; i++) {
				parameters[i] = contextParameters.get(i+1);
			}
			redirectTo = renderLinkSource.createPageRenderLinkWithContext(contextParameters.get(0), parameters);				
		}
		return redirectTo;
	}
	
}
