package org.openmrs.calculation.db;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.calculation.patient.ExperimentalItem;

import java.io.Serializable;
import java.util.List;

/**
 * @see ExperimentalDAO
 */
public class HibernateExperimentalDAO implements ExperimentalDAO {

	SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void saveResult(ExperimentalItem item) {
		sessionFactory.getCurrentSession().saveOrUpdate(item);
	}

	@Override
	public ExperimentalItem loadResult(ExperimentalItem item) {
		if (item == null)
			return null;

		Criteria crit = sessionFactory.getCurrentSession().createCriteria(ExperimentalItem.class);
		List<ExperimentalItem> items = crit.list();

		crit.add(Restrictions.eq("patientId", item.getPatientId()));
		crit.add(Restrictions.eq("className", item.getClassName()));
		if (item.getParameterValues() != null)
			crit.add(Restrictions.eq("parameterValues", item.getParameterValues()));

		// ignoring dateNow and dateUpdated

		items = crit.list();

		return (ExperimentalItem) crit.uniqueResult();
	}
}
