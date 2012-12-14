package org.openmrs.calculation.patient;

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.api.ExperimentalService;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.PersistedPatientResult;
import org.openmrs.calculation.result.PersistedPatientResultMap;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Patient-oriented calculation requiring methods for persisting results through serialization.
 */
public abstract class PersistableCalculation implements PatientCalculation {

	private ExperimentalService experimentalService;

	/**
	 * TODO see if this can be injected somehow ... don't expect @Autowired to work for calculations
	 */
	public PersistableCalculation() {
		experimentalService = Context.getService(ExperimentalService.class);
	}

	public void setExperimentalService(ExperimentalService experimentalService) {
		this.experimentalService = experimentalService;
	}

	/**
	 * Serialize a CalculationResult into a String.
	 *
	 *
	 * @param calculationResult a hydrated version of the {@link org.openmrs.calculation.result.CalculationResult}
	 * @return the persisted version of the {@link CalculationResult}
	 */
	public abstract String serialize(CalculationResult calculationResult);

	/**
	 * Deserialize a {@link CalculationResult} from a {@link PersistedPatientResult}
	 *
	 *
	 * @param persistedPatientResult the persisted version of the {@link org.openmrs.calculation.result.CalculationResult}
	 * @return a hydrated version of the {@link CalculationResult}
	 */
	public abstract CalculationResult deserialize(String persistedPatientResult);

	/**
	 * selects a list of patientIds from the provided {@link PersistedPatientResultMap} whose results should be
	 * recalculated, due to expiration, missing data (result is null) or some other reason.
	 *
 	 * @param persistedPatientResultMap
	 * @return set of patientIds that require recalculation
	 */
	protected abstract Set<Integer> determineWhoNeedsRecalculation(PersistedPatientResultMap persistedPatientResultMap);

	/**
	 * this replaces the original evaluateSinglePatient() method for performing calculations only when necessary
	 *
	 * @see PatientCalculation#evaluate(java.util.Collection, java.util.Map, PatientCalculationContext)
	 */
	public abstract CalculationResultMap doEvaluate(Collection<Integer> cohort, Map parameters, PatientCalculationContext context);

	/**
	 * provides how long a persisted instance of a result from this calculation should remain valid.  Default is null,
	 * indicating a result should never expire.
	 *
	 * @return the amount of time to retain a persisted result for this class, in seconds
	 */
	protected Integer getMaxAllowableAgeInSeconds() {
		return null;
	}

	/**
	 * determines if a {@link PersistedPatientResult} is expired by checking the age of the record against the allowed
	 * age.
	 *
	 * @param persistedPatientResult the {@link PersistedPatientResult} to be evaluated
	 * @return whether or not this result is expired
	 */
	protected boolean isExpiredResult(PersistedPatientResult persistedPatientResult) {
		if (getMaxAllowableAgeInSeconds() == null)
			return false;

		if (persistedPatientResult == null)
			return true;

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, -1 * getMaxAllowableAgeInSeconds());
		return calendar.before(persistedPatientResult.getDateUpdated());
	}

	/**
	 * @see PatientCalculation#evaluate(java.util.Collection, java.util.Map, PatientCalculationContext)
	 *
	 * TODO see if we can keep this from being overridden in subclasses
	 */
	public CalculationResultMap evaluate(Set<Integer> cohort, Map parameters, PatientCalculationContext context) {
		// get all persisted results
		PersistedPatientResultMap persistedResults = null;
		try {
			persistedResults = experimentalService.loadResults(getClass(), cohort, parameters, context);
		} catch (InstantiationException e) {
			throw new APIException("Could not load results for object " + this, e);
		} catch (IllegalAccessException e) {
			throw new APIException("Could not load results for object "+ this, e);
		}

		// determine which results should be recalculated
		Set<Integer> needRecalculation = determineWhoNeedsRecalculation(persistedResults);

		// add those patients that did not have persisted results
		for (Integer patientId : cohort) {
			if (!persistedResults.containsKey(patientId))
				needRecalculation.add(patientId);
		}

		// recalculate those that need it
		CalculationResultMap newResults = null;
		if (needRecalculation.size() > 0) {
			newResults = this.doEvaluate(needRecalculation, parameters, context);
			try {
				experimentalService.persistResults(getClass(), parameters, newResults, context);
			} catch (InstantiationException e) {
				throw new APIException("Could not persist results for object " + this, e);
			} catch (IllegalAccessException e) {
				throw new APIException("Could not persist results for object " + this, e);
			}
		}

		if (newResults == null) {
			newResults = new CalculationResultMap();
		}

		// combine old with new
		for (Integer patientId : persistedResults.keySet()) {
			if (!newResults.containsKey(patientId)) {
				newResults.put(patientId, deserialize(persistedResults.get(patientId).getResult()));
			}
		}

		return newResults;
	}
}