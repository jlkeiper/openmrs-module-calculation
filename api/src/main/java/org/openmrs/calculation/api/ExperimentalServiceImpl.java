package org.openmrs.calculation.api;

import org.openmrs.calculation.result.PersistedPatientResult;
import org.openmrs.calculation.db.PersistedPatientResultDAO;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PersistableCalculation;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.PersistedPatientResultMap;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @see org.openmrs.calculation.api.ExperimentalService
 */
public class ExperimentalServiceImpl implements ExperimentalService {

	PersistedPatientResultDAO persistedPatientResultDAO;

	public void setPersistedPatientResultDAO(PersistedPatientResultDAO persistedPatientResultDAO) {
		this.persistedPatientResultDAO = persistedPatientResultDAO;
	}

	@Override
	public void persistResult(Class<? extends PersistableCalculation> clazz, Integer patientId, Map<String, Object> parameterValues, CalculationResult result, PatientCalculationContext context) throws InstantiationException, IllegalAccessException {
		if (result == null)
			return;

		// TODO consider looking for this result in the table already and just modifying it

		String serializedResult = clazz.newInstance().serialize(result);
		PersistedPatientResult persistedPatientResult = new PersistedPatientResult();
		persistedPatientResult.setPatientId(patientId);
		persistedPatientResult.setResult(serializedResult);
		persistedPatientResult.setClassName(clazz.getName());
		persistedPatientResult.setDateNow(context.getNow());
		persistedPatientResult.setDateUpdated(new Date());
		persistedPatientResult.setParameterValues(serializeParameterValues(parameterValues));

		persistedPatientResultDAO.saveResult(persistedPatientResult);
	}

	private String serializeParameterValues(Map<String, Object> parameterValues) {
		// TODO actually serialize parameter values
		return null;
	}

	@Override
	public void persistResults(Class<? extends PersistableCalculation> clazz, Map<String, Object> parameterValues, CalculationResultMap resultMap, PatientCalculationContext context) throws InstantiationException, IllegalAccessException {
		if (resultMap == null)
			return;

		for (Integer patientId : resultMap.keySet()) {
			persistResult(clazz, patientId, parameterValues, resultMap.get(patientId), context);
		}
	}

	@Override
	public PersistedPatientResultMap loadResults(Class<? extends PersistableCalculation> clazz, Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) throws InstantiationException, IllegalAccessException {
		Date dateNow = context.getNow();
		List<PersistedPatientResult> results = persistedPatientResultDAO.loadResults(clazz, cohort, serializeParameterValues(parameterValues), dateNow);
		PersistedPatientResultMap resultMap = new PersistedPatientResultMap();
		for (PersistedPatientResult result : results) {
			resultMap.put(result.getPatientId(), result);
		}
		return resultMap;
	}
}
