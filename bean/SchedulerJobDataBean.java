package com.fousalert.bean;

import java.util.Map;

public class SchedulerJobDataBean {
	
	private Map<String, SocketDataCacheObject> tickerDataMapIntra = null;
	private String splitDuration;
	private String tickerSymbol;
	
	public SchedulerJobDataBean() {
	}
	
	public SchedulerJobDataBean(Map<String, SocketDataCacheObject> tickerDataMapIntra, String splitDuration, String tickerSymbol) {
		this.tickerDataMapIntra = tickerDataMapIntra;
		this.splitDuration = splitDuration;
		this.tickerSymbol = tickerSymbol;
	}
	
	public Map<String, SocketDataCacheObject> getTickerDataMapIntra() {
		return tickerDataMapIntra;
	}
	public void setTickerDataMapIntra(Map<String, SocketDataCacheObject> tickerDataMapIntra) {
		this.tickerDataMapIntra = tickerDataMapIntra;
	}
	
	public String getSplitDuration() {
		return splitDuration;
	}
	public void setSplitDuration(String splitDuration) {
		this.splitDuration = splitDuration;
	}
	
	public String getTickerSymbol() {
		return tickerSymbol;
	}
	public void setTickerSymbol(String tickerSymbol) {
		this.tickerSymbol = tickerSymbol;
	}
}
