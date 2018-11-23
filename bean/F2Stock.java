package com.fousalert.bean;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class F2Stock extends F1Stock {

	private SimpleStringProperty nearString;
	private SimpleStringProperty aboveString;
	private SimpleDoubleProperty near;
	private SimpleDoubleProperty above;


	public F2Stock(SimpleStringProperty symbol, SimpleDoubleProperty lastPrice, 
			SimpleIntegerProperty lastSize, SimpleLongProperty lastDateTime,SimpleDoubleProperty change,
			SimpleStringProperty status, SimpleStringProperty hodTrigger, SimpleDoubleProperty trigger, SimpleDoubleProperty near, SimpleDoubleProperty above) {
		super(symbol, lastPrice, lastSize, lastDateTime, change, status, hodTrigger, trigger);

		setNear(near);
		setAbove(above);
	}


	public SimpleStringProperty nearStringProperty() {
		return nearString;
	}
	public void setNearString(SimpleStringProperty nearString) {
		this.nearString = nearString;
	}

	public SimpleStringProperty aboveStringProperty() {
		return aboveString;
	}
	public void setAboveString(SimpleStringProperty aboveString) {
		this.aboveString = aboveString;
	}

	public SimpleDoubleProperty nearProperty() {
		return near;
	}
	public void setNear(SimpleDoubleProperty near) {
		this.near = near;
		if(this.near != null) {
			this.nearString = new SimpleStringProperty(String.valueOf(this.near.get()));			
		}
	}

	public SimpleDoubleProperty aboveProperty() {
		return above;
	}
	public void setAbove(SimpleDoubleProperty above) {
		this.above = above;
		if(this.above != null) {
			this.aboveString = new SimpleStringProperty(String.valueOf(this.above.get()));			
		}
	}
}
