package de.beuth.sp.belegsystem.db.dao.hibernate;

import java.util.Calendar;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;

import de.beuth.sp.belegsystem.db.dao.TermDAO;
import de.beuth.sp.belegsystem.lg.Term;

/**
 * 
 * Implementation des TermDAO-Interfaces, erweitert generische DAOImpl-Klasse
 * (typisiert auf Term) und implementiert die zusätzlichen Methoden des
 * TermDAO-Interface.
 * 
 * 
 */
public class TermDAOImpl extends DAOImpl<Term> implements TermDAO {

	/* (non-Javadoc)
	 * @see de.beuth.sp.belegsystem.db.TermDAO#existsTerm(java.util.Calendar, java.util.Calendar)
	 */
	@Override
	public Term existsTerm(final Calendar startDate, final Calendar endDate) {
		final Criteria criteria = this.getSession().createCriteria(Term.class);
		
		// Wenn es einen Term mit StartDatum gibt, der zeitlich zwischen Start und EndDatum fällt
		final Criterion startDateCriterion = Restrictions.between("startDate", startDate, endDate);

		// Wenn es einen Term mit EndDatum gibt, der zeitlich zwischen Start und EndDatum fällt
		final Criterion endDateCriterion = Restrictions.between("endDate", startDate, endDate);
				
		// Wenn es einen Term gibt, der zeitlich vor Start beginnt und nach EndDatum endet
		final LogicalExpression greaterTerm = Restrictions.and(Restrictions.le("startDate", startDate),Restrictions.gt("endDate", endDate));					
				
		criteria.add(
			Restrictions.or(Restrictions.or(startDateCriterion, endDateCriterion), greaterTerm)
		);
		if(criteria.list().size() > 0)
			return (Term) criteria.list().get(0);
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.beuth.sp.belegsystem.db.dao.TermDAO#getTermAtDate(java.util.Calendar)
	 */
	@Override
	public Term getTermAtDate(final Calendar cal) {
		final Criteria criteria = this.getSession().createCriteria(Term.class);
		criteria.add(Restrictions.and(Restrictions.le("startDate", cal),Restrictions.gt("endDate", cal)));					
		if(criteria.list().size() > 0)
			return (Term) criteria.list().get(0);
		return null;		
	}

}
