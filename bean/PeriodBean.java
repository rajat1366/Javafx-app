package com.fousalert.bean;

public class PeriodBean {

	private String period;
	private String slowPeriod;
	private String fastPeriod;
	private String signalPeriod;
	private String slowDPeriod;
	private String slowKPeriod;
	private String fastKPeriod;
	
	public PeriodBean() {
		
	}

	public PeriodBean(String period, String slowPeriod, String fastPeriod,
			String signalPeriod) {
		super();
		this.period = period;
		this.slowPeriod = slowPeriod;
		this.fastPeriod = fastPeriod;
		this.signalPeriod = signalPeriod;
	}
	
	public PeriodBean(String fastKPeriod, String slowKPeriod, String slowDPeriod) {
		super();
		this.fastKPeriod = fastKPeriod;
		this.slowKPeriod = slowKPeriod;
		this.slowDPeriod = slowDPeriod;
	}
	
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getSlowPeriod() {
		return slowPeriod;
	}
	public void setSlowPeriod(String slowPeriod) {
		this.slowPeriod = slowPeriod;
	}
	public String getFastPeriod() {
		return fastPeriod;
	}
	public void setFastPeriod(String fastPeriod) {
		this.fastPeriod = fastPeriod;
	}
	public String getSignalPeriod() {
		return signalPeriod;
	}
	public void setSignalPeriod(String signalPeriod) {
		this.signalPeriod = signalPeriod;
	}

	public String getSlowDPeriod() {
		return slowDPeriod;
	}

	public void setSlowDPeriod(String slowDPeriod) {
		this.slowDPeriod = slowDPeriod;
	}

	public String getSlowKPeriod() {
		return slowKPeriod;
	}

	public void setSlowKPeriod(String slowKPeriod) {
		this.slowKPeriod = slowKPeriod;
	}

	public String getFastKPeriod() {
		return fastKPeriod;
	}

	public void setFastKPeriod(String fastKPeriod) {
		this.fastKPeriod = fastKPeriod;
	}
}