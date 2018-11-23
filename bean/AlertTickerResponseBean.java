package com.fousalert.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fousalert.database.beans.ResultBean;

public class AlertTickerResponseBean extends ResultBean {

	private Map<String, List<AlertTicker>> objectsMap = new HashMap<String, List<AlertTicker>>();
	private List<Object> objects;
	private Long objectId;
	
	public List<Object> getObjects() {
		return objects;
	}
	public void setObjects(List<Object> objects) {
		this.objects = objects;
	}
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	public Map<String, List<AlertTicker>> getObjectsMap() {
		return objectsMap;
	}
	public void setObjectsMap(Map<String, List<AlertTicker>> objectsMap) {
		this.objectsMap = objectsMap;
	}
}