package com.fousalert.bean;

import java.util.Date;

public class F1Ticker {

	private Long tickerId;
	private String tickerCode;
	private Double price;
	private Date tradeDate;
	
	public F1Ticker(){}
	public F1Ticker (String symbolCode , Double price){
		this.tickerCode = symbolCode;
		this.price = price;
	}
	
	public F1Ticker (Long tickerId , Date tradeDate){
		this.tickerId = tickerId;
		this.tradeDate = tradeDate;
	}
	
	public F1Ticker (Long tickerId , Double price){
		this.tickerId = tickerId;
		this.price = price;
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
	
	
	
	
}
