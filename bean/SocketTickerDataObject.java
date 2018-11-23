package com.fousalert.bean;

import java.util.ArrayList;
import java.util.List;

public class SocketTickerDataObject {
	
	private String ticker;
	private String splitDuration;
	private String apiSerial;
	private int chartUID;
	
	private List<IndicatorParamBean> indicatorParams = new ArrayList<IndicatorParamBean>();
	
	public String getTicker() {
		return ticker;
	}
	
	public String getApiSerial() {
		return apiSerial;
	}

	public void setApiSerial(String apiSerial) {
		this.apiSerial = apiSerial;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getSplitDuration() {
		return splitDuration;
	}

	public void setSplitDuration(String splitDuration) {
		this.splitDuration = splitDuration;
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
}