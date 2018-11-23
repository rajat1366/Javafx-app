package com.fousalert.bean;


public class TrendLineRequest {
	private String chartUID;
	private String type;
	private Double x0;
	private Double y0;
	private Double x1;
	private Double y1;
	private String text;
	private Integer trendLineId;
	
	public TrendLineRequest(String chartUID, String type, Double x0, Double y0, Double x1, Double y1, String text,
			Integer trendLineId) {
		super();
		this.chartUID = chartUID;
		this.type = type;
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
		this.text = text;
		this.trendLineId = trendLineId;
	}
	public Integer getTrendLineId() {
		return trendLineId;
	}
	public void setTrendLineId(Integer trendLineId) {
		this.trendLineId = trendLineId;
	}
	public String getChartUID() {
		return chartUID;
	}
	public void setChartUID(String chartUID) {
		this.chartUID = chartUID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Double getX0() {
		return x0;
	}
	public void setX0(Double x0) {
		this.x0 = x0;
	}
	public Double getY0() {
		return y0;
	}
	public void setY0(Double y0) {
		this.y0 = y0;
	}
	public Double getX1() {
		return x1;
	}
	public void setX1(Double x1) {
		this.x1 = x1;
	}
	public Double getY1() {
		return y1;
	}
	public void setY1(Double y1) {
		this.y1 = y1;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
	
}
