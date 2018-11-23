package com.fousalert.layout;

import java.util.List;

public class ParentLayout {
	
	private boolean showSideBars;
	private List<LayoutNode> layoutList;
	private boolean showRightMenuBar;
	private List<Double> leftPaneDividerPositions;
	private List<Double> middlePaneDividerPositions;
	private List<Double> rightPaneDividerPositions;
	
	public boolean isShowSideBars() {
		return showSideBars;
	}
	public void setShowSideBars(boolean showSideBars) {
		this.showSideBars = showSideBars;
	}
	public List<LayoutNode> getLayoutList() {
		return layoutList;
	}
	public void setLayoutList(List<LayoutNode> layoutList) {
		this.layoutList = layoutList;
	}
	public List<Double> getLeftPaneDividerPositions() {
		return leftPaneDividerPositions;
	}
	public void setLeftPaneDividerPositions(List<Double> leftPaneDividerPositions) {
		this.leftPaneDividerPositions = leftPaneDividerPositions;
	}
	public List<Double> getMiddlePaneDividerPositions() {
		return middlePaneDividerPositions;
	}
	public void setMiddlePaneDividerPositions(List<Double> middlePaneDividerPositions) {
		this.middlePaneDividerPositions = middlePaneDividerPositions;
	}
	public List<Double> getRightPaneDividerPositions() {
		return rightPaneDividerPositions;
	}
	public void setRightPaneDividerPositions(List<Double> rightPaneDividerPositions) {
		this.rightPaneDividerPositions = rightPaneDividerPositions;
	}
	public boolean isShowRightMenuBar() {
		return showRightMenuBar;
	}
	public void setShowRightMenuBar(boolean showRightMenuBar) {
		this.showRightMenuBar = showRightMenuBar;
	}
}