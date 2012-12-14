package org.openmrs.calculation.db;

import org.openmrs.calculation.patient.PersistableCalculation;
import org.openmrs.calculation.result.PersistedPatientResult;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Provides methods for persisting calculation results
 */
public interface PersistedPatientResultDAO {

	public void saveResult(PersistedPatientResult patientResult);

	List<PersistedPatientResult> loadResults(Class<? extends PersistableCalculation> clazz,
	                                         Collection<Integer> cohort, String serializedParameterValues, Date dateNow);

}
