package org.openmrs.calculation.api;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PersistableCalculation;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.PersistedPatientResultMap;

import java.util.Collection;
import java.util.Map;

/**
 * This service contains experimental functionality that may eventually migrate into this module's supported public API,
 * or into dependent modules. Methods in this service are liable to change without warning (and no deprecation!) even
 * between minor release versions of the calculation module.
 */
public interface ExperimentalService {

	/**
	 * Provides a mechanism for persisting PatientCalculation results
	 *
	 *
	 * @param clazz           the class to which the {@link org.openmrs.calculation.result.CalculationResult} belongs
	 * @param patientId       the {@link org.openmrs.Patient} whose result is to be stored
	 * @param parameterValues a map of {@link org.openmrs.calculation.parameter.ParameterDefinition} keys and actual values previously used by the calculation
	 * @param result          The {@link org.openmrs.calculation.result.CalculationResult} belonging to the referenced patient
	 * @param context         the {@link org.openmrs.calculation.patient.PatientCalculationContext} for when this result was calculated
	 * @should save a result
	 */
	public void persistResult(Class<? extends PersistableCalculation> clazz, Integer patientId, Map<String, Object> parameterValues, CalculationResult result, PatientCalculationContext context) throws InstantiationException, IllegalAccessException;

	/**
	 * @see #persistResult(Class, Integer, java.util.Map, org.openmrs.calculation.result.CalculationResult, PatientCalculationContext)
	 */
	public void persistResults(Class<? extends PersistableCalculation> clazz, Map<String, Object> parameterValues, CalculationResultMap resultMap, PatientCalculationContext context) throws InstantiationException, IllegalAccessException;

	/**
	 * Provides a mechanism for accessing persisted PatientCalculation results
	 *
	 *
	 *
	 * @param clazz           the class to which the desired {@link org.openmrs.calculation.result.CalculationResult} belongs
	 * @param cohort          the list of patientIds whose result is sought
	 * @param parameterValues a map of {@link org.openmrs.calculation.parameter.ParameterDefinition} keys and actual values previously used by the calculation
	 * @param context         the {@link org.openmrs.calculation.patient.PatientCalculationContext} for when this result was calculated
	 * @return a {@link PersistedPatientResultMap} containing persisted serialized results for the given parameters
	 */
	PersistedPatientResultMap loadResults(Class<? extends PersistableCalculation> clazz, Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) throws InstantiationException, IllegalAccessException;

}
