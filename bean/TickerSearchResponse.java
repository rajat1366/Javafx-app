package com.fousalert.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fousalert.database.beans.ResultBean;
import com.fousalert.database.beans.UITicker;

public class TickerSearchResponse extends ResultBean {

	private List<UITicker> objects = new ArrayList<UITicker>();
	private Map<String, Object> objectsMap;

	public List<UITicker> getObjects() {
		return objects;
	}
	public void setObjects(List<UITicker> objects) {
		this.objects = objects;
	}
	public Map<String, Object> getObjectsMap() {
		return objectsMap;
	}
	public void setObjectsMap(Map<String, Object> objectsMap) {
		this.objectsMap = objectsMap;
	}
}
