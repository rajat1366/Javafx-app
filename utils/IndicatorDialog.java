package com.fousalert.utils;

import java.util.Optional;
import java.util.regex.Pattern;

import com.fousalert.bean.PeriodBean;
import com.fousalert.utils.Constants.IndicatorType;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class IndicatorDialog {

	private Dialog<PeriodBean> dialog = new Dialog<PeriodBean>();

	public IndicatorDialog() {
		super();
		initializeDialog();
	}

	private void initializeDialog() {
		dialog.setHeaderText(null);
		dialog.setContentText(null);
		dialog.getDialogPane().getStylesheets().add(getClass().getResource("/dialogBox.css").toExternalForm());
		dialog.getDialogPane().getStyleClass().add("background-color");
		dialog.initStyle(StageStyle.UTILITY);
		/*dialog.getDialogPane().setOpacity(1);*/

		Stage parentStage = (Stage) Context.getContext().get(Constants.PARENT_STAGE);
		Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();	
		dialogStage.initOwner(parentStage);
		dialogStage.initModality(Modality.WINDOW_MODAL);
	}

	public PeriodBean showDialogBox(String indicator) {
		IndicatorType d = IndicatorType.getByValue(indicator);
		if(d != null) {
			switch (d) {
			case RSI:
			case EMA:
			case SMA:
			case VMA:
				return showPeriodTextFieldDialog(indicator);
			case MACD:
				return showMACDParamsDialog();
			case STOCH:
				return showSTOCHParamsDialog();
			case MOVING_AVG:
				//			return showMovingAvgIndicatorDialog();
			}
		}
		return null;
	}

	private void showErrorDialog(String headerText, String title, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.initStyle(StageStyle.TRANSPARENT);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private boolean validateMACDParamsBean(PeriodBean period) {
		if((period.getPeriod() != null) && (!period.getPeriod().toString().isEmpty()) && (period.getPeriod().toString().matches(Pattern.compile(Constants.NEGATIVE_POSITIVE_NUMBER_REGEX).toString()))) {
			return true;
		} else if((period.getSlowPeriod() != null) && (period.getFastPeriod() != null) && (period.getSignalPeriod() != null) && (!period.getSlowPeriod().toString().isEmpty()) && (!period.getFastPeriod().toString().isEmpty()) && (!period.getSignalPeriod().toString().isEmpty())) {
			if(period.getSlowPeriod().toString().matches(Pattern.compile(Constants.NEGATIVE_POSITIVE_NUMBER_REGEX).toString()) && period.getFastPeriod().toString().matches(Pattern.compile(Constants.NEGATIVE_POSITIVE_NUMBER_REGEX).toString()) && period.getSignalPeriod().toString().matches(Pattern.compile(Constants.NEGATIVE_POSITIVE_NUMBER_REGEX).toString())) {
				return true;
			} 
		} 
		return false;
	}

	private boolean validateSTOCHParamsBean(PeriodBean period) {
		boolean isValid = false;
		if(!(ValidationUtil.isNullOrEmpty(period.getSlowKPeriod(), true) || ValidationUtil.isNullOrEmpty(period.getFastKPeriod(), true) || ValidationUtil.isNullOrEmpty(period.getSlowDPeriod(), true))) {
			if(ValidationUtil.isMatched(period.getFastKPeriod(), Constants.NEGATIVE_POSITIVE_NUMBER_REGEX) && ValidationUtil.isMatched(period.getSlowKPeriod(), Constants.NEGATIVE_POSITIVE_NUMBER_REGEX) && ValidationUtil.isMatched(period.getSlowDPeriod(), Constants.NEGATIVE_POSITIVE_NUMBER_REGEX)) {
				isValid = true;
			}
		}

		return isValid;
	}

	private PeriodBean showPeriodTextFieldDialog(String title) {
		GridPane grid = new GridPane();

		TextField periodField = new TextField();
		periodField.setPromptText(title+" Period Value");

		Label periodLabel = new Label("Period: ");
		periodLabel.getStyleClass().add("white-text-color");

		ButtonType okButtonType = new ButtonType(Constants.applicationLanguage.getProperty("label.save"), ButtonData.OK_DONE);
		
		dialog.getDialogPane().getButtonTypes().clear();
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, okButtonType);
		
		Button okButton = ((Button) dialog.getDialogPane().lookupButton(okButtonType));
		okButton.setDefaultButton(true);
		
		okButton.getStyleClass().add("defaultDialogButton");
		
		Button cancelButton = ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL));
		cancelButton.getStyleClass().add("defaultCancelButton");

		grid.setPadding(new Insets(30));
		grid.setHgap(5);
		grid.setVgap(5);

		// First name label
		GridPane.setHalignment(periodLabel, HPos.LEFT);
		grid.add(periodLabel, 0, 0);

		// Last name label
		GridPane.setHalignment(periodField, HPos.RIGHT);
		grid.add(periodField, 1, 0);

		dialog.getDialogPane().setContent(grid);
		dialog.setResultConverter(new Callback<ButtonType, PeriodBean>() {
			@Override
			public PeriodBean call(ButtonType b) {
				if (b == okButtonType) {
					return new PeriodBean(periodField.getText().toString(), null, null, null);
				}
				return null;
			}
		});
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				periodField.requestFocus();
			}
		});
		Optional<PeriodBean> result = dialog.showAndWait();

		if(result.isPresent()) {
			if(validateMACDParamsBean(result.get())) {
				return result.get();
			} else {
				String invalidParameterMessage = Constants.applicationLanguage.getProperty("invalid.parameter.values");
				showErrorDialog(invalidParameterMessage, invalidParameterMessage, null);
				return showPeriodTextFieldDialog(title);
			}
		}

		return null;
	}

	private PeriodBean showMACDParamsDialog() {
		GridPane grid = new GridPane();

		Label slowPeriodLabel = new Label("Slow Period: ");
		slowPeriodLabel.getStyleClass().add("white-text-color");

		TextField slowPeriodField = new TextField();
		slowPeriodField.setPromptText("Slow Period Values");

		Label fastPeriodLabel = new Label("Fast Period: ");
		fastPeriodLabel.getStyleClass().add("white-text-color");

		TextField fastPeriodField = new TextField();
		fastPeriodField.setPromptText("Fast Period Values");

		Label signalPeriodLabel = new Label("Signal Period: ");
		signalPeriodLabel.getStyleClass().add("white-text-color");

		TextField signalPeriodField = new TextField();
		signalPeriodField.setPromptText("Signal Period Values");

		ButtonType okButtonType = new ButtonType("Done", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().clear();
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, okButtonType);

		grid.setPadding(new Insets(5));
		grid.setHgap(5);
		grid.setVgap(5);

		GridPane.setHalignment(fastPeriodLabel, HPos.LEFT);
		grid.add(fastPeriodLabel, 0, 0);
		GridPane.setHalignment(fastPeriodField, HPos.RIGHT);
		grid.add(fastPeriodField, 1, 0);

		GridPane.setHalignment(slowPeriodLabel, HPos.LEFT);
		grid.add(slowPeriodLabel, 0, 1);
		GridPane.setHalignment(slowPeriodField, HPos.RIGHT);
		grid.add(slowPeriodField, 1, 1);

		GridPane.setHalignment(signalPeriodLabel, HPos.LEFT);
		grid.add(signalPeriodLabel, 0, 2);
		GridPane.setHalignment(signalPeriodField, HPos.RIGHT);
		grid.add(signalPeriodField, 1, 2);

		dialog.getDialogPane().setContent(grid);
		dialog.setResultConverter(new Callback<ButtonType, PeriodBean>() {
			@Override
			public PeriodBean call(ButtonType b) {
				if (b == okButtonType) {
					return new PeriodBean(null, slowPeriodField.getText().toString(), fastPeriodField.getText().toString(), signalPeriodField.getText().toString());
				}
				return null;
			}
		});
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				fastPeriodField.requestFocus();
			}
		});
		Optional<PeriodBean> result = dialog.showAndWait();

		if(result.isPresent()) {
			if(validateMACDParamsBean(result.get())) {
				return result.get();
			} else {
				String invalidParameterMessage = Constants.applicationLanguage.getProperty("invalid.parameter.values");
				showErrorDialog(invalidParameterMessage, invalidParameterMessage, null);
				showMACDParamsDialog();
			}
		}
		return null;
	}

	private PeriodBean showSTOCHParamsDialog() {
		GridPane grid = new GridPane();

		String fastKPeriodLabelString = Constants.applicationLanguage.getProperty("label.fast.k.period");
		String slowKPeriodLabelString = Constants.applicationLanguage.getProperty("label.slow.k.period");
		String slowDPeriodLabelString = Constants.applicationLanguage.getProperty("label.slow.d.period");

		Label slowKPeriodLabel = new Label(slowKPeriodLabelString);
		slowKPeriodLabel.getStyleClass().add("white-text-color");

		TextField slowKPeriodField = new TextField();
		slowKPeriodField.setPromptText(slowKPeriodLabelString);

		Label fastKPeriodLabel = new Label(fastKPeriodLabelString);
		fastKPeriodLabel.getStyleClass().add("white-text-color");

		TextField fastKPeriodField = new TextField();
		fastKPeriodField.setPromptText(fastKPeriodLabelString);

		Label slowDPeriodLabel = new Label(slowDPeriodLabelString);
		slowDPeriodLabel.getStyleClass().add("white-text-color");

		TextField slowDPeriodField = new TextField();
		slowDPeriodField.setPromptText(slowDPeriodLabelString);

		ButtonType okButtonType = new ButtonType("Done", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().clear();
		dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

		grid.setPadding(new Insets(5));
		grid.setHgap(5);
		grid.setVgap(5);

		GridPane.setHalignment(fastKPeriodLabel, HPos.LEFT);
		grid.add(fastKPeriodLabel, 0, 0);
		GridPane.setHalignment(fastKPeriodField, HPos.RIGHT);
		grid.add(fastKPeriodField, 1, 0);

		GridPane.setHalignment(slowKPeriodLabel, HPos.LEFT);
		grid.add(slowKPeriodLabel, 0, 1);
		GridPane.setHalignment(slowKPeriodField, HPos.RIGHT);
		grid.add(slowKPeriodField, 1, 1);

		GridPane.setHalignment(slowDPeriodLabel, HPos.LEFT);
		grid.add(slowDPeriodLabel, 0, 2);
		GridPane.setHalignment(slowDPeriodField, HPos.RIGHT);
		grid.add(slowDPeriodField, 1, 2);

		dialog.getDialogPane().setContent(grid);
		dialog.setResultConverter(new Callback<ButtonType, PeriodBean>() {
			@Override
			public PeriodBean call(ButtonType b) {
				if (b == okButtonType) {
					return new PeriodBean(fastKPeriodField.getText().toString(), slowKPeriodField.getText().toString(), slowDPeriodField.getText().toString());
				}
				return null;
			}
		});
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				fastKPeriodField.requestFocus();
			}
		});
		Optional<PeriodBean> result = dialog.showAndWait();

		if(result.isPresent()) {
			if(validateSTOCHParamsBean(result.get())) {
				return result.get();
			} else {
				String invalidParameterMessage = Constants.applicationLanguage.getProperty("invalid.parameter.values");
				showErrorDialog(invalidParameterMessage, invalidParameterMessage, null);
				showSTOCHParamsDialog();
			}
		}
		return null;
	}

	public Dialog<PeriodBean> getDialog() {
		return dialog;
	}

	public void setDialog(Dialog<PeriodBean> dialog) {
		this.dialog = dialog;
	}
}
