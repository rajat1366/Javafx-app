package com.fousalert.bean;

public class RemoveTrendLine {

	private Integer trendLineId;
	private Integer chartUID;
	
	
	public RemoveTrendLine(Integer trendLineId, Integer chartUID) {
		super();
		this.trendLineId = trendLineId;
		this.chartUID = chartUID;
	}
	public Integer getTrendLineId() {
		return trendLineId;
	}
	public void setTrendLineId(Integer trendLineId) {
		this.trendLineId = trendLineId;
	}
	public Integer getChartUID() {
		return chartUID;
	}
	public void setChartUID(Integer chartUID) {
		this.chartUID = chartUID;
	}
	
}
