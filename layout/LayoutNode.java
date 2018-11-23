package com.fousalert.layout;

import com.fousalert.bean.ChartConfig;
import com.fousalert.customfx.DockPos;
import com.fousalert.utils.Constants.DockPanePosition;
import com.fousalert.utils.Constants.SideDockNodeTitle;

public class LayoutNode {
	private DockPos dockPos;
	private String relativeDockNode;
	private String nodeType;
	private SideDockNodeTitle nodeTitle;
	private Integer chartUID;
	private boolean isFloating;
	private double width;
	private double height;
	private double xPosPercent;
	private double yPosPercent;
	private ChartConfig chartConfig;
	private DockPanePosition dockPanePosition;
	
	public LayoutNode(DockPos dockPos, String relativeDockNode, String nodeType,
			SideDockNodeTitle nodeTitle, Integer chartUID, boolean isFloating, double width, double height, double xPosPercent,
			double yPosPercent, ChartConfig chartConfig, DockPanePosition dockPanePosition) {
		super();
		this.dockPos = dockPos;
		this.relativeDockNode = relativeDockNode;
		this.nodeType = nodeType;
		this.nodeTitle = nodeTitle;
		this.chartUID = chartUID;
		this.isFloating = isFloating;
		this.width = width;
		this.height = height;
		this.xPosPercent = xPosPercent;
		this.yPosPercent = yPosPercent;
		this.chartConfig = chartConfig;
		this.dockPanePosition = dockPanePosition;
	}

	public DockPanePosition getDockPanePosition() {
		return dockPanePosition;
	}

	public void setDockPanePosition(DockPanePosition dockPanePosition) {
		this.dockPanePosition = dockPanePosition;
	}

	public ChartConfig getChartConfig() {
		return chartConfig;
	}

	public void setChartConfig(ChartConfig chartConfig) {
		this.chartConfig = chartConfig;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getxPosPercent() {
		return xPosPercent;
	}

	public void setxPosPercent(double xPosPercent) {
		this.xPosPercent = xPosPercent;
	}

	public double getyPosPercent() {
		return yPosPercent;
	}

	public void setyPosPercent(double yPosPercent) {
		this.yPosPercent = yPosPercent;
	}

	public boolean isFloating() {
		return isFloating;
	}

	public void setFloating(boolean isFloating) {
		this.isFloating = isFloating;
	}

	public Integer getChartUID() {
		return chartUID;
	}

	public void setChartUID(Integer chartUID) {
		this.chartUID = chartUID;
	}

	public DockPos getDockPos() {
		return dockPos;
	}
	
	public void setDockPos(DockPos dockPos) {
		this.dockPos = dockPos;
	}
	
	public String getRelativeDockNode() {
		return relativeDockNode;
	}
	
	public void setRelativeDockNode(String relativeDockNode) {
		this.relativeDockNode = relativeDockNode;
	}
	
	public String getNodeType() {
		return nodeType;
	}
	
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	
	public SideDockNodeTitle getNodeTitle() {
		return nodeTitle;
	}
	
	public void setNodeTitle(SideDockNodeTitle nodeTitle) {
		this.nodeTitle = nodeTitle;
	}
}