package com.fousalert.utils;


public enum AlertStatus {
	NEUTRAL("White"), ACTIONABLE("Green"), ON_WATCH("Orange");
	
	private  String displayStatus;
	
	private AlertStatus(String displayStatus) {
		this.displayStatus = displayStatus;
	}
	
	public String getDisplayStatus() {
		return displayStatus;
	}

	public int getValue() {
		return this.ordinal();
	}
	
	public static AlertStatus getByValue(int value) {
		return AlertStatus.values()[value];
	}
	
	public String getLabel() {
	   return this.toString();
	}
}