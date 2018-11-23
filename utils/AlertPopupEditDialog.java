package com.fousalert.utils;

import java.util.Optional;
import java.util.regex.Pattern;

import com.fousalert.bean.AlertEditPopupBean;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class AlertPopupEditDialog {

	private Dialog<AlertEditPopupBean> dialog = new Dialog<AlertEditPopupBean>();
	
	public AlertPopupEditDialog() {
		super();
		initializeDialog();
	}

	private void initializeDialog() {
//		dialog.setHeaderText(Constants.applicationLanguage.getProperty("label.alert.popup.edit.header"));
		dialog.setTitle(Constants.applicationLanguage.getProperty("label.alert.popup.edit.header"));
		dialog.getDialogPane().getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
		dialog.getDialogPane().getStylesheets().add(getClass().getResource("/dialogBox.css").toExternalForm());
		dialog.initStyle(StageStyle.UTILITY);
		dialog.getDialogPane().getStyleClass().add("main-background-color");
		//dialog.getDialogPane().getStyleClass().add("setAl");
		dialog.getDialogPane().setOpacity(0.85);
		dialog.getDialogPane().setMinSize(250, 80);
		((Stage)dialog.getDialogPane().getScene().getWindow()).setResizable(false);
		
		Stage parentStage = (Stage) Context.getContext().get(Constants.PARENT_STAGE);
		Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();	
		dialogStage.initOwner(parentStage);
		dialogStage.initModality(Modality.WINDOW_MODAL);
	}
	
	public AlertEditPopupBean showAlertPopupEditDialog() {
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(30));
		grid.setHgap(5);
		grid.setVgap(5);
		
		Label timeLabel = new Label(Constants.applicationLanguage.getProperty("label.snooze.time.mins"));
		timeLabel.getStyleClass().add("white-text-color");
		
		TextField timeTextField = new TextField();
		
		CheckBox checkBox = new CheckBox(Constants.applicationLanguage.getProperty("label.alert.popup.edit.checkbox.content"));
		checkBox.setStyle("-fx-margin: 10 0 0 0; -fx-text-fill: #ffffff");
		
		GridPane.setHalignment(timeLabel, HPos.LEFT);
		grid.add(timeLabel, 0, 0);

		GridPane.setHalignment(timeTextField, HPos.RIGHT);
		grid.add(timeTextField, 1, 0);

		GridPane.setHalignment(checkBox, HPos.LEFT);
		grid.add(checkBox, 1, 1);
		
		ButtonType okButtonType = ButtonType.OK;
		
		
		dialog.getDialogPane().getButtonTypes().clear();
		dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
		
		dialog.getDialogPane().setContent(grid);
		Button okButton = ((Button) dialog.getDialogPane().lookupButton(okButtonType));
		okButton.getStyleClass().add("defaultDialogButton");
		
		Button cancelButton = ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL));
		cancelButton.getStyleClass().add("defaultCancelButton");
		
		dialog.setResultConverter(new Callback<ButtonType, AlertEditPopupBean>() {
			@Override
			public AlertEditPopupBean call(ButtonType b) {
				if (b == okButtonType) {
					return new AlertEditPopupBean(timeTextField.getText(), checkBox.isSelected());
				}
				return null;
			}
		});
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				timeTextField.requestFocus();
			}
		});
		
		
		Optional<AlertEditPopupBean> result = dialog.showAndWait();

		if(result.isPresent()) {
			if(validateSnoozeTime(result.get())) {
				return result.get();
			} else {
				String invalidParameterMessage = Constants.applicationLanguage.getProperty("error.invalid.input");
				showErrorDialog(invalidParameterMessage, invalidParameterMessage, null);
				return showAlertPopupEditDialog();
			}
		}
		
		return null;
	}
	
	private boolean validateSnoozeTime(AlertEditPopupBean alertEditPopupBean) {
		String textFieldContent = alertEditPopupBean.getSnoozeTime();
		if((alertEditPopupBean.isAlertDisabled()) || (textFieldContent != null) && (!textFieldContent.isEmpty()) && (textFieldContent.matches(Pattern.compile(Constants.POSITIVE_NUMBER_REGEX).toString()))) {
			return true;
		}
		return false;
	}
	
	private void showErrorDialog(String headerText, String title, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.initStyle(StageStyle.TRANSPARENT);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.setContentText(content);
		alert.showAndWait();
	}
}