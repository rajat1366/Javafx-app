package com.fousalert.bean;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fousalert.database.beans.ResultBean;

public class CandlePriceHistoryResponseBean extends ResultBean {

	private List<AbstractTickerCandlePriceHistory> objects = new ArrayList<AbstractTickerCandlePriceHistory>();
	private Map<String, Object> objectsMap;
	private Date startDate;
	private Date endDate;
	
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public List<AbstractTickerCandlePriceHistory> getObjects() {
		return objects;
	}
	public void setObjects(List<AbstractTickerCandlePriceHistory> objects) {
		this.objects = objects;
	}
	public Map<String, Object> getObjectsMap() {
		return objectsMap;
	}
	public void setObjectsMap(Map<String, Object> objectsMap) {
		this.objectsMap = objectsMap;
	}
}
