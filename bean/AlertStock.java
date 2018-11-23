package com.fousalert.bean;

import java.util.Date;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class AlertStock extends F1Stock {

	private SimpleDoubleProperty triggerPrice;
	private SimpleLongProperty expireTime;
	private SimpleStringProperty expireTimeString;
	private SimpleStringProperty alertStatus;

	public AlertStock(SimpleStringProperty symbol, SimpleDoubleProperty triggerPrice, SimpleLongProperty expireTime,
			SimpleLongProperty volume, SimpleStringProperty alertStatus, SimpleStringProperty algorithm, SimpleDoubleProperty lastPrice) {
		super(symbol, alertStatus, triggerPrice, algorithm, volume, lastPrice);
		
		this.triggerPrice = triggerPrice;
		this.setExpireTime(expireTime);
		this.alertStatus = alertStatus;
	}

	public SimpleDoubleProperty triggerPriceProperty() {
		return triggerPrice;
	}

	public void setTriggerPrice(SimpleDoubleProperty triggerPrice) {
		this.triggerPrice = triggerPrice;
	}

	public SimpleLongProperty expireTimeProperty() {
		return expireTime;
	}

	public void setExpireTime(SimpleLongProperty expireTime) {
		this.expireTime = expireTime;
		if(expireTime != null) {
			this.expireTimeString = new SimpleStringProperty(new Date(expireTime.get()).toString());
		}
	}

	public SimpleStringProperty alertStatusProperty() {
		return alertStatus;
	}

	public void setAlertStatus(SimpleStringProperty alertStatus) {
		this.alertStatus = alertStatus;
	}

	public SimpleStringProperty getExpireTimeString() {
		return expireTimeString;
	}

	public void setExpireTimeString(SimpleStringProperty expireTimeString) {
		this.expireTimeString = expireTimeString;
	}
}