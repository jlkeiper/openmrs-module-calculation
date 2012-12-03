package org.openmrs.calculation.db;

import org.openmrs.calculation.patient.ExperimentalItem;

/**
 * Provides methods for persisting calculation results
 */
public interface ExperimentalDAO {

	public void saveResult(ExperimentalItem item);

	ExperimentalItem loadResult(ExperimentalItem item);

}
