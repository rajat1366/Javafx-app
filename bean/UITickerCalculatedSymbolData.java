package com.fousalert.bean;

import com.fousalert.calculationEngine.algo.indicator.UITickerCalculatedData;

public class UITickerCalculatedSymbolData extends UITickerCalculatedData {
	
	private String tickerSymbol;
	private Long date;
	private String algorithm;
	private String chartIndex;
	private int chartUID;
	
	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public String getTickerSymbol() {
		return tickerSymbol;
	}

	public void setTickerSymbol(String tickerSymbol) {
		this.tickerSymbol = tickerSymbol;
	}

	public String getChartIndex() {
		return chartIndex;
	}

	public void setChartIndex(String chartIndex) {
		this.chartIndex = chartIndex;
	}

	public int getChartUID() {
		return chartUID;
	}

	public void setChartUID(int chartUID) {
		this.chartUID = chartUID;
	}

	
}
