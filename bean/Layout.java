package com.fousalert.bean;

public class Layout {
	
	private Integer layoutID;
	private String layoutName;
	
	public Layout(Integer layoutID, String layoutName) {
		super();
		this.layoutID = layoutID;
		this.layoutName = layoutName;
	}
	public Integer getLayoutID() {
		return layoutID;
	}
	public void setLayoutID(Integer layoutID) {
		this.layoutID = layoutID;
	}
	public String layoutNameProperty() {
		return layoutName;
	}
	public void setLayoutName(String layoutName) {
		this.layoutName = layoutName;
	}
	
	
}
