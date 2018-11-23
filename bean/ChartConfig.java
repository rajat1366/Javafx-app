package com.fousalert.bean;

import java.util.ArrayList;
import java.util.List;

public class ChartConfig {
	
	private String tickerSymbol;
	private int dataDuration;
	private List<IndicatorParamBean> indicatorParams = new ArrayList<IndicatorParamBean>();
	private int chartUID;
	private String groupColor;
	private int apiType;
	private List<TrendLineParamBean> trendlineParams = new ArrayList<TrendLineParamBean>();
	private List<TrendLineParamBean> horizontalTrendlineParams = new ArrayList<TrendLineParamBean>();
	private List<AnnotationParamBean> annotationParams = new ArrayList<AnnotationParamBean>();
	private ChartPreferenceBean chartParams;
	
	public ChartConfig(int chartUID, String tickerSymbol, int dataDuration, List<IndicatorParamBean> indicatorParams, ChartPreferenceBean chartParams, List<TrendLineParamBean> horizontalTrendlineParams) {
		this.tickerSymbol = tickerSymbol;
		this.chartUID = chartUID;
		this.dataDuration = dataDuration;
		this.indicatorParams = indicatorParams;
		this.horizontalTrendlineParams = horizontalTrendlineParams;
	}
	
	public ChartConfig(int chartUID, String tickerSymbol, int dataDuration, List<IndicatorParamBean> indicatorParams,String groupColor, int apiType, List<TrendLineParamBean> trendlineParams, List<AnnotationParamBean>annotationParams, ChartPreferenceBean chartParams, List<TrendLineParamBean> horizontalTrendlineParams) {
		this(chartUID,tickerSymbol,dataDuration,indicatorParams, chartParams, horizontalTrendlineParams);
		this.groupColor = groupColor;
		this.apiType = apiType;
		this.trendlineParams = trendlineParams;
		this.annotationParams = annotationParams;
		this.chartParams = chartParams;
	}
	
	public List<AnnotationParamBean> getAnnotationParams() {
		return annotationParams;
	}
	public void setAnnotationParams(List<AnnotationParamBean> annotationParams) {
		this.annotationParams = annotationParams;
	}
	public List<TrendLineParamBean> getTrendlineParams() {
		return trendlineParams;
	}
	public void setTrendlineParams(List<TrendLineParamBean> trendlineParams) {
		this.trendlineParams = trendlineParams;
	}
	public int getApiType() {
		return apiType;
	}
	public void setApiType(int apiType) {
		this.apiType = apiType;
	}
	public String getGroupColor() {
		return groupColor;
	}
	public void setGroupColor(String groupColor) {
		this.groupColor = groupColor;
	}
	public String getTickerSymbol() {
		return tickerSymbol;
	}
	public void setTickerSymbol(String tickerSymbol) {
		this.tickerSymbol = tickerSymbol;
	}
	public int getDataDuration() {
		return dataDuration;
	}
	public void setDataDuration(int dataDuration) {
		this.dataDuration = dataDuration;
	}
	public List<IndicatorParamBean> getIndicatorParams() {
		return indicatorParams;
	}
	public void setIndicatorParams(List<IndicatorParamBean> indicatorParams) {
		this.indicatorParams = indicatorParams;
	}
	public int getChartUID() {
		return chartUID;
	}
	public void setChartUID(int chartUID) {
		this.chartUID = chartUID;
	}
	public ChartPreferenceBean getChartParams() {
		return chartParams;
	}
	public void setChartParams(ChartPreferenceBean chartParams) {
		this.chartParams = chartParams;
	}
	public List<TrendLineParamBean> getHorizontalTrendlineParams() {
		return horizontalTrendlineParams;
	}
	public void setHorizontalTrendlineParams(List<TrendLineParamBean> horizontalTrendlineParams) {
		this.horizontalTrendlineParams = horizontalTrendlineParams;
	}
}