package com.fousalert.bean;

import java.util.List;

public class SocketDataCacheObject {

	private List<AbstractTickerCandlePriceHistory> abstractTickerCandlePriceHistory;
	private Integer noOfDays;
	
	public List<AbstractTickerCandlePriceHistory> getAbstractTickerCandlePriceHistory() {
		return abstractTickerCandlePriceHistory;
	}
	public void setAbstractTickerCandlePriceHistory(
			List<AbstractTickerCandlePriceHistory> abstractTickerCandlePriceHistory) {
		this.abstractTickerCandlePriceHistory = abstractTickerCandlePriceHistory;
	}
	
	public Integer getNoOfDays() {
		return noOfDays;
	}
	public void setNoOfDays(Integer noOfDays) {
		this.noOfDays = noOfDays;
	}
}
