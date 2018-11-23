package com.fousalert.bean;


public class TrendLine {
	private String chartUID;
	private TrendLineParamBean trendlineData;
	
	public String getChartUID() {
		return chartUID;
	}
	public void setChartUID(String chartUID) {
		this.chartUID = chartUID;
	}
	
	public TrendLineParamBean getTrendlineData() {
		return trendlineData;
	}
	public void setTrendlineData(TrendLineParamBean trendlineData) {
		this.trendlineData = trendlineData;
	}
}
