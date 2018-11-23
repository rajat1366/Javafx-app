package com.fousalert.utils;

import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import org.controlsfx.control.RangeSlider;

import com.fousalert.bean.F1FilterBean;

public class F1FilterDialog {

	private Dialog<F1FilterBean> dialog = new Dialog<F1FilterBean>();

	public F1FilterDialog() {
		super();
		initializeDialog();
	}

	private void initializeDialog() {
		dialog.setHeaderText(null);
		dialog.setContentText(null);
		dialog.getDialogPane().getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
		dialog.getDialogPane().getStyleClass().add("dark-background");
		dialog.initStyle(StageStyle.UTILITY);
		dialog.getDialogPane().setOpacity(0.85);
		dialog.getDialogPane().setMinSize(500, 80);
		((Stage)dialog.getDialogPane().getScene().getWindow()).setResizable(false);
		
		Stage parentStage = (Stage) Context.getContext().get(Constants.PARENT_STAGE);
		Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();	
		dialogStage.initOwner(parentStage);
		dialogStage.initModality(Modality.WINDOW_MODAL);
	}

	public F1FilterBean showDialogBox(F1FilterBean f1FilterBean) {
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(30));
		grid.setHgap(5);
		grid.setVgap(5);
		grid.setMinSize(400, 200);

		Label priceLabel = new Label(Constants.applicationLanguage.getProperty("label.price"));
		priceLabel.getStyleClass().add("white-text-color");
		priceLabel.setMinWidth(50);

		Label minPriceLabel = new Label(Double.toString(Constants.PRICE_MIN));
		minPriceLabel.getStyleClass().add("white-text-color");
		minPriceLabel.setMinWidth(100);
		minPriceLabel.setMaxWidth(100);
		minPriceLabel.setAlignment(Pos.CENTER_RIGHT);

		RangeSlider priceRangeSlider = new RangeSlider();
		priceRangeSlider.setMin(Constants.PRICE_MIN);
		priceRangeSlider.setMax(Constants.PRICE_MAX);
		priceRangeSlider.setHighValue(f1FilterBean.getPriceMax());
		priceRangeSlider.setShowTickMarks(false);
		priceRangeSlider.setShowTickLabels(false);
		priceRangeSlider.setOrientation(Orientation.HORIZONTAL);
		priceRangeSlider.setMinWidth(300);
		priceRangeSlider.setMaxWidth(300);
		priceRangeSlider.getStyleClass().add("range-slider");

		Label maxPriceLabel = new Label(Double.toString(Constants.PRICE_MAX));
		maxPriceLabel.getStyleClass().add("white-text-color");
		maxPriceLabel.setMinWidth(100);
		maxPriceLabel.setMaxWidth(100);

		minPriceLabel.textProperty().bind(
				priceRangeSlider.lowValueProperty().asString());
		maxPriceLabel.textProperty().bind(
				priceRangeSlider.highValueProperty().asString());

		// First name label
		GridPane.setHalignment(priceLabel, HPos.LEFT);
		grid.add(priceLabel, 0, 0);

		GridPane.setHalignment(minPriceLabel, HPos.RIGHT);
		grid.add(minPriceLabel, 1, 0);

		GridPane.setHalignment(priceRangeSlider, HPos.RIGHT);
		grid.add(priceRangeSlider, 2, 0);

		GridPane.setHalignment(maxPriceLabel, HPos.RIGHT);
		grid.add(maxPriceLabel, 3, 0);

		Label volumeLabel = new Label(Constants.applicationLanguage.getProperty("label.volume"));
		volumeLabel.getStyleClass().add("white-text-color");
		volumeLabel.setMinWidth(50);

		Label minVolumeLabel = new Label(Double.toString(Constants.VOLUME_MIN));
		minVolumeLabel.getStyleClass().add("white-text-color");
		minVolumeLabel.setMinWidth(100);
		minVolumeLabel.setMaxWidth(100);
		minVolumeLabel.setAlignment(Pos.CENTER_RIGHT);

		RangeSlider volumeRangeSlider = new RangeSlider();
		volumeRangeSlider.setMin(Constants.VOLUME_MIN);
		volumeRangeSlider.setMax(Constants.VOLUME_MAX);
		volumeRangeSlider.setHighValue(f1FilterBean.getVolumeMax());
		volumeRangeSlider.setShowTickMarks(false);
		volumeRangeSlider.setShowTickLabels(false);
		volumeRangeSlider.setOrientation(Orientation.HORIZONTAL);
		volumeRangeSlider.setMinWidth(300);
		volumeRangeSlider.setMaxWidth(300);
		volumeRangeSlider.getStyleClass().add("range-slider");

		Label maxVolumeLabel = new Label(Double.toString(Constants.VOLUME_MAX));
		maxVolumeLabel.getStyleClass().add("white-text-color");
		maxVolumeLabel.setMinWidth(100);
		maxVolumeLabel.setMaxWidth(100);

		minVolumeLabel.textProperty().bind(
				volumeRangeSlider.lowValueProperty().asString());
		maxVolumeLabel.textProperty().bind(
				volumeRangeSlider.highValueProperty().asString());

		GridPane.setHalignment(volumeLabel, HPos.LEFT);
		grid.add(volumeLabel, 0, 1);

		GridPane.setHalignment(minVolumeLabel, HPos.RIGHT);
		grid.add(minVolumeLabel, 1, 1);

		GridPane.setHalignment(volumeRangeSlider, HPos.RIGHT);
		grid.add(volumeRangeSlider, 2, 1);

		GridPane.setHalignment(maxVolumeLabel, HPos.RIGHT);
		grid.add(maxVolumeLabel, 3, 1);

		ButtonType okButtonType = new ButtonType(Constants.applicationLanguage.getProperty("button.done"), ButtonData.OK_DONE);
		ButtonType resetButtonType = new ButtonType(Constants.applicationLanguage.getProperty("button.reset"));
		ButtonType cancelButtonType = ButtonType.CANCEL;

		dialog.getDialogPane().getButtonTypes().addAll(okButtonType, resetButtonType, cancelButtonType);

		Button okButton = ((Button) dialog.getDialogPane().lookupButton(okButtonType));
		Button resetButton = ((Button) dialog.getDialogPane().lookupButton(resetButtonType));
		Button cancelButton = ((Button) dialog.getDialogPane().lookupButton(cancelButtonType));		
		
		okButton.getStyleClass().addAll("f1-filter-dialog-done-button", "white-text-color");
		resetButton.getStyleClass().addAll("f1-filter-dialog-reset-button", "white-text-color");
		cancelButton.getStyleClass().add("f1-filter-dialog-cancel-button");

		HBox.setHgrow(priceRangeSlider, Priority.ALWAYS);
		HBox.setHgrow(volumeRangeSlider, Priority.ALWAYS);

		dialog.getDialogPane().setContent(grid);
		dialog.setResultConverter(new Callback<ButtonType, F1FilterBean>() {
			@Override
			public F1FilterBean call(ButtonType buttonType) {
				if (buttonType == okButtonType) {
					f1FilterBean.setPriceMin(priceRangeSlider.getLowValue());
					f1FilterBean.setPriceMax(priceRangeSlider.getHighValue());
					f1FilterBean.setVolumeMin(volumeRangeSlider.getLowValue());
					f1FilterBean.setVolumeMax(volumeRangeSlider.getHighValue());
					return f1FilterBean;
				} else if (buttonType == resetButtonType) {
					f1FilterBean.setPriceMin(Constants.PRICE_MIN);
					f1FilterBean.setPriceMax(Constants.PRICE_MAX);
					f1FilterBean.setVolumeMin(Constants.VOLUME_MIN);
					f1FilterBean.setVolumeMax(Constants.VOLUME_MAX);
					return f1FilterBean;
				} else {
					return f1FilterBean;
				}
			}
		});

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				dialog.getDialogPane().requestFocus();
			}
		});
		priceRangeSlider.setLowValue(f1FilterBean.getPriceMin());
		volumeRangeSlider.setLowValue(f1FilterBean.getVolumeMin());
		Optional<F1FilterBean> result = dialog.showAndWait();

		if (result.isPresent()) {
			return result.get();
		}

		return null;
	}
}
