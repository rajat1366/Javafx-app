package com.fousalert.controller.dialog;

import com.fousalert.bean.TrendLineParamBean;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TrendlineConfigDialogViewController {
	@FXML private CheckBox soundCheckBox;
	@FXML private CheckBox popupWindowCheckBox;
	@FXML private RadioButton crossingAboveRadioButton;
	@FXML private RadioButton crossingDownRadioButton;
	@FXML private RadioButton crossingAboveAndDownRadioButton;
	@FXML private CheckBox flashingTextCheckBox;
	@FXML private Hyperlink cancelButton;
	@FXML private Button saveButton;
	@FXML private TextArea flashingTextBox;
	@FXML private Slider lineWidthSlider;
	@FXML private ColorPicker lineColorPicker;
	@FXML private ToggleGroup alertStyleToggleGroup;
	private boolean isCancelButtonClicked = true;
	
	@FXML public void onCancelButtonClicked(ActionEvent event) {
		isCancelButtonClicked = true;
		close();
	}

	public TrendLineParamBean getTrendlineParams(TrendLineParamBean trendlineParams) {
		if(isCancelButtonClicked) {
			return null;
		}
		trendlineParams.setAlertOnlyCrossAbove(crossingAboveRadioButton.isSelected());
		trendlineParams.setAlertOnlyCrossDown(crossingDownRadioButton.isSelected());
		if(flashingTextCheckBox.isSelected()) {
			trendlineParams.setAlertText(flashingTextBox.getText());
		} else {
			trendlineParams.setAlertText(null);
		}
		trendlineParams.setCheckCrossingAboveAndDown(crossingAboveAndDownRadioButton.isSelected());
		trendlineParams.setPlaySound(soundCheckBox.isSelected());
		trendlineParams.setShowPopup(popupWindowCheckBox.isSelected());
		trendlineParams.setLineColor("#"+lineColorPicker.getValue().toString().substring(2,8));
		trendlineParams.setHeight(lineWidthSlider.getValue());
		
		return trendlineParams;
	}

	@FXML public void onSaveButtonClicked(ActionEvent event) {
		isCancelButtonClicked = false;
		close();
	}
	
	@FXML private void close() {
		Stage stage = (Stage)saveButton.getScene().getWindow();
		stage.close();
	}

	public void setLineWidth(double lineWidth) {
		this.lineWidthSlider.setValue(lineWidth);
	}

	public void setLineColor(String lineColor) {
		this.lineColorPicker.setValue(Color.valueOf(lineColor));
	}

	public void setSoundRadioButton(boolean isSoundRadioButton) {
		this.soundCheckBox.setSelected(isSoundRadioButton);
	}

	public void setPopupWindowRadioButton(boolean isPopupWindowRadioButton) {
		this.popupWindowCheckBox.setSelected(isPopupWindowRadioButton);
	}

	public void setCrossingAboveCheckBox(boolean isCrossingAboveCheckBox) {
		this.crossingAboveRadioButton.setSelected(isCrossingAboveCheckBox);
	}

	public void setCrossingDownCheckBox(boolean isCrossingDownCheckBox) {
		this.crossingDownRadioButton.setSelected(isCrossingDownCheckBox);
	}

	public void setCrossingAboveAndDownCheckBox(boolean isCrossingAboveAndDownCheckBox) {
		this.crossingAboveAndDownRadioButton.setSelected(isCrossingAboveAndDownCheckBox);
	}

	public void setFlashingTextRadioButton(boolean isFlashingTextRadioButton) {
		this.flashingTextCheckBox.setSelected(isFlashingTextRadioButton);
	}
	
	public boolean getFlashingTextRadioButton() {
		return flashingTextCheckBox.isSelected();
	}

	public void setFlashingTextBox(String flashingTextBoxContent) {
		this.flashingTextBox.setText(flashingTextBoxContent);
	}
}