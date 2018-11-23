package com.fousalert.application;


import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class HistoricalChartBrowser extends Region {
	final WebView webview = new WebView();
	final WebEngine webEngine = webview.getEngine();
	
	public HistoricalChartBrowser() {
		webview.setPrefHeight(800);
		webview.setPrefWidth(1300);
		webEngine.load(getClass().getResource("/web/content/historicalChart.html").toExternalForm());
	
		getChildren().add(webview);
		
	}
	
	public WebView getWebview() {
		return webview;
	}

	public WebEngine getWebEngine() {
		return webEngine;
	}
	
}
