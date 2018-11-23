package com.fousalert.bean;

public class ChartPreferenceBean {

	private String navHandleColor;
	private String bgColor;
	private String profitCandleColor;
	private String lossCandleColor;
	private String profitCandleBorderColor;
	private String lossCandleBorderColor;
	private String profitWickColor;
	private String lossWickColor;
	private String tickColor;
	private String tickTextColor;
	private String rsiStrokeColor;
	private String emaStrokeColor;
	private String smaStrokeColor;
	private String stochStrokeColor;
	private String macdStrokeColor;
	private String vmaProfitColor;
	private String vmaLossColor;
	private Double rsiStrokeWidth;
	private Double emaStrokeWidth;
	private Double smaStrokeWidth;
	private Double stochStrokeWidth;
	private Double macdStrokeWidth;
	

	public ChartPreferenceBean(String bgColor, String profitCandleColor, String lossCandleColor,
			String profitCandleBorderColor, String lossCandleBorderColor, String profitWickColor, String lossWickColor,
			String tickColor, String tickTextColor, String rsiStrokeColor, String emaStrokeColor, String smaStrokeColor,
			String stochStrokeColor, String macdStrokeColor, String vmaProfitColor, String vmaLossColor,
			Double rsiStrokeWidth, Double emaStrokeWidth, Double smaStrokeWidth, Double stochStrokeWidth,
			Double macdStrokeWidth) {
		super();
		this.bgColor = bgColor;
		this.profitCandleColor = profitCandleColor;
		this.lossCandleColor = lossCandleColor;
		this.profitCandleBorderColor = profitCandleBorderColor;
		this.lossCandleBorderColor = lossCandleBorderColor;
		this.profitWickColor = profitWickColor;
		this.lossWickColor = lossWickColor;
		this.tickColor = tickColor;
		this.tickTextColor = tickTextColor;
		this.rsiStrokeColor = rsiStrokeColor;
		this.emaStrokeColor = emaStrokeColor;
		this.smaStrokeColor = smaStrokeColor;
		this.stochStrokeColor = stochStrokeColor;
		this.macdStrokeColor = macdStrokeColor;
		this.vmaProfitColor = vmaProfitColor;
		this.vmaLossColor = vmaLossColor;
		this.rsiStrokeWidth = rsiStrokeWidth;
		this.emaStrokeWidth = emaStrokeWidth;
		this.smaStrokeWidth = smaStrokeWidth;
		this.stochStrokeWidth = stochStrokeWidth;
		this.macdStrokeWidth = macdStrokeWidth;
	}
	
	
	public String getTickTextColor() {
		return tickTextColor;
	}

	public void setTickTextColor(String tickTextColor) {
		this.tickTextColor = tickTextColor;
	}

	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	public String getProfitCandleColor() {
		return profitCandleColor;
	}

	public void setProfitCandleColor(String profitCandleColor) {
		this.profitCandleColor = profitCandleColor;
	}

	public String getLossCandleColor() {
		return lossCandleColor;
	}

	public void setLossCandleColor(String lossCandleColor) {
		this.lossCandleColor = lossCandleColor;
	}

	public String getProfitCandleBorderColor() {
		return profitCandleBorderColor;
	}

	public void setProfitCandleBorderColor(String profitCandleBorderColor) {
		this.profitCandleBorderColor = profitCandleBorderColor;
	}

	public String getLossCandleBorderColor() {
		return lossCandleBorderColor;
	}

	public void setLossCandleBorderColor(String lossCandleBorderColor) {
		this.lossCandleBorderColor = lossCandleBorderColor;
	}

	public String getProfitWickColor() {
		return profitWickColor;
	}

	public void setProfitWickColor(String profitWickColor) {
		this.profitWickColor = profitWickColor;
	}

	public String getLossWickColor() {
		return lossWickColor;
	}

	public void setLossWickColor(String lossWickColor) {
		this.lossWickColor = lossWickColor;
	}

	public String getTickColor() {
		return tickColor;
	}

	public void setTickColor(String tickColor) {
		this.tickColor = tickColor;
	}

	public String getRsiStrokeColor() {
		return rsiStrokeColor;
	}

	public void setRsiStrokeColor(String rsiStrokeColor) {
		this.rsiStrokeColor = rsiStrokeColor;
	}

	public String getEmaStrokeColor() {
		return emaStrokeColor;
	}

	public void setEmaStrokeColor(String emaStrokeColor) {
		this.emaStrokeColor = emaStrokeColor;
	}

	public String getSmaStrokeColor() {
		return smaStrokeColor;
	}

	public void setSmaStrokeColor(String smaStrokeColor) {
		this.smaStrokeColor = smaStrokeColor;
	}

	public String getStochStrokeColor() {
		return stochStrokeColor;
	}

	public void setStochStrokeColor(String stochStrokeColor) {
		this.stochStrokeColor = stochStrokeColor;
	}

	public String getMacdStrokeColor() {
		return macdStrokeColor;
	}

	public void setMacdStrokeColor(String macdStrokeColor) {
		this.macdStrokeColor = macdStrokeColor;
	}

	public String getVmaProfitColor() {
		return vmaProfitColor;
	}

	public void setVmaProfitColor(String vmaProfitColor) {
		this.vmaProfitColor = vmaProfitColor;
	}

	public String getVmaLossColor() {
		return vmaLossColor;
	}

	public void setVmaLossColor(String vmaLossColor) {
		this.vmaLossColor = vmaLossColor;
	}

	public Double getRsiStrokeWidth() {
		return rsiStrokeWidth;
	}

	public void setRsiStrokeWidth(Double rsiStrokeWidth) {
		this.rsiStrokeWidth = rsiStrokeWidth;
	}

	public Double getEmaStrokeWidth() {
		return emaStrokeWidth;
	}

	public void setEmaStrokeWidth(Double emaStrokeWidth) {
		this.emaStrokeWidth = emaStrokeWidth;
	}

	public Double getSmaStrokeWidth() {
		return smaStrokeWidth;
	}

	public void setSmaStrokeWidth(Double smaStrokeWidth) {
		this.smaStrokeWidth = smaStrokeWidth;
	}

	public Double getStochStrokeWidth() {
		return stochStrokeWidth;
	}

	public void setStochStrokeWidth(Double stochStrokeWidth) {
		this.stochStrokeWidth = stochStrokeWidth;
	}

	public Double getMacdStrokeWidth() {
		return macdStrokeWidth;
	}

	public void setMacdStrokeWidth(Double macdStrokeWidth) {
		this.macdStrokeWidth = macdStrokeWidth;
	}


	public String getNavHandleColor() {
		return navHandleColor;
	}

	public void setNavHandleColor(String navHandleColor) {
		this.navHandleColor = navHandleColor;
	}
}
