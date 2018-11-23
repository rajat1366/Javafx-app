package com.fousalert.bean;

import java.util.Date;

/**
 * @author Nikhil
 *
 */
public class AbstractTickerCandlePriceHistory {
	
	private Long tickerId;
	private Double open;
	private Double close;
	private Double high;
	private Double low;
	private long volume;
	private Date tradeDate;
	private long timeStamp;
	private Integer tradeSize;
	private long date;
	private String symbolName;
	private String tickerSymbol;
	private String tickerCode;
	private Integer apiSerial;
	private Integer splitDuration;
	
	public AbstractTickerCandlePriceHistory() {
		
	}
	
	public AbstractTickerCandlePriceHistory(long tickerId, Double open, Double close, Double high, Double low, long volume, Date tradeDate, long timeStamp, Integer tradeSize, long date, String tickerSymbol, String tickerCode, Integer apiSerial, Integer splitDuration, String symbolName) {
		this.tickerId = tickerId;
		this.open = open;
		this.close = close;
		this.high = high;
		this.low = low;
		this.volume = volume;
		this.tradeDate = tradeDate;
		this.timeStamp = timeStamp;
		this.tradeSize = tradeSize;
		this.date = date;
		this.tickerSymbol = tickerSymbol;
		this.tickerCode = tickerCode;
		this.apiSerial = apiSerial;
		this.splitDuration = splitDuration;
		this.symbolName = symbolName;
	}
	
	
	public Integer getApiSerial() {
		return apiSerial;
	}
	public void setApiSerial(Integer apiSerial) {
		this.apiSerial = apiSerial;
	}
	public Integer getSplitDuration() {
		return splitDuration;
	}
	public void setSplitDuration(Integer splitDuration) {
		this.splitDuration = splitDuration;
	}
	public String getTickerSymbol() {
		return symbolName;
	}
	public void setTickerSymbol(String tickerSymbol) {
		this.tickerSymbol = tickerSymbol;
		this.symbolName = tickerSymbol;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public Long getTickerId() {
		return tickerId;
	}
	public void setTickerId(Long tickerId) {
		this.tickerId = tickerId;
	}
	public long getVolume() {
		return volume;
	}
	public void setVolume(long volume) {
		this.volume = volume;
	}

	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public Double getOpen() {
		return open;
	}
	public void setOpen(Double open) {
		this.open = open;
	}
	public Double getClose() {
		return close;
	}
	public void setClose(Double close) {
		this.close = close;
	}
	public Double getHigh() {
		return high;
	}
	public void setHigh(Double high) {
		this.high = high;
	}
	public Double getLow() {
		return low;
	}
	public void setLow(Double low) {
		this.low = low;
	}
	public Integer getTradeSize() {
		return tradeSize;
	}
	public void setTradeSize(Integer tradeSize) {
		this.tradeSize = tradeSize;
	}
	public String getTickerCode() {
		return tickerCode;
	}
	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}
	public Date getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
		if(this.tradeDate != null) {
			this.timeStamp = this.tradeDate.getTime();
		}
	}

	public String getSymbolName() {
		return symbolName;
	}

	public void setSymbolName(String symbolName) {
		this.tickerSymbol = symbolName;
		this.symbolName = symbolName;
	}
}
