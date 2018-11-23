package com.fousalert.bean;

import java.util.Date;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import com.fousalert.utils.Constants;
import com.fousalert.utils.DateUtil;
import com.fousalert.utils.NumberUtil;

public class Stock {

	private SimpleStringProperty symbol;
	private SimpleIntegerProperty lastSize;
	private SimpleDoubleProperty lastPrice;
	private SimpleLongProperty lastDateTime;
	private SimpleDoubleProperty change;
	private SimpleStringProperty lastDateTimeString;
	private SimpleStringProperty algorithm;
	private SimpleStringProperty gain;
	
	public Stock(SimpleStringProperty symbol, SimpleDoubleProperty lastPrice, SimpleIntegerProperty lastSize, SimpleLongProperty lastDateTime,SimpleDoubleProperty change) {
		this.symbol = symbol;
		this.lastSize = lastSize;
		this.lastPrice = lastPrice;
		this.lastDateTime = lastDateTime;
		this.change= change;
		if(lastPrice != null) {
			this.lastPrice = new SimpleDoubleProperty(NumberUtil.truncateDecimalPointsTo2(lastPrice.get()));
		}
		if(lastDateTime != null) {
			this.lastDateTimeString = new SimpleStringProperty(DateUtil.formatDate(DateUtil.convertToEST(new Date(lastDateTime.get())), "MM-dd-yyyy hh:mm:ss"));
		}
	}
	
	public Stock(SimpleStringProperty symbol, SimpleIntegerProperty lastSize, SimpleLongProperty lastDateTime,SimpleDoubleProperty change) {
		this.symbol = symbol;
		this.lastSize = lastSize;
		this.lastDateTime = lastDateTime;
		this.change= change;
		if(lastDateTime != null) {
			this.lastDateTimeString = new SimpleStringProperty(DateUtil.formatDate(DateUtil.convertToEST(new Date(lastDateTime.get())), "MM-dd-yyyy hh:mm:ss"));
		}
	}
	
	public Stock(SimpleStringProperty symbol, SimpleIntegerProperty lastSize, SimpleLongProperty lastDateTime,SimpleDoubleProperty change, SimpleStringProperty algorithm, SimpleDoubleProperty lastPrice) {
		this(symbol, lastSize, lastDateTime, change);
		this.algorithm = algorithm;
		if(lastPrice != null) {
			this.lastPrice = new SimpleDoubleProperty(NumberUtil.truncateDecimalPointsTo2(lastPrice.get()));
		}
	}
	
	public SimpleStringProperty lastDateTimeStringProperty() {
		return lastDateTimeString;
	}
	public void setLastDateTimeString(SimpleStringProperty lastDateTimeString) {
		this.lastDateTimeString = lastDateTimeString;
	}

	public SimpleStringProperty symbolProperty() {
		return symbol;
	}
	public void setSymbol(SimpleStringProperty symbol) {
		this.symbol = symbol;
	}
	public SimpleIntegerProperty lastSizeProperty() {
		return lastSize;
	}
	public void setLastSize(SimpleIntegerProperty lastSize) {
		this.lastSize = lastSize;
	}
	public SimpleDoubleProperty lastPriceProperty() {
		return lastPrice;
	}
	public void setLastPrice(SimpleDoubleProperty lastPrice) {
		if(lastPrice != null) {
			this.lastPrice = new SimpleDoubleProperty(NumberUtil.truncateDecimalPointsTo2(lastPrice.get()));			
		}
	}
	public SimpleLongProperty lastDateTimeProperty() {
		return lastDateTime;
	}
	public void setLastDateTime(SimpleLongProperty lastDateTime) {
		this.lastDateTime = lastDateTime;
		if(lastDateTime != null) {
			this.lastDateTimeString = new SimpleStringProperty(DateUtil.formatDate(DateUtil.convertToEST(new Date(lastDateTime.get())), Constants.DATE_TIME_FORMAT));
		}
	}
	public SimpleDoubleProperty changeProperty() {
		return change;
	}
	public void setChange(SimpleDoubleProperty change) {
		this.change = change;
	}

	public SimpleStringProperty algorithmProperty() {
		return algorithm;
	}

	public void setAlgorithm(SimpleStringProperty algorithm) {
		this.algorithm = algorithm;
	}

	public SimpleStringProperty gainProperty() {
		return gain;
	}

	public void setGain(SimpleStringProperty gain) {
		this.gain = gain;
	}
	
	
}
