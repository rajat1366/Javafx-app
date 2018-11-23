package com.fousalert.customfx;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.fousalert.bean.AnnotationParamBean;
import com.fousalert.bean.ChartConfig;
import com.fousalert.bean.IntervalComboBoxBean;
import com.fousalert.bean.PeriodBean;
import com.fousalert.bean.TrendLineParamBean;
import com.fousalert.controller.ChartUIEditDialogController;
import com.fousalert.controller.DashboardController;
import com.fousalert.controller.dialog.PrePostDataDialogController;
import com.fousalert.layout.LayoutNode;
import com.fousalert.utils.ChartGroupUtil;
import com.fousalert.utils.Constants;
import com.fousalert.utils.Constants.IndicatorType;
import com.fousalert.utils.Constants.TickerDuration;
import com.fousalert.utils.Context;
import com.fousalert.utils.IndicatorDialog;
import com.google.gson.Gson;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class ChartTitleBar extends DockTitleBar implements Serializable {
	private static final long serialVersionUID = 5569813594772759367L;
	private Button bulbButton;
	private Button settingButton;
	private Button lineButton;
	private Button textButton;
	private Button moveWindowButton;
	private Button listButton;
	private Button closeButton;
	private String tickerCode;
	private DashboardController dashboardController;
	
	private ComboBox<String> tickerDropDown;
	private ComboBox<IntervalComboBoxBean> timeDurationDropDown;
	private ComboBox<String> indicatorDropDown;

	public ChartTitleBar(String title, DashboardController dashboardController) {
		super(title);
		this.getStyleClass().add("chart-title-bar");

		timeDurationDropDown = new ComboBox<IntervalComboBoxBean>();

		
		
		timeDurationDropDown.getItems().add(new IntervalComboBoxBean(TickerDuration.ONE_MIN.getTickerDuration()+Constants.MINUTE, TickerDuration.ONE_MIN.getMinutes()));
		timeDurationDropDown.getItems().add(new IntervalComboBoxBean(TickerDuration.TWO_MIN.getTickerDuration()+Constants.MINUTE, TickerDuration.TWO_MIN.getMinutes()));
		timeDurationDropDown.getItems().add(new IntervalComboBoxBean(TickerDuration.FIVE_MIN.getTickerDuration()+Constants.MINUTE, TickerDuration.FIVE_MIN.getMinutes()));
		timeDurationDropDown.getItems().add(new IntervalComboBoxBean(TickerDuration.TEN_MIN.getTickerDuration()+Constants.MINUTE, TickerDuration.TEN_MIN.getMinutes()));
		timeDurationDropDown.getItems().add(new IntervalComboBoxBean(TickerDuration.FIFTEEN_MIN.getTickerDuration()+Constants.MINUTE, TickerDuration.FIFTEEN_MIN.getMinutes()));
		timeDurationDropDown.getItems().add(new IntervalComboBoxBean(TickerDuration.THIRTY_MIN.getTickerDuration()+Constants.MINUTE, TickerDuration.THIRTY_MIN.getMinutes()));
		timeDurationDropDown.getItems().add(new IntervalComboBoxBean(TickerDuration.HOUR.getTickerDuration()+Constants.HOUR, TickerDuration.HOUR.getMinutes()));
		timeDurationDropDown.getItems().add(new IntervalComboBoxBean(TickerDuration.DAILY.getTickerDuration(), TickerDuration.DAILY.getMinutes()));

		setupTitleBarChartButtons();
		
		this.dashboardController = dashboardController;
	}
	
	public DashboardController getDashboardController() {
		return dashboardController;
	}
	public void setDashboardController(DashboardController dashboardController) {
		this.dashboardController = dashboardController;
	}

	public void setTickerCode(String tickerCode) {
		tickerDropDown.getSelectionModel().select(tickerDropDown.getItems().indexOf(tickerCode));
		this.tickerCode = tickerCode;
	}

	private void setupTitleBarChartButtons() {
		tickerDropDown = new ComboBox<String>();
		tickerDropDown.getStyleClass().add("dashboard-chart-ticker-name");

		indicatorDropDown = new ComboBox<String>();
		bulbButton = new Button();
		settingButton = new Button();
		moveWindowButton = new Button();
		listButton = new Button();
		closeButton = new Button();
		lineButton = new Button();
		textButton = new Button();

		listButton.getStyleClass().add("strange-icon");
		bulbButton.getStyleClass().add("bulb-icon");
		settingButton.getStyleClass().add("setting-icon");
		moveWindowButton.getStyleClass().add("move-window-icon");
		closeButton.getStyleClass().add("close-button-icon");
		lineButton.getStyleClass().add("line-icon");
		textButton.getStyleClass().add("text-icon");
		textButton.setDisable(true);

		moveWindowButton.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
		moveWindowButton.addEventHandler(MouseEvent.DRAG_DETECTED, this);
		moveWindowButton.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
		moveWindowButton.addEventHandler(MouseEvent.MOUSE_RELEASED, this);

		lineButton.setOnAction(event -> {
			WebView webView = (WebView) dockNode.getChildren().get(1);
			webView.getEngine().executeScript("historicalChart.customLineDraw(1)");
		});
		textButton.setOnAction(event -> {
			WebView webView = (WebView) dockNode.getChildren().get(1);
			webView.getEngine().executeScript("historicalChart.customAddText(1)");
		});
		settingButton.setOnAction(chartUIHandler);
		listButton.setOnAction(listButtonHandler);

		HBox closeButtonHBox = new HBox(0, moveWindowButton, closeButton);
		HBox emptyHBox = new HBox();
		HBox.setHgrow(emptyHBox, Priority.ALWAYS);
		closeButtonHBox.setMaxWidth(20);
		ToolBar titlebarContainer = new ToolBar();
		titlebarContainer.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
		titlebarContainer.addEventHandler(MouseEvent.DRAG_DETECTED, this);
		titlebarContainer.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
		titlebarContainer.addEventHandler(MouseEvent.MOUSE_RELEASED, this);
		titlebarContainer.getItems().addAll(tickerDropDown, timeDurationDropDown, indicatorDropDown, lineButton, textButton, listButton, bulbButton, settingButton, emptyHBox, moveWindowButton, closeButton);
		this.getChildren().addAll(titlebarContainer);
		titlebarContainer.getStyleClass().addAll("button-background");
		titlebarContainer.setPadding(new Insets(0,5,0,0));
		closeButton.setMaxSize(11, 11);
		tickerDropDown.setMinWidth(75.0);
		tickerDropDown.setMaxWidth(75.0);
		tickerDropDown.getItems().addAll(tickerAsStringList);
		indicatorDropDown.setMaxWidth(80.0);
		indicatorDropDown.setMinWidth(80.0);
		indicatorDropDown.getStyleClass().add("title-bar-dropdown");
		timeDurationDropDown.setMinWidth(70.0);
		timeDurationDropDown.setMaxWidth(70.0);
		timeDurationDropDown.getSelectionModel().selectFirst();
		timeDurationDropDown.setEditable(false);
		
		timeDurationDropDown.setCellFactory(new Callback<ListView<IntervalComboBoxBean>, ListCell<IntervalComboBoxBean>>() {
			@Override
			public ListCell<IntervalComboBoxBean> call(ListView<IntervalComboBoxBean> param) {
				return new ListCell<IntervalComboBoxBean>() {
					@Override
					protected void updateItem(IntervalComboBoxBean interval, boolean empty) {
						super.updateItem(interval, empty);
						if (interval != null) {
							setText(interval.getLabel());
						} else {
							setText("");
						}
						setGraphic(null);
					}
				};
			}
		});
		
		StackPane.setAlignment(closeButtonHBox, Pos.CENTER_RIGHT);

		timeDurationComboBoxHandlers();
		registerTickerComboBoxHandlers();
		initializeIndicatorDropDown();
	}

	private EventHandler<ActionEvent> listButtonHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PrePostDataDialog.fxml"));
			try {
				Parent root = (Parent) loader.load();
				Scene scene = new Scene(root);
				Stage parentStage = (Stage)Context.getContext().get(Constants.PARENT_STAGE);
				Stage stage = new Stage(StageStyle.TRANSPARENT);
				stage.setScene(scene);
				stage.initOwner(parentStage);
				Bounds bounds = listButton.localToScreen(listButton.getBoundsInLocal());
				stage.setX(bounds.getMinX() + 2);
				stage.setY(bounds.getMinY() + 25);

				stage.focusedProperty().addListener(new InvalidationListener() {
					@Override
					public void invalidated(Observable observable) {
						if(!stage.isFocused()) {
							stage.close();
						}
					}
				});
				
				stage.show();
				
				PrePostDataDialogController controller = loader.getController();
				controller.setStage(stage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	private EventHandler<ActionEvent> chartUIHandler = new EventHandler<ActionEvent>(){	
		@Override			
		public void handle(ActionEvent event) {			
			try{			
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/chartUIEditDialog.fxml"));			
				AnchorPane page = (AnchorPane) loader.load();			
				Stage dialogStage = new Stage();			
				dialogStage.setTitle("Admin settings");			
				dialogStage.initModality(Modality.WINDOW_MODAL);			
				dialogStage.initOwner((Stage)Context.getContext().get(Constants.PARENT_STAGE));			
				Scene scene = new Scene(page);			
				scene.getStylesheets().add(getClass().getResource("/dialogBox.css").toExternalForm());			
				dialogStage.setScene(scene);			
				ChartUIEditDialogController chartUIController = loader.getController();		
				List<LayoutNode> layoutNodes = new ArrayList<LayoutNode>();
				layoutNodes.add(dockNode.getLayoutNode());
				chartUIController.setLayoutNode(layoutNodes);
				chartUIController.setDashboardController(dashboardController);
				chartUIController.setUserValuesToColorPicker();
				dialogStage.showAndWait();	
			} catch(Exception e) {			
				e.printStackTrace();			
			}			
		}			
	};
	
	private void initializeIndicatorDropDown() {
		List<String> indicatorList = new ArrayList<String>();
		for(IndicatorType indicator : IndicatorType.values())
			indicatorList.add(indicator.getIndicatorType().toUpperCase());

		indicatorDropDown.getItems().addAll(indicatorList);
		indicatorDropDown.getItems().add(0, "Indicator");
		indicatorDropDown.getSelectionModel().select(0);

		indicatorDropDown.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String indicatorSelectedValue = indicatorDropDown.getValue();
				WebView webView = (WebView) dockNode.getChildren().get(1);
				IndicatorDialog dialog = new IndicatorDialog();
				dialog.getDialog().getDialogPane().requestFocus();
				if((indicatorSelectedValue != null) && !indicatorSelectedValue.isEmpty()) {
					PeriodBean periodBean = null;
					if(indicatorSelectedValue.equalsIgnoreCase(Constants.IndicatorType.EMA.toString()) || indicatorSelectedValue.equalsIgnoreCase(Constants.IndicatorType.SMA.toString())) {
						periodBean = dialog.showDialogBox(indicatorSelectedValue);						
					} else {
						if(indicatorSelectedValue.equalsIgnoreCase(Constants.IndicatorType.MACD.toString())) {
							String slowPeriod = Constants.applicationProperties.getProperty("indicator.macd.slowPeriod");
							String fastPeriod = Constants.applicationProperties.getProperty("indicator.macd.fastPeriod");
							String signalPeriod = Constants.applicationProperties.getProperty("indicator.macd.signalPeriod");
							periodBean = new PeriodBean(null, slowPeriod, fastPeriod, signalPeriod);
						} else if(indicatorSelectedValue.equalsIgnoreCase(Constants.IndicatorType.STOCH.toString())) {
							String fastKPeriod = Constants.applicationProperties.getProperty("indicator.stoch.fastKPeriod");
							String slowKPeriod = Constants.applicationProperties.getProperty("indicator.stoch.slowKPeriod");
							String slowDPeriod = Constants.applicationProperties.getProperty("indicator.stoch.slowDPeriod");
							periodBean = new PeriodBean(fastKPeriod, slowKPeriod, slowDPeriod);
						} else if(indicatorSelectedValue.equalsIgnoreCase(Constants.IndicatorType.RSI.toString()) || indicatorSelectedValue.equalsIgnoreCase(Constants.IndicatorType.VMA.toString())) {
							String period = Constants.applicationProperties.getProperty("indicator." + indicatorSelectedValue.toLowerCase() + ".period");
							periodBean = new PeriodBean(period, null, null, null);
						}
					}
					Gson gson = new Gson();
					if(periodBean != null) {
						webView.getEngine().executeScript("historicalChart.getIndicatorValue('"+gson.toJson(periodBean)+"') " );						
						webView.getEngine().executeScript("historicalChart.addGraph('First', '"+indicatorSelectedValue+"');");
					}
				}
				indicatorDropDown.getSelectionModel().select(0);
			}
		});
	}

	private void timeDurationComboBoxHandlers() {
		timeDurationDropDown.setOnAction(new EventHandler<ActionEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handle(ActionEvent event) {
				IntervalComboBoxBean selectedInterval = timeDurationDropDown.getValue();
				String timeDurationSelectedValue = selectedInterval.getInterval();
				WebView webView = (WebView)dockNode.getChildren().get(1);

				Map<Integer, ChartConfig> chartConfigs = (Map<Integer, ChartConfig>)Context.getContext().get(Constants.CHART_CONFIG_MAP_KEY);
				ChartConfig selectedChartConfig = chartConfigs.get(Integer.parseInt(webView.getId()));

				if(timeDurationSelectedValue.equalsIgnoreCase("Daily")) {
					webView.getEngine().executeScript("historicalChart.getData('anything',1,2,null,null,2,'"+tickerDropDown.getValue()+"','First',null,0)");
					selectedChartConfig.setApiType(Constants.API_TYPE.INTER_DAY.getType());
				} else {
					webView.getEngine().executeScript("historicalChart.getData('anything',"+timeDurationSelectedValue + ",1,null,null,1,'"+tickerDropDown.getValue()+"','First',null,2)");
					selectedChartConfig.setDataDuration(Integer.parseInt(String.valueOf(timeDurationSelectedValue)));
					selectedChartConfig.setApiType(Constants.API_TYPE.INTRA_DAY.getType());
				}
			}
		});
	}

	private void registerTickerComboBoxHandlers() {
		tickerDropDown.setOnMouseClicked(event -> {
			if(tickerDropDown.isEditable()) {
				tickerDropDown.setEditable(false);
				tickerDropDown.getSelectionModel().select(tickerDropDown.getItems().indexOf(tickerCode));
			} else {
				tickerDropDown.getEditor().setText("");
				tickerDropDown.setEditable(true);
				tickerDropDown.getItems().clear();
				tickerDropDown.getItems().addAll(tickerAsStringList);
			}
		});

		tickerDropDown.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(Pattern.matches(Constants.TICKER_PATTERN_REGEX, event.getCode().toString()) || (event.getCode().equals(KeyCode.BACK_SPACE)) || (event.getCode().equals(KeyCode.DELETE))) {
					String value = tickerDropDown.getEditor().getText().toString();
					if((value != null) && (tickersList.size() != 0) && (Pattern.matches(Constants.TICKER_PATTERN_REGEX, value))) {
						tickerDropDown.getItems().clear();
						for(int i=0;i<tickersList.size();i++) {
							if(tickersList.get(i).getTickerCode().startsWith(value.toUpperCase()) || tickersList.get(i).getTickerName().toLowerCase().startsWith(value.toLowerCase())) {
								tickerDropDown.getItems().add(tickersList.get(i).getTickerCode());
							}
						}
					} else {
						tickerDropDown.getItems().clear();
						tickerDropDown.getItems().addAll(tickerAsStringList);
						tickerDropDown.getSelectionModel().select(tickerDropDown.getItems().indexOf(tickerCode));
					}
					tickerDropDown.show();
				} 
			}
		});

		tickerDropDown.setOnAction(new EventHandler<ActionEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handle(ActionEvent event) {
				String selectedValue = tickerDropDown.getValue();
				if((selectedValue != null) && !selectedValue.isEmpty() && Pattern.matches(Constants.TICKER_PATTERN_REGEX, selectedValue) && tickerDropDown.getItems().contains(selectedValue.toUpperCase())) {
					tickerDropDown.setEditable(false);
					tickerDropDown.getSelectionModel().select(tickerDropDown.getItems().indexOf(selectedValue.toUpperCase()));
					tickerCode = selectedValue.toUpperCase();
					WebView webView = (WebView) dockNode.getChildren().get(1);
					
					AnchorPane parentContainer = (AnchorPane)Context.getContext().get(Constants.PARENT_CONTAINER);
					Map<Integer, ChartConfig> chartConfigs = (Map<Integer, ChartConfig>)Context.getContext().get(Constants.CHART_CONFIG_MAP_KEY);
					ChartConfig selectedChartConfig = chartConfigs.get(Integer.parseInt(webView.getId()));
					
					if(!tickerCode.equals(selectedChartConfig.getTickerSymbol())){
					
						webView.getEngine().executeScript("historicalChart.changeTickerGraph('anything', '"+selectedValue.toUpperCase()+"', 'First', true,'"+timeDurationDropDown.getValue().getInterval()+"') " );
						dashboardController.removeEntryFromTickerTrendlineDataSet(Integer.toString(selectedChartConfig.getChartUID()));
						selectedChartConfig.setTickerSymbol(selectedValue.toUpperCase());
						selectedChartConfig.setTrendlineParams(new ArrayList<TrendLineParamBean>());
						selectedChartConfig.setAnnotationParams(new ArrayList<AnnotationParamBean>());
						WebView currentWebView = null;
						
						// parent container wala code will not work with floating nodes as parent container is not conatining floating nodes 
						// dockNode map se mil jaegi values
						List<ChartConfig> sameGroupChartList = ChartGroupUtil.getSameGroupChart(chartConfigs, selectedChartConfig);
						for(ChartConfig currentChartConfig : sameGroupChartList) {
							currentWebView = (WebView) parentContainer.lookup("#"+currentChartConfig.getChartUID());
							if(currentWebView != null){
								DockNode dockNode =(DockNode)currentWebView.getParent();
								ChartTitleBar chartTitleBar = (ChartTitleBar)dockNode.getDockTitleBar();
								currentWebView.getEngine().executeScript("historicalChart.changeTickerGraph('anything', '"+selectedValue.toUpperCase()+"', 'First', 'true','"+chartTitleBar.getTimeDurationDropDown().getValue().getInterval()+"') " );
								dashboardController.removeEntryFromTickerTrendlineDataSet(Integer.toString(selectedChartConfig.getChartUID()));
								chartTitleBar.getTickerDropDown().setValue(selectedChartConfig.getTickerSymbol());
								currentChartConfig.setTickerSymbol(selectedChartConfig.getTickerSymbol());
								currentChartConfig.setTrendlineParams(new ArrayList<TrendLineParamBean>());
								currentChartConfig.setAnnotationParams(new ArrayList<AnnotationParamBean>());
							}
						}
					}
					
				}
			}
		});

		tickerDropDown.focusedProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if(!tickerDropDown.isFocused()) {
					tickerDropDown.setEditable(false);
					tickerDropDown.getSelectionModel().select(tickerDropDown.getItems().indexOf(tickerCode));
				}
			}
		});
	}

	public ComboBox<IntervalComboBoxBean> getTimeDurationDropDown() {
		return timeDurationDropDown;
	}

	public void setTimeDurationDropDown(ComboBox<IntervalComboBoxBean> timeDurationDropDown) {
		this.timeDurationDropDown = timeDurationDropDown;
	}

	public ComboBox<String> getTickerDropDown() {
		return tickerDropDown;
	}

	public void setTickerDropDown(ComboBox<String> tickerDropDown) {
		this.tickerDropDown = tickerDropDown;
	}

	public Button getCloseButton() {
		return closeButton;
	}

	public String getTickerCode() {
		return tickerCode;
	}

	public Button getLineButton() {
		return lineButton;
	}

	public Button getTextButton() {
		return textButton;
	}
}