package com.fousalert.bean;

import com.fousalert.utils.Constants;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class F1Stock extends Stock {

	private SimpleStringProperty status;
	private SimpleStringProperty hodTrigger;
	private SimpleDoubleProperty trigger;
	private SimpleStringProperty triggerString = new SimpleStringProperty();
	private SimpleStringProperty source;
	private SimpleLongProperty volume;

	public F1Stock(SimpleStringProperty symbol, SimpleDoubleProperty lastPrice, SimpleStringProperty status, SimpleStringProperty hodTrigger, SimpleDoubleProperty trigger) {
		super(symbol, lastPrice, null, null, null);
		this.status = status;
		this.hodTrigger = hodTrigger;
		
		setTrigger(trigger);
	}
	
	public F1Stock(SimpleStringProperty symbol, SimpleStringProperty status, SimpleDoubleProperty trigger, SimpleStringProperty algorithm, SimpleLongProperty volume, SimpleDoubleProperty lastPrice) {
		super(symbol, null, null, null, algorithm, lastPrice);
		this.hodTrigger = new SimpleStringProperty(Constants.EMPTY_STRING);
		this.status = status;
		this.volume = volume;
		setTrigger(trigger);
	}

	public F1Stock(SimpleStringProperty symbol, SimpleDoubleProperty lastPrice, 
			SimpleIntegerProperty lastSize, SimpleLongProperty lastDateTime,SimpleDoubleProperty change,
			SimpleStringProperty status, SimpleStringProperty hodTrigger, SimpleDoubleProperty trigger) {
		super(symbol, lastPrice, lastSize, lastDateTime, change);
		
		this.status = status;
		this.hodTrigger = hodTrigger;
		
		setTrigger(trigger);
	}
	
	public F1Stock(SimpleStringProperty symbol,	SimpleDoubleProperty lastPrice,
			SimpleDoubleProperty trigger, SimpleLongProperty lastDateTime, SimpleStringProperty source) {
		super(symbol, lastPrice, null, lastDateTime, null);
		
		this.trigger = trigger;
		this.source = source;
	}

	public SimpleStringProperty sourceProperty() {
		return source;
	}
	public void setSource(SimpleStringProperty source) {
		this.source = source;
	}

	public SimpleStringProperty statusProperty() {
		return status;
	}
	public void setStatus(SimpleStringProperty status) {
		this.status = status;
	}

	public SimpleStringProperty hodTriggerProperty() {
		return hodTrigger;
	}
	public void setHodTrigger(SimpleStringProperty hodTrigger) {
		this.hodTrigger = hodTrigger;
	}

	public SimpleDoubleProperty triggerProperty() {
		return trigger;
	}
	public void setTrigger(SimpleDoubleProperty trigger) {
		this.trigger = trigger;
		if(trigger != null) {
			this.triggerString = new SimpleStringProperty(String.valueOf(trigger.get()));
		}
	}

	public SimpleStringProperty triggerStringProperty() {
		return triggerString;
	}
	public void setTriggerString(SimpleStringProperty triggerString) {
		this.triggerString = triggerString;
	}

	public SimpleLongProperty volumeProperty() {
		return volume;
	}

	public void setVolume(SimpleLongProperty volume) {
		this.volume = volume;
	}
}
