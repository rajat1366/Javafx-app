package com.fousalert.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fousalert.database.beans.ResultBean;

public class F1TickerResponseBean extends ResultBean {

	private List<F1Ticker> objects = new ArrayList<F1Ticker>();
	private Map<String, Object> objectsMap;
	
	private Long objectId;
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
	public List<F1Ticker> getObjects() {
		return objects;
	}
	public void setObjects(List<F1Ticker> objects) {
		this.objects = objects;
	}
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	public Map<String, Object> getObjectsMap() {
		return objectsMap;
	}
	public void setObjectsMap(Map<String, Object> objectsMap) {
		this.objectsMap = objectsMap;
	}
}
