package com.fousalert.bean;

public class F1FilterBean {

	private double priceMin;
	private double priceMax;
	private double volumeMin;
	private double volumeMax;

	public F1FilterBean(double priceMin, double priceMax, double volumeMin,
			double volumeMax) {
		super();
		this.priceMin = priceMin;
		this.priceMax = priceMax;
		this.volumeMin = volumeMin;
		this.volumeMax = volumeMax;
	}
	
	public double getPriceMin() {
		return priceMin;
	}
	public void setPriceMin(double priceMin) {
		this.priceMin = priceMin;
	}
	public double getPriceMax() {
		return priceMax;
	}
	public void setPriceMax(double priceMax) {
		this.priceMax = priceMax;
	}
	public double getVolumeMin() {
		return volumeMin;
	}
	public void setVolumeMin(double volumeMin) {
		this.volumeMin = volumeMin;
	}
	public double getVolumeMax() {
		return volumeMax;
	}
	public void setVolumeMax(double volumeMax) {
		this.volumeMax = volumeMax;
	}
}