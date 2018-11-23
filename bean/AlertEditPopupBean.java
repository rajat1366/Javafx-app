package com.fousalert.bean;

public class AlertEditPopupBean {

	private String snoozeTime;
	private boolean isAlertDisabled = false;

	public AlertEditPopupBean(String snoozeTime, boolean isAlertDisabled) {
		super();
		this.snoozeTime = snoozeTime;
		this.isAlertDisabled = isAlertDisabled;
	}

	public String getSnoozeTime() {
		return snoozeTime;
	}
	public void setSnoozeTime(String snoozeTime) {
		this.snoozeTime = snoozeTime;
	}
	public boolean isAlertDisabled() {
		return isAlertDisabled;
	}
	public void setAlertDisabled(boolean isAlertDisabled) {
		this.isAlertDisabled = isAlertDisabled;
	}
}