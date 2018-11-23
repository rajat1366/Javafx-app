package com.fousalert.utils;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ConnectionLostDialog {

	private Dialog<Boolean> dialog = new Dialog<Boolean>();
	private static boolean isDialogAlreadyShowing = false;
	
	public ConnectionLostDialog() {
		super();
		initializeDialog();
	}

	private void initializeDialog() {
		dialog.setHeaderText(Constants.applicationLanguage.getProperty("error.server.connection.lost"));
		dialog.setContentText(Constants.applicationLanguage.getProperty("error.application.shutdown"));
		dialog.getDialogPane().getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
		dialog.initStyle(StageStyle.TRANSPARENT);
		dialog.getDialogPane().setOpacity(0.85);
		dialog.getDialogPane().setMinSize(250, 80);
		((Stage)dialog.getDialogPane().getScene().getWindow()).setResizable(false);
		
		Stage parentStage = (Stage) Context.getContext().get(Constants.PARENT_STAGE);
		Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();	
		dialogStage.initOwner(parentStage);
		dialogStage.initModality(Modality.WINDOW_MODAL);
	}
	
	public void showLostConnectionDialog() {
		if(!isDialogAlreadyShowing) {
			isDialogAlreadyShowing = true;
			ButtonType okButtonType = ButtonType.OK;
			dialog.getDialogPane().getButtonTypes().addAll(okButtonType);
			dialog.showAndWait();
			isDialogAlreadyShowing = false;
			System.exit(0);  //As per Naval
		}
	}
}