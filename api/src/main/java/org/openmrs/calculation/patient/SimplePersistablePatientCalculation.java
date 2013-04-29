package org.openmrs.calculation.patient;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.parameter.ParameterDefinitionSet;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.PersistedPatientResultMap;
import org.openmrs.calculation.result.SimpleResult;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Simple implementation of a persistable patient calculation.  Result is simply the patient's preferred name as a String.
 */
public class SimplePersistablePatientCalculation extends PersistableCalculation {

	/**
	 * Evaluates a single patient and returns the patient's preferred name.
	 *
	 * @param patientId       patient to be evaluated
	 * @param parameterValues parameters that might affect the evaluation
	 * @param context         {@link PatientCalculationContext} used to run this calculation
	 * @return a {@link SimpleResult} based on the patient's name
	 */
	public CalculationResult evaluateSinglePatient(Integer patientId, Map<String, Object> parameterValues, PatientCalculationContext context) {
		Patient patient = Context.getPatientService().getPatient(patientId);
		if (patient == null)
			return null;
		return new SimpleResult(patient.getPersonName().toString(), this);
	}

	/**
	 * @see PatientCalculation#evaluate(java.util.Collection, java.util.Map, PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
		return doEvaluate(cohort, parameterValues, context);
	}

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#getParameterDefinitionSet()
	 */
	@Override
	public ParameterDefinitionSet getParameterDefinitionSet() {
		return null;
	}

	/**
	 * @see PersistableCalculation#serialize(org.openmrs.calculation.result.CalculationResult)
	 */
	public String serialize(CalculationResult calculationResult) {
		return calculationResult.toString();
	}

	/**
	 * @see PersistableCalculation#deserialize(String)
	 * @param persistedPatientResult
	 */
	public CalculationResult deserialize(String persistedPatientResult) {
		return new SimpleResult(persistedPatientResult, this);
	}

	@Override
	protected Set<Integer> determineWhoNeedsRecalculation(PersistedPatientResultMap persistedPatientResultMap) {
		return null;
	}

	@Override
	public CalculationResultMap doEvaluate(Collection<Integer> cohort, Map parameterValues, PatientCalculationContext context) {
		CalculationResultMap results = new CalculationResultMap();
		for (Integer patientId : cohort) {
			results.put(patientId, this.evaluateSinglePatient(patientId, parameterValues, context));
		}
		return results;
	}

}