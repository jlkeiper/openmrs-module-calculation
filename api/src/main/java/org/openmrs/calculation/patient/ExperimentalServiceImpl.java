package org.openmrs.calculation.patient;

import org.openmrs.calculation.db.ExperimentalDAO;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @see ExperimentalService
 */
public class ExperimentalServiceImpl implements ExperimentalService {

	ExperimentalDAO dao;

	public void setDao(ExperimentalDAO dao) {
		this.dao = dao;
	}

	@Override
	public void persistResult(Class<SimplePersistablePatientCalculation> clazz, Integer patientId, Map<String, Object> parameterValues, CalculationResult result, PatientCalculationContext context) throws InstantiationException, IllegalAccessException {
		if (result == null)
			return;

		ExperimentalItem item = buildItem(clazz, patientId, parameterValues, context);
		item.setResult(clazz.newInstance().serialize(result));
		dao.saveResult(item);
	}

	private ExperimentalItem buildItem(Class<SimplePersistablePatientCalculation> clazz, Integer patientId, Map<String, Object> parameterValues, PatientCalculationContext context) {
		ExperimentalItem item = new ExperimentalItem();
		item.setPatientId(patientId);
		item.setClassName(clazz.getName());
		item.setParameterValues(serializeParameterValues(parameterValues));
		item.setDateNow(context.getNow());
		item.setDateUpdated(new Date());
		return item;
	}

	private String serializeParameterValues(Map<String, Object> parameterValues) {
		return null;
	}

	@Override
	public void persistResults(Class<SimplePersistablePatientCalculation> clazz, Map<String, Object> parameterValues, CalculationResultMap resultMap, PatientCalculationContext context) throws InstantiationException, IllegalAccessException {
		if (resultMap == null)
			return;

		for (Integer patientId : resultMap.keySet()) {
			persistResult(clazz, patientId, parameterValues, resultMap.get(patientId), context);
		}
	}

	@Override
	public CalculationResult loadResult(Class<SimplePersistablePatientCalculation> clazz, Integer patientId, Map<String, Object> parameterValues, PatientCalculationContext context) throws InstantiationException, IllegalAccessException {
		ExperimentalItem item = buildItem(clazz, patientId, parameterValues, context);
		item = dao.loadResult(item);

		if (item == null)
			return null;

		if (item.getResult() == null)
			return null;

		return clazz.newInstance().deserialize(item.getResult());
	}

	@Override
	public CalculationResultMap loadResults(Class<SimplePersistablePatientCalculation> clazz, Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
		return null;
	}
}
