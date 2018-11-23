package com.fousalert.bean;

public class IntervalComboBoxBean {
	
	public IntervalComboBoxBean() {
		
	}
	
	public IntervalComboBoxBean(String label, String interval) {
		this.label = label;
		this.interval = interval;
	}
	
	private String label;
	private String interval;

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getInterval() {
		return interval;
	}
	public void setInterval(String interval) {
		this.interval = interval;
	}
	
	
	public String toString() {
		return label;
	}
}
