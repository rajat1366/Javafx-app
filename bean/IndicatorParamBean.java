package com.fousalert.bean;

public class IndicatorParamBean {
	
	private Integer indicatorUID;
	private String ticker;
	private Integer period;
	private String algorithm;
	private String chartIndex;
	private Integer slowPeriod;
	private Integer slowDPeriod;
	private Integer slowKPeriod;
	private Integer fastPeriod;
	private Integer fastKPeriod;
	private Integer signalPeriod;
	private Integer splitDuration;
	
	
	public IndicatorParamBean() {
		
	}
	
	public IndicatorParamBean(int indicatorUID, int period, String algorithm) {
		this.indicatorUID = indicatorUID;
		this.period = period;
		this.algorithm = algorithm;
	}
	
	public IndicatorParamBean(Integer period, Integer fastPeriod, Integer slowPeriod, Integer signalPeriod, Integer slowDPeriod, Integer fastKPeriod, Integer slowKPeriod) {
		this.period = period;
		this.slowPeriod = slowPeriod;
		this.slowDPeriod = slowDPeriod;
		this.fastPeriod = fastPeriod;
		this.signalPeriod = signalPeriod;
		this.fastKPeriod = fastKPeriod;
		this.slowKPeriod = slowKPeriod;
	}
	
	public IndicatorParamBean(Integer indicatorUID, Integer period, Integer fastPeriod, Integer slowPeriod, Integer signalPeriod, Integer fastKPeriod, Integer slowKPeriod, Integer slowDPeriod, String algorithm) {
		this(period, fastPeriod, slowPeriod, signalPeriod, slowDPeriod, fastKPeriod, slowKPeriod);
		this.algorithm = algorithm;
		this.indicatorUID = indicatorUID;
	}
	
	public String getTicker() {
		return ticker;
	}
	public Integer getIndicatorUID() {
		return indicatorUID;
	}

	public void setIndicatorUID(Integer indicatorUID) {
		this.indicatorUID = indicatorUID;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}

	public String getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	public String getChartIndex() {
		return chartIndex;
	}
	public void setChartIndex(String chartIndex) {
		this.chartIndex = chartIndex;
	}
	public Integer getSlowPeriod() {
		return slowPeriod;
	}
	public void setSlowPeriod(Integer slowPeriod) {
		this.slowPeriod = slowPeriod;
	}
	public Integer getSlowDPeriod() {
		return slowDPeriod;
	}
	public void setSlowDPeriod(Integer slowDPeriod) {
		this.slowDPeriod = slowDPeriod;
	}
	public Integer getFastPeriod() {
		return fastPeriod;
	}
	public void setFastPeriod(Integer fastPeriod) {
		this.fastPeriod = fastPeriod;
	}

	public Integer getSignalPeriod() {
		return signalPeriod;
	}

	public void setSignalPeriod(Integer signalPeriod) {
		this.signalPeriod = signalPeriod;
	}

	public Integer getSplitDuration() {
		return splitDuration;
	}

	public void setSplitDuration(Integer splitDuration) {
		this.splitDuration = splitDuration;
	}

	public Integer getSlowKPeriod() {
		return slowKPeriod;
	}

	public void setSlowKPeriod(Integer slowKPeriod) {
		this.slowKPeriod = slowKPeriod;
	}

	public Integer getFastKPeriod() {
		return fastKPeriod;
	}

	public void setFastKPeriod(Integer fastKPeriod) {
		this.fastKPeriod = fastKPeriod;
	}
}
