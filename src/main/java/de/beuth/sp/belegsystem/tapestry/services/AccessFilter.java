package de.beuth.sp.belegsystem.tapestry.services;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.ComponentSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;

import de.beuth.sp.belegsystem.Settings;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Participant;
import de.beuth.sp.belegsystem.lg.Program;
import de.beuth.sp.belegsystem.lg.Role;
import de.beuth.sp.belegsystem.lg.User;
import de.beuth.sp.belegsystem.lg.base.Persistent;
import de.beuth.sp.belegsystem.tapestry.BasePage;
import de.beuth.sp.belegsystem.tapestry.pages.Login;

public class AccessFilter implements ComponentRequestFilter {

	private final PageRenderLinkSource renderLinkSource;

	private final ComponentSource componentSource;

	private final Response response;

	private final ApplicationStateManager applicationStateManager;
	
	@Inject
	private RequestGlobals requestGlobals;
	
	public AccessFilter(PageRenderLinkSource renderLinkSource, ComponentSource componentSource,
			Response response, ApplicationStateManager applicationStateManager) {
		this.renderLinkSource = renderLinkSource;
		this.componentSource = componentSource;
		this.response = response;
		this.applicationStateManager = applicationStateManager;
	}

	public void handleComponentEvent(ComponentEventRequestParameters parameters, ComponentRequestHandler handler)
			throws IOException {

		if (dispatchedToLoginPage(parameters.getActivePageName(), parameters.getPageActivationContext())) {
			return;
		}	

		handler.handleComponentEvent(parameters);

	}

	public void handlePageRender(PageRenderRequestParameters parameters, ComponentRequestHandler handler)
			throws IOException {
		
		if (dispatchedToLoginPage(parameters.getLogicalPageName(), parameters.getActivationContext())) {
			return;
		}

		handler.handlePageRender(parameters);
	}

	@SuppressWarnings("unchecked")
	private boolean dispatchedToLoginPage(String pageName, EventContext context) throws IOException {		
		Class<? extends BasePage> pageClass;		
		try {
			pageClass = (Class<? extends BasePage>) Class.forName(componentSource.getPage(pageName).getClass().getName());						
		} catch (ClassNotFoundException e) {
			//this should not happen at all
			throw new RuntimeException("Class of page couldn't be found");
		}

		//Bei public keine weitere Überprüfung, alles ok
		if (pageClass.isAnnotationPresent(Public.class)) {
			return false;
		}		
		
		
		User user = this.applicationStateManager.getIfExists(User.class);
		if (checkAccess(user, pageClass.getAnnotation(Restricted.class), pageClass, context)) {		
			return false;
		}
		
		if (user!=null) {
			requestGlobals.getRequest().getSession(true).setAttribute(Settings.FLASH_MESSAGE_SESSION_ATTRIBUTE, 
						"Ihre Rechte reichen nicht aus." + 
						"Melden Sie sich als ein Benutzer mit ausreichenden Rechten an, um die URL '" + requestGlobals.getHTTPServletRequest().getRequestURL() + "' aufrufen zu können.");
		}
		
		//ok, entweder kein User eingeloggt oder Rechte reichen nicht aus
		Object[] contextParameters = new String[context.getCount()+1];
		contextParameters[0] = pageName;
		for (int i = 0; i < context.getCount(); i++) {
			contextParameters[i+1] = context.get(String.class, i);
		}
		Link link = renderLinkSource.createPageRenderLinkWithContext(Login.class, contextParameters);

		response.sendRedirect(link);

		return true;
	}
	
	public static boolean checkAccess(User user, Restricted restrictedAnnotation) {
		return checkAccess(user, restrictedAnnotation, null, null);
	}
	
	private static boolean checkAccess(User user, Restricted restrictedAnnotation, Class<? extends BasePage> pageClass, EventContext context) {
		
		if (restrictedAnnotation==null) { 
			//Annotation==null, also für alle (auch nicht angemeldete Nutzer!) freigegeben
			return true;
		}
		
		if (user!=null) { 
			//User in Session gespeichert, also schon jemand eingeloggt
			
			boolean checkVal = true;
			
			if (restrictedAnnotation.onlyIfAssociatedToProgram()) {
				//Check ob User mit Program assoziert
				checkVal = checkProgramAssociation(user, pageClass, context);
			}				

			
			if (restrictedAnnotation.allowedFor().length!=0) {
				//Check auf erlaubte Rollen
				List<Class<? extends Role>> allowedRoles = Arrays.asList(restrictedAnnotation.allowedFor());
				boolean userHasAllowedRole = false;
				for (Role role : user.getRoles()) {
					if (allowedRoles.contains(role.getClass()) && checkVal) {
						userHasAllowedRole = true;
						break;
					}
				}
				//Falls keine allowedRole gefunden...
				if (!userHasAllowedRole) {
					checkVal = false;
				}				
			}
			
			if (restrictedAnnotation.onlyForSuperAdmin()) {
				//Check ob nur für SuperAdmins erlaubt
				//Der Check und Die Annotation onlyIfSuperAdmin ist höherwertiger als ProgramAssociation && allowedRoles und kann
				//daher den bisher gesetzten checkVal überschreiben.
				if (user.hasRoleOfType(Admin.class) && user.getRoleOfType(Admin.class).isSuperAdmin()) {
					checkVal = true;
				} else {
					checkVal = false;
				}
			}
						
			return checkVal;
		}				
		//kein User eingeloggt, aber Annotation gesetzt, also nicht erlaubt
		return false;
	}
	
	private static boolean checkProgramAssociation(User user, Class<? extends BasePage> pageClass, EventContext context) {
		
		//falls kein context oder pageClass vorhanden ist müssen wir auch keine Program-Assoziation überprüfen
		if (context==null) {
			return true;
		}
		if (pageClass==null) {
			return true;
		}
		
		if (user.hasRoleOfType(Admin.class) && user.getRoleOfType(Admin.class).isSuperAdmin()) {
			//Super Admins dürfen unabhängig von Program Association!
			return true;
		}
		
		Persistent persistent = null;
		for (Field field : pageClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(PageActivationContext.class)) {
				persistent = (Persistent) context.get(field.getType(), 0); 
				break;
			}
		}
		//ebenso falls bei keine Feld PageActivationContext gesetzt ist aus dem wir die Klasse des Contexts extrahieren könnten, also persistent == null;
		if (persistent==null) {
			return true;
		}
		List<Program> programsOfContext = new ArrayList<Program>();
		if (persistent instanceof Program) {
			programsOfContext.add((Program) persistent);
		} else if (persistent instanceof Course) {
			programsOfContext.add(((Course)persistent).getProgram());
		} else if (persistent instanceof Role) {
			programsOfContext.addAll(getProgramsOfUser(((Role)persistent).getUser()));
		} else if  (persistent instanceof User) {
			programsOfContext.addAll(getProgramsOfUser((User)persistent));
		}
		
		List<Program> programsOfUser = getProgramsOfUser(user);
		for (Program program : programsOfContext) {
			if (programsOfUser.contains(program)) {
				return true;
			}
		}
		return false;		
	}
	
	private static List<Program> getProgramsOfUser(User user) {
		List<Program> programsOfUser = new ArrayList<Program>();
		for (Role role : user.getRoles()) {
			if (role instanceof Participant) {
				programsOfUser.add(((Participant)role).getProgram());
			} else if (role instanceof Instructor) {
				programsOfUser.addAll(((Instructor)role).getPrograms());
			} else if (role instanceof Admin) {
				programsOfUser.addAll(((Admin)role).getAdministeredPrograms());
			}						
		}
		return programsOfUser;
	}
}
