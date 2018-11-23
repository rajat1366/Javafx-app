package com.fousalert.utils;

import org.json.JSONException;
import org.json.JSONObject;

import com.fousalert.bean.AlertStock;
import com.fousalert.bean.Stock;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class JSONToStockMapper {

	public static Stock toWatchStock(String jsonString) throws JSONException {
		return toWatchStock(new JSONObject(jsonString));
	}
	public static Stock toWatchStock(JSONObject jsonObject) throws JSONException {
		Stock stock = new Stock(new SimpleStringProperty(jsonObject.getString("symbolName")), new SimpleDoubleProperty(jsonObject.getDouble("lastPrice")), new SimpleIntegerProperty(jsonObject.getInt("lastSize")), new SimpleLongProperty(jsonObject.getLong("timeStamp")),null);
		return stock; 
	}
	
	public static AlertStock toAlertStock(String jsonString) throws JSONException {
		return toAlertStock(new JSONObject(jsonString));
	}
	public static AlertStock toAlertStock(JSONObject jsonObject) throws JSONException {
		Long expireTimeProperty = (jsonObject.get("expireTime") != JSONObject.NULL)?jsonObject.getLong("expireTime"):null;
		String alertStatus = (jsonObject.get("alertStatus") != JSONObject.NULL)?jsonObject.getString("alertStatus"):null;
		Double triggerPriceProperty = (jsonObject.get("triggerPrice") != JSONObject.NULL) ? NumberUtil.truncateDecimalPointsTo2(jsonObject.getDouble("triggerPrice")): null;
		Double percentagePriceChange = (jsonObject.get("percentagePriceChange") != JSONObject.NULL) ? NumberUtil.truncateDecimalPoints(jsonObject.getDouble("percentagePriceChange"), Constants.DECIMAL_NUMBER_FORMAT_2): null;
		Double price = (jsonObject.get("price") != JSONObject.NULL) ? NumberUtil.truncateDecimalPoints(jsonObject.getDouble("price"), Constants.DECIMAL_NUMBER_FORMAT_2): null;
		Long time = (jsonObject.get("time") != JSONObject.NULL) ? jsonObject.getLong("time"):null;
		Long volume = (jsonObject.get("volume") != JSONObject.NULL) ? jsonObject.getLong("volume"):null;
		
		AlertStock stock = new AlertStock(new SimpleStringProperty(jsonObject.getString("symbolName")), (triggerPriceProperty != null)?new SimpleDoubleProperty(triggerPriceProperty):null, (expireTimeProperty != null)? (new SimpleLongProperty(expireTimeProperty)):null,
				(volume != null) ? new SimpleLongProperty(jsonObject.getLong("volume")):null, (alertStatus != null)?new SimpleStringProperty(alertStatus):null, new SimpleStringProperty(jsonObject.getString("algorithm")),(price != null)?new SimpleDoubleProperty(price):null);
		stock.setChange((percentagePriceChange != null)?new SimpleDoubleProperty(percentagePriceChange):null);
		stock.setLastDateTime((time != null) ? new SimpleLongProperty(jsonObject.getLong("time")):null);
		return stock;
	}
}
