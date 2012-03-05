package de.beuth.sp.belegsystem.tapestry.pages.term;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.beuth.sp.belegsystem.db.dao.TermDAO;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.Term;
import de.beuth.sp.belegsystem.tapestry.base.impl.BasePageImpl;
import de.beuth.sp.belegsystem.tapestry.services.Restricted;

/**
 * Page-Klasse für die Auflistung alle Semester.
 * 
 */
@Restricted(allowedFor = { Admin.class })
public class ListTerm extends BasePageImpl {
	
	/**
	 * DAO Klasse des Semesters
	 */
	@Inject
	private TermDAO termDAO;
	
	/**
	 * Attribute, in dem das aktuelle Semester
	 * gespeichert wird
	 */
	@Property
	private Term term;
	
	/**
	 * Eine Liste, mit allen Semestern, die gelöscht werden sollen.
	 * Der Benutzer wählt auf der Oberfläche alle Semester aus, 
	 * die gelöscht werden sollen. 
	 */
	private List<Term> termsToDelete;
	
	/**
	 * Gibt eine Liste mit allen vorhandenen Semestern zurück.
	 * @return
	 */
	public List<Term> getTerms() {
		return termDAO.getAll();
	}
	
	public void onPrepareForSubmit() {
		termsToDelete = new ArrayList<Term>();
	}
	
	/**
	 * Löscht alle ausgewählen Semester
	 */
	@CommitAfter
	public void onSuccessFromDeletables() {
		termDAO.deleteList(termsToDelete);
	}	

	public boolean isDelete() {
		return false;
	}

	public void setDelete(final boolean delete) {
		if (delete) {
			termsToDelete.add(term);
		}
	}

}
