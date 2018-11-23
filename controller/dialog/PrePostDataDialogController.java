package com.fousalert.controller.dialog;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class PrePostDataDialogController implements Initializable {
	
	private Stage stage;
	@FXML private RadioButton neverRadioButton;
	@FXML private RadioButton alwaysRadioButton;
	@FXML private RadioButton outsideRadioButton;
	@FXML private ToggleGroup radioToggleGroup;
	
	public PrePostDataDialogController() {
		super();
	}
	
	@FXML public void neverRadioButtonClicked(ActionEvent event) {
		onRadioButtonClicked();
	}
	
	@FXML public void alwaysRadioButtonClicked(ActionEvent event) {
		onRadioButtonClicked();	
	}
	
	@FXML public void outsideRadioButtonClicked(ActionEvent event) {
		onRadioButtonClicked();
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	private void onRadioButtonClicked() {
		stage.close();
	}
	
	public void setPosAndShowStage(double xPos, double yPos) {
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}