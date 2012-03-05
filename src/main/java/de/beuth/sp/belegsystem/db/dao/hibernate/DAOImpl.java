package de.beuth.sp.belegsystem.db.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;

import de.beuth.sp.belegsystem.db.dao.DAO;
import de.beuth.sp.belegsystem.lg.base.Persistent;

/**
 * Implementation des generischen DAO-Interfaces als abstrakte Klasse, d.h. nur
 * die konkreten Ableitungen hiervon können tatsächlich benutzt und instanziiert
 * werden (wie z.B. CourseDAOImpl)
 * 
 * 
 * @param <T>
 *            Persistent
 */
public abstract class DAOImpl<T extends Persistent> implements DAO<T> {

	@Inject
	private HibernateSessionManager sessionManager;

	@SuppressWarnings("unchecked")
	private final Class<T> persistentTypeClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass())
			.getActualTypeArguments()[0];

	protected Session getSession() {
		return sessionManager.getSession();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <PK extends Serializable> T find(PK id) {
		return (T) getSession().get(persistentTypeClass, id);
	}

	@Override
	public void saveOrUpdate(T objectToPersist) {
		getSession().saveOrUpdate(objectToPersist);
	}

	@Override
	public void delete(T persistentObject) {
		getSession().delete(persistentObject);
	}

	@Override
	public <PK extends Serializable> void delete(PK id) {
		delete(find(id));
	}

	@Override
	public void deleteList(List<T> persistentObjects) {
		for (T persistentObject : persistentObjects) {
			delete(persistentObject);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAll() {
		return getSession().createQuery("from " + persistentTypeClass.getName()).list();
	}

	@Override
	public void rollbackTransaction() {
		sessionManager.abort();
		getSession().clear();
	};

	@Override
	public void commitTransaction() {
		sessionManager.commit();
	}

}
