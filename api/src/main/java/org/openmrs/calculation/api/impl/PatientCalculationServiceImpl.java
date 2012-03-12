/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.calculation.api.impl;

import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.calculation.MissingParameterException;
import org.openmrs.calculation.PatientCalculation;
import org.openmrs.calculation.api.CalculationContext;
import org.openmrs.calculation.api.PatientCalculationService;
import org.openmrs.calculation.definition.ParameterDefinition;
import org.openmrs.calculation.definition.ParameterDefinitionSet;
import org.openmrs.calculation.evaluator.CalculationEvaluator;
import org.openmrs.calculation.result.CohortResult;
import org.openmrs.calculation.result.EmptyResult;
import org.openmrs.calculation.result.Result;
import org.openmrs.calculation.util.CalculationUtil;
import org.openmrs.util.HandlerUtil;

/**
 * It is a default implementation of {@link PatientCalculationService}.
 */
public class PatientCalculationServiceImpl extends BaseOpenmrsService implements PatientCalculationService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.calculation.api.PatientCalculationService#createCalculationContext()
	 */
	@Override
	public CalculationContext createCalculationContext() {
		return new SimpleCalculationContext();
	}
	
	/**
	 * @see org.openmrs.calculation.api.PatientCalculationService#getCalculation(java.lang.String)
	 */
	@Override
	public PatientCalculation getCalculation(String tokenName) throws APIException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @see org.openmrs.calculation.api.PatientCalculationService#evaluate(java.lang.Integer,
	 *      org.openmrs.calculation.PatientCalculation)
	 */
	@Override
	public Result evaluate(Integer patientId, PatientCalculation calculation) throws APIException {
		return evaluate(patientId, calculation, null);
	}
	
	/**
	 * @see org.openmrs.calculation.api.PatientCalculationService#evaluate(java.lang.Integer,
	 *      org.openmrs.calculation.PatientCalculation,
	 *      org.openmrs.calculation.api.CalculationContext)
	 */
	@Override
	public Result evaluate(Integer patientId, PatientCalculation calculation, CalculationContext context)
	    throws APIException {
		return evaluate(patientId, calculation, null, context);
	}
	
	/**
	 * @see org.openmrs.calculation.api.PatientCalculationService#evaluate(java.lang.Integer,
	 *      org.openmrs.calculation.PatientCalculation, java.util.Map,
	 *      org.openmrs.calculation.api.CalculationContext)
	 */
	@Override
	public Result evaluate(Integer patientId, PatientCalculation calculation, Map<String, Object> parameterValues,
	                       CalculationContext context) throws APIException {
		Cohort cohort = new Cohort(patientId);
		cohort.addMember(patientId);
		CohortResult cr = evaluate(cohort, calculation, parameterValues, context);
		if (cr == null || cr.size() == 0)
			return new EmptyResult();
		
		return cr.get(patientId);
	}
	
	/**
	 * @see org.openmrs.calculation.api.PatientCalculationService#evaluate(org.openmrs.Cohort,
	 *      org.openmrs.calculation.PatientCalculation)
	 */
	@Override
	public CohortResult evaluate(Cohort cohort, PatientCalculation calculation) throws APIException {
		return evaluate(cohort, calculation, null);
	}
	
	/**
	 * @see org.openmrs.calculation.api.PatientCalculationService#evaluate(org.openmrs.Cohort,
	 *      org.openmrs.calculation.PatientCalculation,
	 *      org.openmrs.calculation.api.CalculationContext)
	 */
	@Override
	public CohortResult evaluate(Cohort cohort, PatientCalculation calculation, CalculationContext context)
	    throws APIException {
		return evaluate(cohort, calculation, null, context);
	}
	
	/**
	 * @see org.openmrs.calculation.api.PatientCalculationService#evaluate(org.openmrs.Cohort,
	 *      org.openmrs.calculation.PatientCalculation, java.util.Map,
	 *      org.openmrs.calculation.api.CalculationContext)
	 */
	@Override
	public CohortResult evaluate(Cohort cohort, PatientCalculation calculation, Map<String, Object> parameterValues,
	                             CalculationContext context) throws APIException {
		if (calculation == null)
			throw new IllegalArgumentException("Calculation cannot be null");
		ParameterDefinitionSet defs = calculation.getParameterDefinitionSet();
		//Check for missing of values for required parameters
		if (defs != null) {
			for (ParameterDefinition parameter : calculation.getParameterDefinitionSet()) {
				if (parameter.isRequired()) {
					boolean foundMissingValue = false;
					if (parameterValues == null) {
						foundMissingValue = true;
					} else {
						Object value = parameterValues.get(parameter.getKey());
						String datatype = parameter.getDatatype();
						//the shouldn't be blank if the datatype is String or a primitive wrapper class
						if (value == null) {
							foundMissingValue = true;
						} else if ((CalculationUtil.isPrimitiveWrapperClassName(datatype) || String.class.getName().equals(
						    datatype))
						        && StringUtils.isBlank(value.toString())) {
							foundMissingValue = true;
						}
					}
					
					if (foundMissingValue)
						throw new MissingParameterException(parameter);
				}
			}
		}
		
		if (context == null)
			context = createCalculationContext();
		
		CohortResult cr = HandlerUtil.getPreferredHandler(CalculationEvaluator.class, calculation.getClass()).evaluate(
		    cohort, calculation, parameterValues, context);
		
		return cr;
	}
	
	/**
	 * Base class for {@link CalculationContext}s
	 */
	public class SimpleCalculationContext implements CalculationContext {
		
		private Date now = null;
		
		private Map<String, Object> contextCache = new WeakHashMap<String, Object>();
		
		/**
		 * @see org.openmrs.calculation.api.CalculationContext#getNow()
		 */
		@Override
		public Date getNow() {
			return now;
		}
		
		/**
		 * @see org.openmrs.calculation.api.CalculationContext#setNow(java.util.Date)
		 */
		@Override
		public void setNow(Date date) {
			now = date;
		}
		
		/**
		 * @see org.openmrs.calculation.api.CalculationContext#addToCache(java.lang.String,
		 *      java.lang.Object)
		 */
		@Override
		public void addToCache(String key, Object value) {
			contextCache.put(key, value);
		}
		
		/**
		 * @see org.openmrs.calculation.api.CalculationContext#getFromCache(java.lang.String)
		 */
		@Override
		public Object getFromCache(String key) {
			return contextCache.get(key);
		}
		
		/**
		 * @see org.openmrs.calculation.api.CalculationContext#getFromCache(org.openmrs.Cohort,
		 *      org.openmrs.calculation.PatientCalculation)
		 */
		@Override
		public CohortResult getFromCache(Cohort cohort, PatientCalculation calculation) {
			//TODO Add implementation code
			throw null;
		}
		
		/**
		 * @see org.openmrs.calculation.api.CalculationContext#removeFromCache(java.lang.String)
		 */
		@Override
		public void removeFromCache(String key) {
			contextCache.remove(key);
		}
	}
}