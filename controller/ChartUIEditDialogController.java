package com.fousalert.controller;


import java.util.List;

import com.fousalert.bean.ChartPreferenceBean;
import com.fousalert.layout.LayoutNode;
import com.fousalert.utils.Constants;
import com.google.gson.Gson;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class ChartUIEditDialogController {
	// general tab
	@FXML private ColorPicker bgColorPicker;
	@FXML private ColorPicker profitCandleColorPicker;
	@FXML private ColorPicker lossCandleColorPicker;
	@FXML private ColorPicker profitCandleBorderColorPicker;
	@FXML private ColorPicker lossCandleBorderColorPicker;
	@FXML private ColorPicker profitWickColorPicker;
	@FXML private ColorPicker lossWickColorPicker;
	@FXML private ColorPicker tickColorPicker;
	@FXML private ColorPicker tickTextColorPicker;

	// Indicator tab
	@FXML private ColorPicker rsiStrokeColorPicker;
	@FXML private ColorPicker emaStrokeColorPicker;
	@FXML private ColorPicker smaStrokeColorPicker;
	@FXML private ColorPicker stochStrokeColorPicker;
	@FXML private ColorPicker macdStrokeColorPicker;
	@FXML private ColorPicker vmaProfitColorPicker;
	@FXML private ColorPicker vmaLossColorPicker;
	@FXML private Slider rsiStrokeWidthSlider;
	@FXML private Slider emaStrokeWidthSlider;
	@FXML private Slider smaStrokeWidthSlider;
	@FXML private Slider stochStrokeWidthSlider;
	@FXML private Slider macdStrokeWidthSlider;

	@FXML private Button applyButton;

	private DashboardController dashboardController;
	private List<LayoutNode> layoutNodes;
	
	public ChartUIEditDialogController() {}
	
	public void setLayoutNode(List<LayoutNode> layoutNodes) {
		this.layoutNodes = layoutNodes;
	}

	public DashboardController getDashboardController() {
		return dashboardController;
	}
	public void setDashboardController(DashboardController dashboardController) {
		this.dashboardController = dashboardController;
	}
	
	@FXML private void initialize(){
		applyButton.requestFocus();
		initializeStrokeSlider(rsiStrokeWidthSlider);
		initializeStrokeSlider(emaStrokeWidthSlider);
		initializeStrokeSlider(smaStrokeWidthSlider);
		initializeStrokeSlider(stochStrokeWidthSlider);
		initializeStrokeSlider(macdStrokeWidthSlider);

	};

	@FXML private void  changeChartUI(){
		ChartPreferenceBean chartPreferences = new ChartPreferenceBean("#"+bgColorPicker.getValue().toString().substring(2,8),
				"#"+profitCandleColorPicker.getValue().toString().substring(2,8), 
				"#"+lossCandleColorPicker.getValue().toString().substring(2,8), 
				"#"+profitCandleBorderColorPicker.getValue().toString().substring(2,8), 
				"#"+lossCandleBorderColorPicker.getValue().toString().substring(2,8), 
				"#"+profitWickColorPicker.getValue().toString().substring(2,8), 
				"#"+lossWickColorPicker.getValue().toString().substring(2,8), 
				"#"+tickColorPicker.getValue().toString().substring(2,8),
				"#"+tickTextColorPicker.getValue().toString().substring(2,8),
				"#"+rsiStrokeColorPicker.getValue().toString().substring(2,8), 
				"#"+emaStrokeColorPicker.getValue().toString().substring(2,8),
				"#"+smaStrokeColorPicker.getValue().toString().substring(2,8), 
				"#"+stochStrokeColorPicker.getValue().toString().substring(2,8), 
				"#"+macdStrokeColorPicker.getValue().toString().substring(2,8), 
				"#"+vmaProfitColorPicker.getValue().toString().substring(2,8), 
				"#"+vmaLossColorPicker.getValue().toString().substring(2,8), 
				rsiStrokeWidthSlider.getValue(), emaStrokeWidthSlider.getValue(),
				smaStrokeWidthSlider.getValue(), stochStrokeWidthSlider.getValue(), 
				macdStrokeWidthSlider.getValue());
			
		for (LayoutNode layoutNode : layoutNodes) {
			layoutNode.getChartConfig().setChartParams(chartPreferences);
			dashboardController.changeChartUIParameters(chartPreferences, layoutNode.getChartUID());
		}
		close();
	}
	
	@FXML private void setDefaultSetting() {
		String chartPreferencesJson = Constants.applicationProperties.getProperty("chart.ui.default.properties");
		Gson gson = new Gson();
		ChartPreferenceBean chartPreferences = gson.fromJson(chartPreferencesJson,ChartPreferenceBean.class);
		for (LayoutNode layoutNode : layoutNodes) {
			layoutNode.getChartConfig().setChartParams(chartPreferences);
			dashboardController.changeChartUIParameters(chartPreferences, layoutNode.getChartUID());
		}
		close();
	}

	@FXML private void close(){
		Stage stage = (Stage) bgColorPicker.getScene().getWindow();
		stage.close();
	}
	
	public void setUserValuesToColorPicker() {
		ChartPreferenceBean chartPreferences = layoutNodes.get(0).getChartConfig().getChartParams();
		
		bgColorPicker.setValue(Color.web(chartPreferences.getBgColor()));
		profitCandleColorPicker.setValue(Color.web(chartPreferences.getProfitCandleColor()));
		lossCandleColorPicker.setValue(Color.web(chartPreferences.getLossCandleColor()));
		profitCandleBorderColorPicker.setValue(Color.web(chartPreferences.getProfitCandleBorderColor()));
		lossCandleBorderColorPicker.setValue(Color.web(chartPreferences.getLossCandleBorderColor()));
		profitWickColorPicker.setValue(Color.web(chartPreferences.getProfitWickColor()));
		lossWickColorPicker.setValue(Color.web(chartPreferences.getLossWickColor()));
		tickColorPicker.setValue(Color.web(chartPreferences.getTickColor()));
		tickTextColorPicker.setValue(Color.web(chartPreferences.getTickTextColor()));

		rsiStrokeColorPicker.setValue(Color.web(chartPreferences.getRsiStrokeColor()));
		emaStrokeColorPicker.setValue(Color.web(chartPreferences.getEmaStrokeColor()));
		smaStrokeColorPicker.setValue(Color.web(chartPreferences.getSmaStrokeColor()));
		stochStrokeColorPicker.setValue(Color.web(chartPreferences.getStochStrokeColor()));
		macdStrokeColorPicker.setValue(Color.web(chartPreferences.getMacdStrokeColor()));
		vmaProfitColorPicker.setValue(Color.web(chartPreferences.getVmaProfitColor()));
		vmaLossColorPicker.setValue(Color.web(chartPreferences.getVmaLossColor()));

		rsiStrokeWidthSlider.setValue(chartPreferences.getRsiStrokeWidth());
		emaStrokeWidthSlider.setValue(chartPreferences.getEmaStrokeWidth());
		smaStrokeWidthSlider.setValue(chartPreferences.getSmaStrokeWidth());
		stochStrokeWidthSlider.setValue(chartPreferences.getStochStrokeWidth());
		macdStrokeWidthSlider.setValue(chartPreferences.getMacdStrokeWidth());
	}
	
	private void initializeStrokeSlider(Slider WidthSlider) {
		WidthSlider.setMin(1);
		WidthSlider.setMax(5);
	}
}