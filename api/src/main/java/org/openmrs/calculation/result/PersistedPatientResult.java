package org.openmrs.calculation.result;

import java.util.Date;

/**
 * Simple model for representing a persisted {@link org.openmrs.calculation.result.CalculationResult} result via Hibernate
 */
public class PersistedPatientResult {

	private Integer persistedPatientResultId;
	private Integer patientId;
	private String className;
	private String parameterValues;
	private Date dateNow;
	private Date dateUpdated;
	private String result;

	public Integer getPersistedPatientResultId() {
		return persistedPatientResultId;
	}

	public void setPersistedPatientResultId(Integer persistedPatientResultId) {
		this.persistedPatientResultId = persistedPatientResultId;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(String parameterValues) {
		this.parameterValues = parameterValues;
	}

	public Date getDateNow() {
		return dateNow;
	}

	public void setDateNow(Date dateNow) {
		this.dateNow = dateNow;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
