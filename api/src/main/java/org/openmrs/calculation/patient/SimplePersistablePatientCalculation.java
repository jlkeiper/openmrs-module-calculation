package org.openmrs.calculation.patient;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.parameter.ParameterDefinitionSet;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;

import java.util.Collection;
import java.util.Map;

/**
 * Simple patient-oriented calculation with methods for persisting data through serialization.
 */
public class SimplePersistablePatientCalculation implements PatientCalculation {

	public CalculationResult evaluate(Integer patientId, Map<String, Object> parameterValues, PatientCalculationContext context) {
		Patient patient = Context.getPatientService().getPatient(patientId);
		if (patient == null)
			return null;
		return new SimpleResult(patient.getPersonName().toString(), this);
	}

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
		CalculationResultMap results = new CalculationResultMap();
		for (Integer patientId : cohort) {
			results.put(patientId, this.evaluate(patientId, parameterValues, context));
		}
		return results;
	}

	@Override
	public ParameterDefinitionSet getParameterDefinitionSet() {
		return null;
	}

	/**
	 * Serialize a CalculationResult into a String.
	 *
	 * @param result the CalculationResult object to be serialized
	 * @return a serialized version of the CalculationResult
	 */
	public String serialize(CalculationResult result) {
		return result.toString();
	}

	/**
	 * Deserialize a CalculationResult from a String
	 *
	 * @param serializedResult the previously serialized CalculationResult
	 * @return a hydrated version of the CalculationResult
	 */
	public CalculationResult deserialize(String serializedResult) {
		return new SimpleResult(serializedResult, this);
	}

}