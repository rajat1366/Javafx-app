package com.fousalert.application;

import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class MyBrowser extends Region {
	final WebView webview = new WebView();
	final WebEngine webEngine = webview.getEngine();

	public MyBrowser() {
    	webview.setPrefHeight(680);
		webview.setPrefWidth(820);
		webEngine
				.load(getClass().getResource("/web/content/landing.html").toExternalForm());
		getChildren().add(webview);

	}

	public WebView getWebview() {
		return webview;
	}

	public WebEngine getWebEngine() {
		return webEngine;
	}
	

}
