package org.openmrs.calculation.patient;

import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;

import java.util.Collection;
import java.util.Map;

/**
 * Facilitates persistence of CalculationResults for PatientCalculations
 */
public interface ExperimentalService {

	/**
	 * Provides a mechanism for persisting PatientCalculation results
	 *
	 * @param clazz           the class to which the {@link org.openmrs.calculation.result.CalculationResult} belongs
	 * @param patientId       the {@link org.openmrs.Patient} whose result is to be stored
	 * @param parameterValues a map of {@link org.openmrs.calculation.parameter.ParameterDefinition} keys and actual values previously used by the calculation
	 * @param context         the {@link PatientCalculationContext} for when this result was calculated
	 * @param result          The {@link org.openmrs.calculation.result.CalculationResult} belonging to the referenced patient
	 * @should save a result
	 */
	public void persistResult(Class<SimplePersistablePatientCalculation> clazz, Integer patientId, Map<String, Object> parameterValues, CalculationResult result, PatientCalculationContext context) throws InstantiationException, IllegalAccessException;

	/**
	 * @see #persistResult(Class, Integer, java.util.Map, org.openmrs.calculation.result.CalculationResult, PatientCalculationContext)
	 */
	public void persistResults(Class<SimplePersistablePatientCalculation> clazz, Map<String, Object> parameterValues, CalculationResultMap resultMap, PatientCalculationContext context) throws InstantiationException, IllegalAccessException;

	/**
	 * Provides a mechanism for accessing persisted PatientCalculation results
	 *
	 * @param clazz           the class to which the desired {@link CalculationResult} belongs
	 * @param patientId       the patientId whose result is sought
	 * @param parameterValues a map of {@link org.openmrs.calculation.parameter.ParameterDefinition} keys and actual values previously used by the calculation
	 * @param context         the {@link PatientCalculationContext} for when this result was calculated
	 * @return the matching hydrated {@link CalculationResult}
	 * @should load a result
	 */
	CalculationResult loadResult(Class<SimplePersistablePatientCalculation> clazz, Integer patientId, Map<String, Object> parameterValues, PatientCalculationContext context) throws InstantiationException, IllegalAccessException;

	/**
	 * @see #loadResult(Class, Integer, java.util.Map, PatientCalculationContext)
	 */
	CalculationResultMap loadResults(Class<SimplePersistablePatientCalculation> clazz, Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context);

}
