package de.beuth.sp.belegsystem.tapestry.pages;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;

/**
 * Einfache Logout-Page unserer Anwendung ohne eigenen Seiteninhalt. Stellt nur
 * die Funktionalität der Invalidierung der Session über URL zur Verfügung.
 * 
 * 
 */
public class Logout extends BasePageImpl {
	
	@Inject
	private RequestGlobals requestGlobals;
	
	Object onActivate() {
		requestGlobals.getRequest().getSession(true).invalidate();
		return Index.class;
	}

}
