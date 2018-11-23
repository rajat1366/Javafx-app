package com.fousalert.bean;

import java.util.ArrayList;
import java.util.List;

import com.fousalert.database.beans.ResultBean;
import com.fousalert.database.entities.db2.TickerNews;

public class NewsResponse extends ResultBean {

	private List<TickerNews> objects = new ArrayList<TickerNews>();

	public List<TickerNews> getObjects() {
		return objects;
	}
	public void setObjects(List<TickerNews> objects) {
		this.objects = objects;
	}
}
