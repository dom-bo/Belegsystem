package de.beuth.sp.belegsystem.tapestry.components;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.PageLink;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.ComponentEventLinkEncoder;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.services.javascript.StylesheetLink;
import org.apache.tapestry5.services.javascript.StylesheetOptions;

import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Instructor;
import de.beuth.sp.belegsystem.lg.Participant;
import de.beuth.sp.belegsystem.lg.User;
import de.beuth.sp.belegsystem.tapestry.BasePage;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.pages.Login;
import de.beuth.sp.belegsystem.tapestry.pages.Profile;
import de.beuth.sp.belegsystem.tapestry.pages.admin.ListAdmin;
import de.beuth.sp.belegsystem.tapestry.pages.course.BookCourse;
import de.beuth.sp.belegsystem.tapestry.pages.course.InstructorCourseView;
import de.beuth.sp.belegsystem.tapestry.pages.course.ListCourse;
import de.beuth.sp.belegsystem.tapestry.pages.course.ParticipantCourseView;
import de.beuth.sp.belegsystem.tapestry.pages.instructor.ListInstructor;
import de.beuth.sp.belegsystem.tapestry.pages.participant.ListParticipant;
import de.beuth.sp.belegsystem.tapestry.pages.program.ListProgram;
import de.beuth.sp.belegsystem.tapestry.pages.term.ListTerm;
import de.beuth.sp.belegsystem.tapestry.pages.user.ListUser;

/**
 * Generelle Layout-Komponente für unsere Pages. Stell Navigation abhängig von
 * angemeldetem User zusammen.
 * 
 * 
 */
@Import(stylesheet = "context:belegsystem_layout/css/my_layout.css")
public class BelegsystemLayout {

	@InjectContainer
	@Property
	private BasePage basePage;

	@Environmental
	private JavaScriptSupport javaScriptSupport;

	@Inject
	@Path("context:belegsystem_layout/css/patches/patch_my_layout.css")
	private Asset ieOnlyStylesheet;

	@Inject
	private ComponentResources resources;

	@Inject
	private Messages messages;

	@Inject
	private ComponentClassResolver resolver;

	@Inject
	private ComponentEventLinkEncoder encoder;

	@Inject
	private Request request;

	/**
	 * Loop-Variable
	 */
	@Property
	private String pageName;

	private Map<String, Object[]> contextParameterMap = new HashMap<String, Object[]>();

	/**
	 * Fügt spezielles Stylesheet für den Internet Explorer hinzu.
	 */
	void afterRender() {
		javaScriptSupport.importStylesheet(new StylesheetLink(ieOnlyStylesheet, new StylesheetOptions()
				.withCondition("IE")));
	}

	/**
	 * Erzeugt die Elemente für Top-Navigation in Abhängigkeit vom angemeldetem
	 * Nutzer und gibt diese zurück.
	 * 
	 */
	public Iterable<String> getNavPages() {

		final Set<String> pageNames = new LinkedHashSet<String>();

		// Nicht eingeloggt
		if (!basePage.isUserLoggedIn()) {
			pageNames.add(resolver.resolvePageClassNameToPageName(Login.class.getName()));
			return pageNames;
		}

		// Eingeloggter User
		final User user = basePage.getLoggedInUser();
		if (user.hasRoleOfType(Participant.class)) {
			pageNames.add(resolver.resolvePageClassNameToPageName(BookCourse.class.getName()));
			pageNames.add(resolver.resolvePageClassNameToPageName(ParticipantCourseView.class.getName()));
		}
		if (user.hasRoleOfType(Instructor.class)) {
			pageNames.add(resolver.resolvePageClassNameToPageName(InstructorCourseView.class.getName()));
		}
		if (user.hasRoleOfType(Admin.class)) {
			pageNames.add(resolver.resolvePageClassNameToPageName(ListCourse.class.getName()));
			pageNames.add(resolver.resolvePageClassNameToPageName(ListParticipant.class.getName()));
			pageNames.add(resolver.resolvePageClassNameToPageName(ListInstructor.class.getName()));
			if (user.getRoleOfType(Admin.class).isSuperAdmin()) {
				pageNames.add(resolver.resolvePageClassNameToPageName(ListAdmin.class.getName()));
			}
			pageNames.add(resolver.resolvePageClassNameToPageName(ListUser.class.getName()));
			pageNames.add(resolver.resolvePageClassNameToPageName(ListProgram.class.getName()));
			pageNames.add(resolver.resolvePageClassNameToPageName(ListTerm.class.getName()));
		}

		// Profile-Seite inklusive aller Context-Parameter des vorherigen
		// Request so dass wir dahin zurückkehren können
		pageNames.add(resolver.resolvePageClassNameToPageName(Profile.class.getName()));
		buildContextForBackLink(Profile.class);

		return pageNames;
	}

	/**
	 * Hilfsmethode um Back-Funktionalität für ausgewählte Seiten nach
	 * Abspeichern eine sFormulars zu gewährleisten. Wird eingesetzt bei der
	 * Profile-Page.
	 * 
	 * @param pageClass
	 */
	private void buildContextForBackLink(Class<? extends BasePageImpl> pageClass) {
		// Context zusammenbauen
		final String[] oldContext = encoder.decodePageRenderRequest(request).getActivationContext().toStrings();
		final String[] newContext = new String[oldContext.length + 1];
		newContext[0] = resources.getPageName();
		for (int i = 0; i < oldContext.length; i++) {
			newContext[i + 1] = oldContext[i];
		}
		contextParameterMap.put(resolver.resolvePageClassNameToPageName(pageClass.getName()), newContext);
	}

	/**
	 * Überprüft ob beim Iterieren über die Elemente in die Top-Navigation
	 * gerade das Element der aktiven Seite gerendert wird. Wird benötigt um in
	 * der Navigation die aktuelle Seite mit einem eigenen CSS-Stil
	 * auszustatten.
	 */
	public boolean isActivePage() {
		return pageName.equals(resources.getPageName());
		// return pageName.startsWith(resources.getPageName().split("/")[0]);
	}

	/**
	 * Gibt den Anzeigenamen für das aktuelle Top-Navigationselement zurück
	 */
	public String getPageDisplayName() {
		return messages.get(pageName);
	}

	/**
	 * Gibt den Titel der Seite zurück (für den Namen des Browser-Fensters bzw.
	 * -Tabs)
	 * 
	 * @return
	 */
	public String getTitle() {
		return messages.get(resources.getPageName());
	}

	/**
	 * Gibt einen etwaigen Context für den {@link PageLink} des aktuellen
	 * Navigationselements zurück (der zuvor beim Zusammenbauen der Liste in
	 * {@link #getNavPages()} erstellt wurde).
	 */
	public Object[] getContext() {
		return contextParameterMap.get(pageName);
	}
}
