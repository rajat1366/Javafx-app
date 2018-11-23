package com.fousalert.bean;

public class AlertTrendlineBean {

	private double lowValue;
	private double highValue;
	private String tickerName;
	private boolean isBothCheckEnabled = false;
	private long trendlineId;

	public AlertTrendlineBean(double lowValue, double highValue, String tickerName, boolean isBothCheckEnabled,
			long trendlineId) {
		super();
		this.lowValue = lowValue;
		this.highValue = highValue;
		this.tickerName = tickerName;
		this.isBothCheckEnabled = isBothCheckEnabled;
		this.trendlineId = trendlineId;
	}
	
	public double getLowValue() {
		return lowValue;
	}
	public void setLowValue(double lowValue) {
		this.lowValue = lowValue;
	}
	public double getHighValue() {
		return highValue;
	}
	public void setHighValue(double highValue) {
		this.highValue = highValue;
	}
	public String getTickerName() {
		return tickerName;
	}
	public void setTickerName(String tickerName) {
		this.tickerName = tickerName;
	}
	public boolean isBothCheckEnabled() {
		return isBothCheckEnabled;
	}
	public void setBothCheckEnabled(boolean isBothCheckEnabled) {
		this.isBothCheckEnabled = isBothCheckEnabled;
	}
	public long getTrendlineId() {
		return trendlineId;
	}
	public void setTrendlineId(long trendlineId) {
		this.trendlineId = trendlineId;
	}
}