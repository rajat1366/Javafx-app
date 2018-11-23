package com.fousalert.bean;

import com.fousalert.utils.Constants.AlertDirection;

public class TrendLineParamBean {
	private Long x1;
	private Double y1;
	private Long x2;
	private Double y2;
	private Integer iD;
	private double slope;
	
	private int x1Index;
	private int x2Index;
	
	private boolean enabled;
	private double lowValue;
	private double highValue;
	private String tickerName;
	private boolean checkCrossingAboveAndDown = true;
	private long trendlineId;
	private boolean isSnoozedForDay = false;
	private long snoozeUpto;
	private long snoozeMinutes;
	
	private boolean showPopup;
	private boolean playSound;
	private boolean alertOnlyCrossAbove;
	private boolean alertOnlyCrossDown;
	private String alertText;
	
	private String lineColor;
	private AlertDirection alertDirection;
	private String chartUID;
	
	//For horizontal Trendline
	private String strokeColor;
	private String fillColor;
	private Double height;
	
	public TrendLineParamBean() {
		super();
	}

	public TrendLineParamBean(Long x1, Double y1, Long x2, Double y2, Integer iD) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.iD = iD;
	}
	

	public TrendLineParamBean(double lowValue, double highValue, String tickerName, boolean isBothCheckEnabled, int trendlineId) {
		super();
		this.lowValue = lowValue;
		this.highValue = highValue;
		this.tickerName = tickerName;
		this.checkCrossingAboveAndDown = isBothCheckEnabled;
		this.iD = trendlineId;
	}

	public String getChartUID() {
		return chartUID;
	}

	public void setChartUID(String chartUID) {
		this.chartUID = chartUID;
	}

	public String getLineColor() {
		return lineColor;
	}

	public void setLineColor(String lineColor) {
		this.lineColor = lineColor;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(String strokeColor) {
		this.strokeColor = strokeColor;
	}

	public String getFillColor() {
		return fillColor;
	}

	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Integer getiD() {
		return iD;
	}

	public void setiD(Integer iD) {
		this.iD = iD;
	}

	public Long getX1() {
		return x1;
	}
	public void setX1(Long x1) {
		this.x1 = x1;
	}
	public Double getY1() {
		return y1;
	}
	public void setY1(Double y1) {
		this.y1 = y1;
	}
	public Long getX2() {
		return x2;
	}
	public void setX2(Long x2) {
		this.x2 = x2;
	}
	public Double getY2() {
		return y2;
	}
	public void setY2(Double y2) {
		this.y2 = y2;
	}
	
	public double getLowValue() {
		return lowValue;
	}
	public void setLowValue(double lowValue) {
		this.lowValue = lowValue;
	}
	public double getHighValue() {
		return highValue;
	}
	public void setHighValue(double highValue) {
		this.highValue = highValue;
	}
	public String getTickerName() {
		return tickerName;
	}
	public void setTickerName(String tickerName) {
		this.tickerName = tickerName;
	}
	public boolean isCheckCrossingAboveAndDown() {
		return checkCrossingAboveAndDown;
	}
	public void setCheckCrossingAboveAndDown(boolean checkCrossingAboveAndDown) {
		this.checkCrossingAboveAndDown = checkCrossingAboveAndDown;
	}
	public long getTrendlineId() {
		return trendlineId;
	}
	public void setTrendlineId(long trendlineId) {
		this.trendlineId = trendlineId;
	}

	public boolean isSnoozedForDay() {
		return isSnoozedForDay;
	}

	public void setSnoozedForDay(boolean isSnoozedForDay) {
		this.isSnoozedForDay = isSnoozedForDay;
	}

	public long getSnoozeUpto() {
		return snoozeUpto;
	}
	public void setSnoozeUpto(long snoozeUpto) {
		this.snoozeUpto = snoozeUpto;
	}

	public long getSnoozeMinutes() {
		return snoozeMinutes;
	}
	public void setSnoozeMinutes(long snoozeMinutes) {
		this.snoozeMinutes = snoozeMinutes;
	}

	public int getX1Index() {
		return x1Index;
	}

	public void setX1Index(int x1Index) {
		this.x1Index = x1Index;
	}

	public int getX2Index() {
		return x2Index;
	}

	public void setX2Index(int x2Index) {
		this.x2Index = x2Index;
	}

	public double getSlope() {
		return slope;
	}

	public void setSlope(double slope) {
		this.slope = slope;
	}

	public boolean isShowPopup() {
		return showPopup;
	}

	public void setShowPopup(boolean showPopup) {
		this.showPopup = showPopup;
	}

	public boolean isPlaySound() {
		return playSound;
	}

	public void setPlaySound(boolean playSound) {
		this.playSound = playSound;
	}

	public boolean isAlertOnlyCrossAbove() {
		return alertOnlyCrossAbove;
	}

	public void setAlertOnlyCrossAbove(boolean alertOnlyCrossAbove) {
		this.alertOnlyCrossAbove = alertOnlyCrossAbove;
	}

	public boolean isAlertOnlyCrossDown() {
		return alertOnlyCrossDown;
	}

	public void setAlertOnlyCrossDown(boolean alertOnlyCrossDown) {
		this.alertOnlyCrossDown = alertOnlyCrossDown;
	}

	public String getAlertText() {
		return alertText;
	}

	public void setAlertText(String alertText) {
		this.alertText = alertText;
	}

	public AlertDirection getAlertDirection() {
		return alertDirection;
	}

	public void setAlertDirection(AlertDirection alertDirection) {
		this.alertDirection = alertDirection;
	}
}