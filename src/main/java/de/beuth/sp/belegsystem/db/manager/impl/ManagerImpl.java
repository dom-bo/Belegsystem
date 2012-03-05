package de.beuth.sp.belegsystem.db.manager.impl;

import de.beuth.sp.belegsystem.db.manager.Manager;
import de.beuth.sp.belegsystem.lg.base.Persistent;

/**
 * Implementation des generischen Manager-Interfaces als abstrakte Klasse, d.h. nur die konkreten Ableitungen hiervon können tatsächlich benutzt und instanziiert werden (wie z.B. CourseManagerImpl)
 *
 * @param <T> Parameter für persistente Logik-Klasse für die der jeweilige Manager zuständig ist.
 */
public abstract class ManagerImpl<T extends Persistent> implements Manager<T> {
	
	@Override
	public void rollbackTransaction() {
		getDAO().rollbackTransaction();
	};
	
	@Override
	public void commitTransaction() {
		getDAO().commitTransaction();
	}	
}
