package org.openmrs.calculation.api;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.SimplePersistablePatientCalculation;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.PersistedPatientResultMap;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Test class for {@link org.openmrs.calculation.api.ExperimentalService}
 */
public class ExperimentalServiceTest extends BaseModuleContextSensitiveTest {
	/**
	 * @verifies save a result
	 * @see org.openmrs.calculation.api.ExperimentalService#persistResult(Class, Integer, java.util.Map, org.openmrs.calculation.result.CalculationResult, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void persistResult_shouldSaveAResult() throws Exception {
		ExperimentalService service = Context.getService(ExperimentalService.class);

		PatientCalculationContext context = new TestPatientCalculationContext();
		SimplePersistablePatientCalculation patientCalculation = new SimplePersistablePatientCalculation();
		Map<String, Object> parameterValues = null;

		CalculationResult expected = patientCalculation.evaluateSinglePatient(8, parameterValues, context);

		service.persistResult(SimplePersistablePatientCalculation.class, 8, parameterValues, expected, context);
		Context.flushSession();
		PersistedPatientResultMap actual = service.loadResults(SimplePersistablePatientCalculation.class, Collections.singletonList(8), parameterValues, context);

		Assert.assertEquals(1, actual.size());

		CalculationResult actualResult = patientCalculation.deserialize(actual.get(8).getResult());
		Assert.assertEquals(expected.toString(), actualResult.toString());
	}

	/**
	 * @verifies load a result
	 * @see ExperimentalService#loadResults(Class, java.util.Collection, java.util.Map, PatientCalculationContext)
	 */
	@Test
	@Ignore
	public void loadResults_shouldLoadAResult() throws Exception {
		// @see #saveResult_shouldSaveAResult()
	}

	/**
	 * Base class for {@link PatientCalculationContext}s
	 */
	private class TestPatientCalculationContext implements PatientCalculationContext {

		private Date now = null;

		private Map<String, Object> contextCache = new WeakHashMap<String, Object>();

		public TestPatientCalculationContext() {
			this.now = new Date();
		}

		/**
		 * @see org.openmrs.calculation.patient.PatientCalculationContext#getNow()
		 */
		@Override
		public Date getNow() {
			return now;
		}

		/**
		 * @see org.openmrs.calculation.patient.PatientCalculationContext#setNow(java.util.Date)
		 */
		@Override
		public void setNow(Date date) {
			now = date;
		}

		/**
		 * @see org.openmrs.calculation.patient.PatientCalculationContext#addToCache(java.lang.String,
		 *      java.lang.Object)
		 */
		@Override
		public void addToCache(String key, Object value) {
			contextCache.put(key, value);
		}

		/**
		 * @see org.openmrs.calculation.patient.PatientCalculationContext#getFromCache(java.lang.String)
		 */
		@Override
		public Object getFromCache(String key) {
			return contextCache.get(key);
		}

		/**
		 * @see org.openmrs.calculation.patient.PatientCalculationContext#removeFromCache(java.lang.String)
		 */
		@Override
		public void removeFromCache(String key) {
			contextCache.remove(key);
		}
	}
}