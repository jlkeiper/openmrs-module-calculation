package org.openmrs.calculation.db;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.calculation.patient.PersistableCalculation;
import org.openmrs.calculation.result.PersistedPatientResult;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @see PersistedPatientResultDAO
 */
public class HibernatePersistedPatientResultDAO implements PersistedPatientResultDAO {

	SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void saveResult(PersistedPatientResult item) {
		sessionFactory.getCurrentSession().saveOrUpdate(item);
	}

	@Override
	public List<PersistedPatientResult> loadResults(Class<? extends PersistableCalculation> clazz, Collection<Integer> cohort, String serializedParameterValues, Date dateNow) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(PersistedPatientResult.class);

		crit.add(Restrictions.in("patientId", cohort));
		crit.add(Restrictions.eq("className", clazz.getName()));
		if (serializedParameterValues != null)
			crit.add(Restrictions.eq("parameterValues", serializedParameterValues));

		// ignoring dateNow

		return (List<PersistedPatientResult>) crit.list();
	}
}
