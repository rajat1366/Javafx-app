package com.fousalert.bean;

import redis.clients.util.Slowlog;

/**
 * @author Nikhil
 *
 */
public class SocketCalculateDataObject {

	private String ticker;
	private int apiSerial;
	private String algorithm;
	private Integer period;
	private Integer fastPeriod;
	private Integer fastKPeriod;
	private Integer slowPeriod;
	private Integer slowDPeriod;
	private Integer slowKPeriod;
	private Integer signalPeriod;
	private String chartIndex;
	private String chartUID;
	
	private IndicatorParamBean indicatorParams;
	
	public SocketCalculateDataObject() {
	}
	
	public SocketCalculateDataObject(String ticker, int apiSerial, String algorithm, Integer period, String chartIndex, int slowPeriod) {
		this(ticker, apiSerial, algorithm, period, chartIndex);
		this.slowPeriod = slowPeriod;
	}
	
	public SocketCalculateDataObject(String ticker, int apiSerial, String algorithm, Integer period, String chartIndex) {
		super();
		this.ticker = ticker;
		this.apiSerial = apiSerial;
		this.algorithm = algorithm;
		this.period = period;
		this.chartIndex = chartIndex;
	}
	
	public SocketCalculateDataObject(String ticker, int apiSerial, String algorithm, Integer period, Integer fastPeriod,
			Integer slowPeriod, Integer signalPeriod, String chartIndex) {
		super();
		this.ticker = ticker;
		this.apiSerial = apiSerial;
		this.algorithm = algorithm;
		this.period = period;
		this.fastPeriod = fastPeriod;
		this.slowPeriod = slowPeriod;
		this.signalPeriod = signalPeriod;
		this.chartIndex = chartIndex;
	}
	public SocketCalculateDataObject(String ticker, int apiSerial, String algorithm, Integer period, Integer fastPeriod,
			Integer fastKPeriod ,Integer slowPeriod,Integer slowDPeriod,Integer slowKPeriod, Integer signalPeriod, String chartIndex) {
		this(ticker,apiSerial,algorithm,period,fastPeriod,slowPeriod,signalPeriod,chartIndex);
		this.fastKPeriod = fastKPeriod;
		this.slowDPeriod = slowDPeriod;
		this.slowKPeriod = slowKPeriod;
	}
	
	
	public Integer getFastPeriod() {
		return fastPeriod;
	}
	public void setFastPeriod(Integer fastPeriod) {
		this.fastPeriod = fastPeriod;
	}
	public Integer getSlowPeriod() {
		return slowPeriod;
	}
	public void setSlowPeriod(Integer slowPeriod) {
		this.slowPeriod = slowPeriod;
	}
	public Integer getSignalPeriod() {
		return signalPeriod;
	}
	public void setSignalPeriod(Integer signalPeriod) {
		this.signalPeriod = signalPeriod;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public int getApiSerial() {
		return apiSerial;
	}
	public void setApiSerial(int apiSerial) {
		this.apiSerial = apiSerial;
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

	public IndicatorParamBean getIndicatorParams() {
		return indicatorParams;
	}

	public void setIndicatorParams(IndicatorParamBean indicatorParams) {
		this.indicatorParams = indicatorParams;
	}

	public Integer getSlowDPeriod() {
		return slowDPeriod;
	}

	public void setSlowDPeriod(Integer slowDPeriod) {
		this.slowDPeriod = slowDPeriod;
	}

	public String getChartUID() {
		return chartUID;
	}

	public void setChartUID(String chartUID) {
		this.chartUID = chartUID;
	}

	public Integer getFastKPeriod() {
		return fastKPeriod;
	}

	public void setFastKPeriod(Integer fastKPeriod) {
		this.fastKPeriod = fastKPeriod;
	}

	public Integer getSlowKPeriod() {
		return slowKPeriod;
	}

	public void setSlowKPeriod(Integer slowKPeriod) {
		this.slowKPeriod = slowKPeriod;
	}
}
