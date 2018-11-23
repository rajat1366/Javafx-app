package com.fousalert.bean;

import java.util.Date;

public class AlertTicker {

	private Long tickerId;
	private String tickerCode;
	private Double price;
	private Date tradeDate;
	private String algorithm;
	private Boolean isEnabled;
	
	public AlertTicker(){}
	public AlertTicker (String symbolCode , Double price){
		this.tickerCode = symbolCode;
		this.price = price;
	}
	
	public AlertTicker (String symbolCode , Double price, String algorithm){
		this(symbolCode, price);
		this.algorithm = algorithm;
	}
	
	public AlertTicker (Long tickerId , Date tradeDate){
		this.tickerId = tickerId;
		this.tradeDate = tradeDate;
	}
	
	public AlertTicker (Long tickerId , Double price){
		this.tickerId = tickerId;
		this.price = price;
	}

	public String getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	public String getTickerCode() {
		return tickerCode;
	}
	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Long getTickerId() {
		return tickerId;
	}

	public void setTickerId(Long tickerId) {
		this.tickerId = tickerId;
	}

	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	public Boolean getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
}
