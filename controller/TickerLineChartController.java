package com.fousalert.controller;

import com.fousalert.application.interfaces.Closeable;
import com.fousalert.utils.Constants;
import com.fousalert.utils.Context;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class TickerLineChartController implements  Closeable {

	@FXML private WebView tickerLineChart;
	@FXML private Button backButton;
	private WebEngine webEngine;
	
	@FXML private void onBackButtonClicked(ActionEvent event) {
		Stage stage = (Stage) Context.getContext().get(Constants.PARENT_STAGE);
		Scene scene = (Scene) Context.getContext().get(Constants.PARENT_SCENE);
		stage.setScene(scene);
	}
	
	@FXML private void initialize(){
		webEngine = tickerLineChart.getEngine();
		webEngine.load(getClass().getResource("/line.html").toExternalForm());
	}
	@Override
	public void close() {
	}
}
