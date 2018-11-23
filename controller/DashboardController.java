package com.fousalert.controller;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.fousalert.application.interfaces.Closeable;
import com.fousalert.application.interfaces.StockDataReceivable;
import com.fousalert.bean.AbstractTickerCandlePriceHistory;
import com.fousalert.bean.AlertEditPopupBean;
import com.fousalert.bean.AlertPopupBean;
import com.fousalert.bean.AlertStock;
import com.fousalert.bean.AlertTicker;
import com.fousalert.bean.AlertTickerResponseBean;
import com.fousalert.bean.AnnotationParamBean;
import com.fousalert.bean.AnnotationRequest;
import com.fousalert.bean.CandlePriceHistoryResponseBean;
import com.fousalert.bean.ChartConfig;
import com.fousalert.bean.ChartPreferenceBean;
import com.fousalert.bean.EditEMARequest;
import com.fousalert.bean.F1FilterBean;
import com.fousalert.bean.F1Stock;
import com.fousalert.bean.GroupColorRequest;
import com.fousalert.bean.IndicatorParamBean;
import com.fousalert.bean.IntervalComboBoxBean;
import com.fousalert.bean.NewsBean;
import com.fousalert.bean.NewsResponse;
import com.fousalert.bean.PeriodBean;
import com.fousalert.bean.RemoveChartRequest;
import com.fousalert.bean.SocketCalculateDataObject;
import com.fousalert.bean.SocketDataCacheObject;
import com.fousalert.bean.SocketTickerDataObject;
import com.fousalert.bean.Stock;
import com.fousalert.bean.TickerSearchResponse;
import com.fousalert.bean.TrendLine;
import com.fousalert.bean.TrendLineParamBean;
import com.fousalert.bean.UITickerCalculatedSymbolData;
import com.fousalert.bean.UIUserGroupEntitlement;
import com.fousalert.calculationEngine.algo.indicator.CalculatedOutput;
import com.fousalert.calculationEngine.algo.indicator.UITickerCalculatedData;
import com.fousalert.commonconstants.UtilityConstants;
import com.fousalert.controller.dialog.TrendlineConfigDialogViewController;
import com.fousalert.convertor.AbstractBeanConverter;
import com.fousalert.customfx.ChartTitleBar;
import com.fousalert.customfx.CustomComboBox;
import com.fousalert.customfx.DockNode;
import com.fousalert.customfx.DockPane;
import com.fousalert.customfx.DockPos;
import com.fousalert.customfx.DockTitleBar;
import com.fousalert.customfx.F1ListTitleBar;
import com.fousalert.customfx.WatchTitleBar;
import com.fousalert.database.beans.UITicker;
import com.fousalert.database.benzinga.news.APINews;
import com.fousalert.database.entities.db2.TickerNews;
import com.fousalert.database.utils.DataStoreConstants.ResultStatus;
import com.fousalert.datastore.bean.LiveTicker;
import com.fousalert.datastore.storage.ListDataStore;
import com.fousalert.datastore.storage.impl.MemoryListDataStore;
import com.fousalert.layout.LayoutNode;
import com.fousalert.layout.ParentLayout;
import com.fousalert.restservice.LoginTemplate;
import com.fousalert.service.UserService;
import com.fousalert.utilities.CompressionUtility;
import com.fousalert.utils.AlertPopupEditDialog;
import com.fousalert.utils.ApplicationUtil;
import com.fousalert.utils.CalculationUtil;
import com.fousalert.utils.ChartGroupUtil;
import com.fousalert.utils.ConnectionLostDialog;
import com.fousalert.utils.Constants;
import com.fousalert.utils.Constants.AlertDirection;
import com.fousalert.utils.Constants.DataFlowType;
import com.fousalert.utils.Constants.DockPanePosition;
import com.fousalert.utils.Constants.Entitlements;
import com.fousalert.utils.Constants.IndicatorType;
import com.fousalert.utils.Constants.SideDockNodeTitle;
import com.fousalert.utils.Constants.TickerDuration;
import com.fousalert.utils.Context;
import com.fousalert.utils.DateUtil;
import com.fousalert.utils.F1FilterDialog;
import com.fousalert.utils.IndicatorDialog;
import com.fousalert.utils.InternalSocketClient;
import com.fousalert.utils.InternalSocketServer;
import com.fousalert.utils.JSONToStockMapper;
import com.fousalert.utils.NumberUtil;
import com.fousalert.utils.SplitTickerCandleUpdator;
import com.fousalert.utils.StreamerNettyClient;
import com.fousalert.utils.TickerDataUtil;
import com.fousalert.utils.WSClient;
import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import io.netty.util.internal.ConcurrentSet;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;

public class DashboardController implements Closeable, StockDataReceivable {

	private Map<String, List<TrendLineParamBean>> alertTrendlineBeanMap = new HashMap<String, List<TrendLineParamBean>>();
	private Map<String, List<TrendLineParamBean>> alertHorizontalTrendlineBeanMap = new HashMap<String, List<TrendLineParamBean>>();
	private Set<String> newsBag = new HashSet<String>();

	private List<AbstractTickerCandlePriceHistory> tickerDataList = new ArrayList<AbstractTickerCandlePriceHistory>();
	private Map<String, SocketDataCacheObject> tickerDataMapIntra = new HashMap<>();
	private Map<String, SocketDataCacheObject> tickerDataMapInter = new HashMap<>();
	private SplitTickerCandleUpdator tickerCandleUpdator = new SplitTickerCandleUpdator();
	//	private Set<String> tickerTrendlineSet = new HashSet<String>();
	private F1FilterBean f1FilterBean;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, String> countTicker = new HashMap<String, String>();
	private ParentLayout parentLayout = new ParentLayout();
	private DashboardController dashboardController;
	private Menu file = new Menu("File");
	private VBox alertPopupVBox;
	private ConcurrentMap<String, Long> snoozeAlertPopupMap = new ConcurrentHashMap<String, Long>();

	private Integer layoutId = null;
	private TickerDataUtil tickerDataUtil = new TickerDataUtil();

	private WSClient wsClient = new WSClient();
	private List<UITicker> tickers = new ArrayList<UITicker>();
	private List<String> tickerStringList = new ArrayList<String>();
	private Button toggleRightMenuButton;
	private ConcurrentSet<String> tickerTrendlineDataSet = new ConcurrentSet<>();
	private List<LayoutNode> layoutList = new ArrayList<LayoutNode>();

	// Dependencies initialization
	private UserService userService = new UserService();
	private DockPane middleDockPane;
	private DockPane leftDockPane;
	private DockPane rightDockPane;
	private AnchorPane parentContainer = new AnchorPane();
	private SplitPane middleSplitPane = new SplitPane();
	// Menu Items
	MenuItem showSideBarsMenuItem = new MenuItem("Show Side Bars");
	MenuItem hideSideBarsMenuItem = new MenuItem("Hide Side Bars");

	private Image logo = new Image("images/logo-icon.png");
	private ImageView logoView = new ImageView(logo);
	
	private VBox newsContainer = new VBox();

	// Table List initialization
	private ObservableList<Stock> watchStockList = FXCollections.observableArrayList();
	private ObservableList<F1Stock> f1StockList = FXCollections.observableArrayList();
	private ObservableList<F1Stock> f1StockBackupList = FXCollections.observableArrayList();
	private ObservableList<F1Stock> masterStockList = FXCollections.observableArrayList();
	private ObservableList<AlertStock> parabolicStockList = FXCollections.observableArrayList();
	private ObservableList<AlertStock> gapStockList = FXCollections.observableArrayList();

	private Map<Integer, ChartConfig> chartConfigs = new HashMap<Integer, ChartConfig>();

	// Data map initialization
	private Map<String, Stock> watchStockMap = new HashMap<String, Stock>();
	private Map<String, String> watchChartStockMap = new HashMap<String, String>();
	private Map<String, F1Stock> f1StockMap = new HashMap<String, F1Stock>();
	private Map<String, AlertStock> parabolicStockMap = new HashMap<String, AlertStock>();
	private Map<String, AlertStock> gapStockMap = new HashMap<String, AlertStock>();
	private Map<String, F1Stock> masterStockMap = new HashMap<String, F1Stock>();
	private Map<String, DockNode> dockNodesMap = new HashMap<String, DockNode>();

	// Netty streamer client setup
	private StreamerNettyClient streamerNettyClient;
	private StreamerNettyClient alertNettyClient;
	private StreamerNettyClient oneMinuteNettyClient;

	// Misc. variables
	protected boolean doUpdateWatchList = true;

	// Menus
	private MenuBar topLeftMenuBar = new MenuBar();
	private ToolBar rightMenuBar = new ToolBar();
	private ToolBar bottomMenubar = new ToolBar();

	// Table Views
	private TableView<F1Stock> masterListTableView = new TableView<F1Stock>();
	private TableView<Stock> watchListTableView = new TableView<Stock>();
	private TableView<F1Stock> f1TableView = new TableView<F1Stock>();
	private TableView<AlertStock> parabolicTableView = new TableView<AlertStock>();
	private TableView<AlertStock> gapTableView = new TableView<AlertStock>();
	private TableView<Stock> squeezeZoneTableView = new TableView<Stock>();

	// Master Table Columns
	private TableColumn<F1Stock, String> masterSymbolColumn = new TableColumn<F1Stock, String>("SYMBOL");
	private TableColumn<F1Stock, Double> masterLastPriceColumn = new TableColumn<F1Stock, Double>("PRICE");
	private TableColumn<F1Stock, Double> masterTriggerColumn = new TableColumn<F1Stock, Double>("TRIGGER");
	private TableColumn<F1Stock, String> masterTimeColumn = new TableColumn<F1Stock, String>("TIME");
	private TableColumn<F1Stock, String> masterSourceColumn = new TableColumn<F1Stock, String>("SOURCE");

	// Watch Table Columns
	private TableColumn<Stock, String> watchSymbolColumn = new TableColumn<Stock, String>("SYMBOL");
	private TableColumn<Stock, Double> watchPriceColumn = new TableColumn<Stock, Double>("PRICE");
	private TableColumn<Stock, Integer> watchGainColumn = new TableColumn<Stock, Integer>("% GAIN");
	private TableColumn<Stock, String> watchVolumnColumn = new TableColumn<Stock, String>("VOLUME");
	private TableColumn<Stock, Double> watchChangeColumn = new TableColumn<Stock, Double>("CHANGE");

	// F1 Table Columns
	private TableColumn<F1Stock, String> f1SymbolColumn = new TableColumn<F1Stock, String>("SYMBOL");
	private TableColumn<F1Stock, Double> f1PriceColumn = new TableColumn<F1Stock, Double>("PRICE");
	private TableColumn<F1Stock, Double> f1PercentChangeColumn = new TableColumn<F1Stock, Double>("% CHANGE");
	private TableColumn<F1Stock, String> f1StatusColumn = new TableColumn<F1Stock, String>("STATUS");
	private TableColumn<F1Stock, String> f1TriggerColumn = new TableColumn<F1Stock, String>("TRIGGER");
	private TableColumn<F1Stock, String> f1HodTriggerColumn = new TableColumn<F1Stock, String>("HOD TRIGGER");

	// Parabolic Table Columns
	private TableColumn<AlertStock, String> parabolicSymbolColumn = new TableColumn<AlertStock, String>("SYMBOL");
	private TableColumn<AlertStock, Double> parabolicTriggerPriceColumn = new TableColumn<AlertStock, Double>("TRIGGER");
	private TableColumn<AlertStock, Integer> parabolicActualColumn = new TableColumn<AlertStock, Integer>("ACTUAL");
	private TableColumn<AlertStock, String> parabolicStatusColumn = new TableColumn<AlertStock, String>("STATUS");
	private TableColumn<AlertStock, String> parabolicTimeColumn = new TableColumn<AlertStock, String>("TIME");
	private TableColumn<AlertStock, Double> parabolicChangeColumn = new TableColumn<AlertStock, Double>("CHANGE");

	// Gap Table Columns
	private TableColumn<AlertStock, String> gapSymbolColumn = new TableColumn<AlertStock, String>("SYMBOL");
	private TableColumn<AlertStock, Double> gapTriggerColumn = new TableColumn<AlertStock, Double>("TRIGGER");
	private TableColumn<AlertStock, Integer> gapActualColumn = new TableColumn<AlertStock, Integer>("ACTUAL");
	private TableColumn<AlertStock, String> gapStatusColumn = new TableColumn<AlertStock, String>("STATUS");
	private TableColumn<AlertStock, String> gapTimeColumn = new TableColumn<AlertStock, String>("TIME");
	private TableColumn<AlertStock, Double> gapChangeColumn = new TableColumn<AlertStock, Double>("CHANGE");

	// Squeeze Table Columns
	private TableColumn<Stock, String> squeezeZoneSymbolColumn = new TableColumn<Stock, String>("SYMBOL");
	private TableColumn<Stock, Double> squeezeZoneTriggerColumn = new TableColumn<Stock, Double>("TRIGGER");
	private TableColumn<Stock, Integer> squeezeZoneActualColumn = new TableColumn<Stock, Integer>("ACTUAL");
	private TableColumn<Stock, String> squeezeZoneStatusColumn = new TableColumn<Stock, String>("STATUS");
	private TableColumn<Stock, Double> squeezeZoneTimeColumn = new TableColumn<Stock, Double>("TIME");
	private TableColumn<Stock, Double> squeezeZoneChangeColumn = new TableColumn<Stock, Double>("CHANGE");

	private EventHandler<ActionEvent> logoutHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			LoginTemplate loginclient = new LoginTemplate();
			if (loginclient.logout()) {
				closeFloatingDockNodes();
				close();
				Stage primaryStage = (Stage) Context.getContext().get(Constants.PARENT_STAGE);
				primaryStage.setTitle(UtilityConstants.WINDOW_NAME);
				Parent root = null;
				try {
					root = FXMLLoader.load(getClass().getResource("/LoginController.fxml"));
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("Error opening LoginController FXML after signout. Full details: " + e);
				}
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());

				if (primaryStage.isMaximized()) {
					primaryStage.setMaximized(false);
				}
				primaryStage.setScene(scene);
				primaryStage.show();
				primaryStage.centerOnScreen();
			} else {
				System.out.println("Logout Failed !");
			}
		}
	};

	private void closeFloatingDockNodes() {
		for (Iterator<DockNode> iterator = dockNodesMap.values().iterator(); iterator.hasNext();) {
			DockNode dockNode = iterator.next();
			if (dockNode.isFloating()) {
				dockNode.close();
			}
			iterator.remove();
		}
	}

	private void showSideBars() {
		parentLayout.setShowSideBars(true);
		showSideBarsMenuItem.setDisable(true);
		hideSideBarsMenuItem.setDisable(false);
		middleSplitPane.getItems().add(0, leftDockPane);
		middleSplitPane.getItems().add(rightDockPane);
	}

	private void hideSideBars() {
		parentLayout.setShowSideBars(false);
		showSideBarsMenuItem.setDisable(false);
		hideSideBarsMenuItem.setDisable(true);
		middleSplitPane.getItems().remove(0);
		middleSplitPane.getItems().remove(middleSplitPane.getItems().size()-1);
	}

	private EventHandler<ActionEvent> showSideBarsHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			showSideBars();
		}
	};

	private EventHandler<ActionEvent> hideSideBarsHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			hideSideBars();
		}
	};

	public void initializeTableIdsAndCss() {
		masterListTableView.setId("table-view");
		watchListTableView.setId("table-view");
		f1TableView.setId("table-view");
		parabolicTableView.setId("table-view");
		gapTableView.setId("table-view");
		squeezeZoneTableView.setId("table-view");

		masterSymbolColumn.getStyleClass().add("table-column-ticker");
		watchSymbolColumn.getStyleClass().add("table-column-ticker");
		f1SymbolColumn.getStyleClass().add("table-column-ticker");
		parabolicSymbolColumn.getStyleClass().add("table-column-ticker");
		gapSymbolColumn.getStyleClass().add("table-column-ticker");
		squeezeZoneSymbolColumn.getStyleClass().add("table-column-ticker");

		masterLastPriceColumn.getStyleClass().add("table-column-normal");
		masterSourceColumn.getStyleClass().add("table-column-normal");
		masterTimeColumn.getStyleClass().add("table-column-normal");
		masterTriggerColumn.getStyleClass().add("table-column-normal");

		watchChangeColumn.getStyleClass().add("table-column-normal");
		watchGainColumn.getStyleClass().add("table-column-normal");
		watchPriceColumn.getStyleClass().add("table-column-normal");
		watchVolumnColumn.getStyleClass().add("table-column-normal");

		f1HodTriggerColumn.getStyleClass().add("table-column-normal");
		f1PercentChangeColumn.getStyleClass().add("table-column-normal");
		f1PriceColumn.getStyleClass().add("table-column-normal");
		f1StatusColumn.getStyleClass().add("table-column-normal");
		f1TriggerColumn.getStyleClass().add("table-column-normal");

		parabolicActualColumn.getStyleClass().add("table-column-normal");
		parabolicChangeColumn.getStyleClass().add("table-column-normal");
		parabolicStatusColumn.getStyleClass().add("table-column-normal");
		parabolicTimeColumn.getStyleClass().add("table-column-normal");
		parabolicTriggerPriceColumn.getStyleClass().add("table-column-normal");

		gapActualColumn.getStyleClass().add("table-column-normal");
		gapChangeColumn.getStyleClass().add("table-column-normal");
		gapStatusColumn.getStyleClass().add("table-column-normal");
		gapTimeColumn.getStyleClass().add("table-column-normal");
		gapTriggerColumn.getStyleClass().add("table-column-normal");

		squeezeZoneActualColumn.getStyleClass().add("table-column-normal");
		squeezeZoneChangeColumn.getStyleClass().add("table-column-normal");
		squeezeZoneStatusColumn.getStyleClass().add("table-column-normal");
		squeezeZoneTimeColumn.getStyleClass().add("table-column-normal");
		squeezeZoneTriggerColumn.getStyleClass().add("table-column-normal");
	}

	private void generalPropertiesInitialization() {
		Font.loadFont(getClass().getResource("/fonts/segoeui.ttf").toExternalForm(), 10);
		
		parentContainer.getStyleClass().add("background-color");
		showSideBarsMenuItem.setDisable(true);
		hideSideBarsMenuItem.setDisable(false);

		Context.getContext().put(Constants.CHART_CONFIG_MAP_KEY, chartConfigs);
		Context.getContext().put(Constants.LAYOUT_PARENT, parentLayout);
		f1FilterBean = new F1FilterBean(Constants.PRICE_MIN, Constants.PRICE_MAX, Constants.VOLUME_MIN, Constants.VOLUME_MAX);
	}

	private StackPane initializeHorizontallyBottomMenu() {
		StackPane bottomStackPane = new StackPane();
		bottomStackPane.getStyleClass().addAll("border-color");
		bottomMenubar.setPrefWidth(600);
		bottomMenubar.setMaxHeight(27);
		bottomMenubar.setMinHeight(27);
		bottomMenubar.getStyleClass().add("background-color");
		StackPane.setAlignment(bottomMenubar, Pos.CENTER_LEFT);

		AnchorPane.setBottomAnchor(bottomStackPane, 0.0);
		AnchorPane.setLeftAnchor(bottomStackPane, 0.0);
		AnchorPane.setRightAnchor(bottomStackPane, 0.0);

		bottomStackPane.getChildren().add(bottomMenubar);
		return bottomStackPane;
	}

	private HBox getHorizontallyMiddleStackPane() {
		HBox middleHBox = new HBox();

		HBox.setHgrow(middleHBox, Priority.ALWAYS);
		AnchorPane.setBottomAnchor(middleHBox, 0.0);
		AnchorPane.setTopAnchor(middleHBox, 0.0);
		AnchorPane.setLeftAnchor(middleHBox, 0.0);
		AnchorPane.setRightAnchor(middleHBox, 0.0);

		rightMenuBar.setMaxWidth(50.0);
		rightMenuBar.setMinWidth(50.0);
		rightMenuBar.setOrientation(Orientation.VERTICAL);
		rightMenuBar.getStyleClass().addAll("background-color-sidebar", "right-menu-bar-padding", "border-color");
		rightMenuBar.managedProperty().bind(rightMenuBar.visibleProperty());

		middleHBox.getChildren().addAll(middleSplitPane, rightMenuBar);
		HBox.setHgrow(middleSplitPane, Priority.ALWAYS);
		HBox.setHgrow(rightMenuBar, Priority.ALWAYS);
		return middleHBox;
	}

	private EventHandler<ActionEvent> showHideToggleHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			ToggleButton toggleButton = (ToggleButton) event.getSource();
			if(toggleButton.isSelected()) {
				hideSideBars();
				toggleButton.setOpacity(0.2);
			} else {
				showSideBars();
				toggleButton.setOpacity(1.0);
			}
		}
	};
	
	private StackPane getTopMenuStackPane() {
		StackPane topMenuStackPane = new StackPane();
		topMenuStackPane.getStyleClass().addAll("border-color");
		AnchorPane.setLeftAnchor(topMenuStackPane, 0.0);
		AnchorPane.setRightAnchor(topMenuStackPane, 0.0);

		logoView.setFitWidth(57);
		logoView.setFitHeight(50);
		topMenuStackPane.getChildren().add(logoView);
		StackPane.setAlignment(logoView, Pos.CENTER_LEFT);

		topLeftMenuBar.setMaxWidth(350.0);
		topLeftMenuBar.setMinWidth(350.0);
		topLeftMenuBar.setMinHeight(50.0);
		topLeftMenuBar.setMaxHeight(50.0);
		topLeftMenuBar.setPadding(new Insets(12.0, 7.0, 12.0, 7.0));
		topLeftMenuBar.getStyleClass().addAll("top-left-menu-bar", "background-color");
		topMenuStackPane.getChildren().add(topLeftMenuBar);
		StackPane.setAlignment(topLeftMenuBar, Pos.CENTER_LEFT);
		StackPane.setMargin(topLeftMenuBar, new Insets(0.0, 0.0, 0.0, 57.0));

		Button addChart = new Button("Add Chart", new ImageView(new Image("images/addChart.png")));
		addChart.getStyleClass().addAll("button-background", "top-center-menu-padding");
		addChart.setOnAction(addChartButtonHandler);
		addChart.setCursor(Cursor.HAND);
		addChart.setAlignment(Pos.CENTER);

		ToggleButton toggleSideBarButton = new ToggleButton(Constants.applicationLanguage.getProperty("label.toggle.scans"));
		toggleSideBarButton.setOnAction(showHideToggleHandler);
		toggleSideBarButton.getStyleClass().addAll("button-background", "top-center-menu-padding", "toggle-scans-icon");
		toggleSideBarButton.setOpacity(1.0);
		
		HBox middleHbox = new HBox(5, addChart, toggleSideBarButton);
		StackPane.setAlignment(middleHbox, Pos.CENTER);
		middleHbox.setMaxWidth(300);
		middleHbox.setMinWidth(300);
		middleHbox.getStyleClass().add("top-center-menu-padding");
		topMenuStackPane.getChildren().addAll(middleHbox, toggleRightMenuButton);

		StackPane.setMargin(middleHbox, new Insets(15.0, 0.0, 0.0, 0.0));
		
		StackPane.setAlignment(toggleRightMenuButton, Pos.CENTER_RIGHT);
		parentContainer.getChildren().add(topMenuStackPane);

		return topMenuStackPane;
	}

	private void addAllComponentsInParentContainer() {
		StackPane horizontallyTopMenuStackPane = getTopMenuStackPane();
		HBox horizontallyMiddleStackPane = getHorizontallyMiddleStackPane();
		StackPane horizontallyBottomStackPane = initializeHorizontallyBottomMenu();

		VBox parentVBox = new VBox();

		AnchorPane.setBottomAnchor(parentVBox, 0.0);
		AnchorPane.setTopAnchor(parentVBox, 0.0);
		AnchorPane.setLeftAnchor(parentVBox, 0.0);
		AnchorPane.setRightAnchor(parentVBox, 0.0);

		VBox.setVgrow(horizontallyTopMenuStackPane, Priority.NEVER);
		VBox.setVgrow(horizontallyMiddleStackPane, Priority.ALWAYS);
		VBox.setVgrow(horizontallyBottomStackPane, Priority.NEVER);

		parentVBox.getChildren().addAll(horizontallyTopMenuStackPane, horizontallyMiddleStackPane,
				horizontallyBottomStackPane);

		parentContainer.getChildren().add(parentVBox);
	}

	private void setupAndShowStage() {
		Stage parentStage = (Stage) Context.getContext().get(Constants.PARENT_STAGE);
		parentStage.setTitle("Dashboard");
		Scene parentScene = new Scene(parentContainer);
		parentScene.getStylesheets().addAll(getClass().getResource("/application.css").toExternalForm());
		parentStage.setScene(parentScene);
		parentStage.sizeToScene();
		parentStage.show();
		parentStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				close();
				System.exit(0);
			}
		});
		if (!parentStage.isMaximized()) {
			parentStage.setMaximized(true);
		}

		parentStage.setMinWidth(900);

		Context.getContext().put(Constants.PARENT_SCENE, parentScene);
		Context.getContext().put(Constants.PARENT_CONTAINER, parentContainer);
	}

	public void initialize() throws SchedulerException, URISyntaxException {
		dashboardController = this;
		
		generalPropertiesInitialization();
		loadApplicationData();
		initializeTableIdsAndCss();
		createMenuBar();
		addAllComponentsInParentContainer();
		setupAndShowStage();
		Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
		DockPane.initializeDefaultUserAgentStylesheet();
		initializeDashboardContent();
		initializeDefaultWebViews();
		addTableColumnsInTableViews();
		initializeAlertVBoxPopupStage();
		initializeAlertPopupTimer();
		setupTrendlineAlerts(chartConfigs, alertTrendlineBeanMap, alertHorizontalTrendlineBeanMap);
		/*deleteTrendlineInDBAndLayoutList(340, Integer.toString(-2033995979), false);
		deleteTrendlineInDBAndLayoutList(790, Integer.toString(-2033995979), true);*/
	}

	private void setupTrendlineAlerts(Map<Integer, ChartConfig> chartConfigs, Map<String, List<TrendLineParamBean>> trendlineAlertBeanMap, Map<String, List<TrendLineParamBean>> horizontalTrendlineAlertBeanMap) {
		for(ChartConfig chartConfig : chartConfigs.values()) {
			String ticker = chartConfig.getTickerSymbol();
			if(alertTrendlineBeanMap.get(ticker) == null) {
				List<TrendLineParamBean> trendLines = chartConfig.getTrendlineParams().stream().map(trendline -> {
					trendline.setTickerName(chartConfig.getTickerSymbol());
					return trendline;
				}).collect(Collectors.toList());
				trendlineAlertBeanMap.put(ticker, trendLines);
			} else {
				alertTrendlineBeanMap.get(ticker).addAll(chartConfig.getTrendlineParams());
			}

			if(horizontalTrendlineAlertBeanMap.get(ticker) == null) {
				List<TrendLineParamBean> trendLines = chartConfig.getHorizontalTrendlineParams().stream().map(trendline -> {
					trendline.setTickerName(chartConfig.getTickerSymbol());
					return trendline;
				}).collect(Collectors.toList());
				horizontalTrendlineAlertBeanMap.put(ticker, trendLines);
			} else {
				horizontalTrendlineAlertBeanMap.get(ticker).addAll(chartConfig.getHorizontalTrendlineParams());
			}
		}

	}

	private void initializeAlertPopupTimer() {
		new Timer().schedule(
				new TimerTask() {
					@Override
					public void run() {
						for (String key : snoozeAlertPopupMap.keySet()) {
							Long snoozeTime = snoozeAlertPopupMap.get(key);
							if(snoozeTime > 0) {
								snoozeAlertPopupMap.put(key, snoozeTime-1);
							} else if(snoozeTime == 0) {
								snoozeAlertPopupMap.remove(key);
							}
						}
					}
				}, 0, 60000);
	}

	private void initializeAlertVBoxPopupStage() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Stage stage = new Stage(StageStyle.TRANSPARENT);
		alertPopupVBox = new VBox(5);
		alertPopupVBox.setMinHeight(screenSize.getHeight()-150);
		alertPopupVBox.setBackground(Background.EMPTY);

		ScrollPane scrollPane = new ScrollPane(alertPopupVBox);
		scrollPane.setMinWidth(300.0);
		scrollPane.setMinHeight(screenSize.getHeight()-150);
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.getStyleClass().addAll("transparent-background", "scrollpane-transparent-background");
		alertPopupVBox.getStyleClass().addAll("transparent-background", "scrollpane-transparent-background");

		Scene scene = new Scene(scrollPane);
		scene.setFill(Color.TRANSPARENT);
		stage.setScene(scene);
		stage.setMinWidth(300.0);
		stage.setMinHeight(screenSize.getHeight()-150);
		stage.setX(screenSize.getWidth() - 350);
		stage.setY(150);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				stage.initOwner(parentContainer.getScene().getWindow());
				stage.show();
			}
		});
	}

	private void showUserHorizontalAlerts(List<TrendLineParamBean> alerts, Stock stock) {
		if(alerts != null && alerts.size() > 0) {
			for(TrendLineParamBean trendlineAlertBean : alerts) {
				if(tickerTrendlineDataSet.contains(trendlineAlertBean.getChartUID()+Constants.UNDERSCORE+trendlineAlertBean.getiD())) {
					showUserAlert(trendlineAlertBean, stock, trendlineAlertBean.getY1());
				}
			}
		}
	}

	private void showUserAlerts(List<TrendLineParamBean> alerts, Stock stock, int candleIndex) {
		if(alerts != null && alerts.size() > 0) {
			for(TrendLineParamBean trendlineAlertBean : alerts) {
				if(tickerTrendlineDataSet.contains(trendlineAlertBean.getChartUID()+Constants.UNDERSCORE+trendlineAlertBean.getiD())) {
					double projectedPrice = getTrendlineProjectedPrice(trendlineAlertBean.getSlope(), candleIndex, trendlineAlertBean.getX1Index(), trendlineAlertBean.getY1());
					showUserAlert(trendlineAlertBean, stock, projectedPrice);
				}
			}			
		}
	}

	private void showUserAlert(TrendLineParamBean alertTrendLineBean, Stock stock, double projectedPrice) {
		boolean isAlertGenerated = false;
		double currentPrice = stock.lastPriceProperty().get();
		Date alertDate = DateUtil.convertToEST(new Date());
		String alertDateString = DateUtil.formatDate(alertDate, Constants.DATE_TIME_FORMAT_ALERT_POPUP);
		boolean generateAlert = false;
		AlertDirection previousAlertDirection = alertTrendLineBean.getAlertDirection();
		AlertDirection currentAlertDirection = currentPrice > projectedPrice ? AlertDirection.UP : AlertDirection.DOWN;
		if(previousAlertDirection != null && previousAlertDirection != currentAlertDirection) {
			generateAlert = true;
		}
		alertTrendLineBean.setAlertDirection(currentAlertDirection);

		if(alertTrendLineBean.isEnabled() && generateAlert) {
			if(alertTrendLineBean.getSnoozeUpto() == 0) {
				alertTrendLineBean.setSnoozeMinutes(Constants.DEFAULT_ALERT_WAKE_MINUTES);
				alertTrendLineBean.setSnoozeUpto(System.currentTimeMillis() + Constants.DEFAULT_ALERT_WAKE_MILLIS);
			}
			if(alertTrendLineBean.getSnoozeUpto() < System.currentTimeMillis()) {
				if(stock.symbolProperty().get().equals(alertTrendLineBean.getTickerName())) {
					AlertPopupBean popupBean = null;

					//if(alertTrendLineBean.getY1() != null && alertTrendLineBean.getY2() != null) {
					if(alertTrendLineBean.isCheckCrossingAboveAndDown()) {
						String alertText = null;
						if(currentPrice > projectedPrice && alertTrendLineBean.getAlertDirection() != AlertDirection.UP) {
							alertText = Constants.applicationLanguage.getProperty("label.candle.up").replace("{Symbol}", stock.symbolProperty().get());
							alertTrendLineBean.setAlertDirection(AlertDirection.UP);
						} else {
							alertText = Constants.applicationLanguage.getProperty("label.candle.down").replace("{Symbol}", stock.symbolProperty().get());
							alertTrendLineBean.setAlertDirection(AlertDirection.DOWN);
						}
						isAlertGenerated = true;
						alertText = alertText.replace("{Price}", Double.toString(NumberUtil.truncateDecimalPointsTo2(stock.lastPriceProperty().get())));
						popupBean = new AlertPopupBean(alertText, alertDateString, alertTrendLineBean);
					} else if(currentPrice < projectedPrice) {
						String alertText = Constants.applicationLanguage.getProperty("label.candle.down").replace("{Symbol}", stock.symbolProperty().get());
						alertText = alertText.replace("{Price}", Double.toString(NumberUtil.truncateDecimalPointsTo2(stock.lastPriceProperty().get())));
						alertTrendLineBean.setAlertDirection(AlertDirection.DOWN);
						isAlertGenerated = true;
						popupBean = new AlertPopupBean(alertText, alertDateString, alertTrendLineBean);
					} else if(currentPrice > projectedPrice) {
						alertTrendLineBean.setAlertDirection(AlertDirection.UP);
						String alertText = Constants.applicationLanguage.getProperty("label.candle.up").replace("{Symbol}", stock.symbolProperty().get());
						alertText = alertText.replace("{Price}", String.valueOf(NumberUtil.truncateDecimalPointsTo2(stock.lastPriceProperty().get())));
						popupBean = new AlertPopupBean(alertText, alertDateString, alertTrendLineBean);
						isAlertGenerated = true;
					}
					//}

					if(alertTrendLineBean.isPlaySound() && isAlertGenerated) {
						Platform.runLater(() -> {
							try {
								ApplicationUtil.playAlertSound();
							} catch (URISyntaxException e) {
								logger.error("Error while playing alert sound. Full details: " + e);
								e.printStackTrace();
							}
						});
					}
					if(alertTrendLineBean.isShowPopup() && isAlertGenerated) {
						if((popupBean != null)) {
							popupBean.setParentVBox(alertPopupVBox);
							popupBean.getEditButton().setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									AlertPopupEditDialog dialog = new AlertPopupEditDialog();
									AlertEditPopupBean alertEditPopupBean = dialog.showAlertPopupEditDialog();
									if(alertEditPopupBean != null) {
										boolean isAlertDisabled = alertEditPopupBean.isAlertDisabled();
										if(!isAlertDisabled) {
											int snoozeMinutes = Integer.parseInt(alertEditPopupBean.getSnoozeTime());
											if(snoozeMinutes == 0) {
												snoozeMinutes = Constants.DEFAULT_ALERT_WAKE_MINUTES;
											}
											alertTrendLineBean.setSnoozeMinutes(snoozeMinutes);
											alertTrendLineBean.setSnoozeUpto(System.currentTimeMillis() + DateUtil.getMillisFromMinutes(snoozeMinutes));
										} else {
											alertTrendLineBean.setEnabled(true);
											alertTrendLineBean.setShowPopup(true);
											int minutesRemaining = DateUtil.getRemainingMinutesInCurrentDay();
											alertTrendLineBean.setSnoozeMinutes(minutesRemaining);
											alertTrendLineBean.setSnoozeUpto(DateUtil.getMillisFromMinutes(minutesRemaining));
										}
									}
								}


							});
							alertPopupVBox.getChildren().add(0, popupBean.getFormattedAlertPane());
							alertTrendLineBean.setSnoozeUpto(System.currentTimeMillis() + alertTrendLineBean.getSnoozeMinutes() * 60 * 1000);
						}
					}
				}

			}
		}


	}

	private void loadApplicationData() {
		TickerSearchResponse resultBean = wsClient.sendGetRequest(Constants.applicationProperties.getProperty("application.server.uri") + Constants.applicationProperties.getProperty("search.tickers"), TickerSearchResponse.class);
		if (resultBean.getStatus() == ResultStatus.SUCCESS)
			tickers = resultBean.getObjects();

		for (UITicker ticker : tickers) {
			tickerStringList.add(ticker.getTickerCode());			
		}

		Context.getContext().put(Constants.FULL_TICKER_LIST_KEY, tickers);
		Context.getContext().put(Constants.TICKER_LIST_AS_STRING, tickerStringList);
		
		
		loadAllNews();
	}
	
	private void loadAllNews() {
		Runnable newsFetcher = new Runnable() {
			public void run() {
				Date today = new Date();
				Date yesterday = DateUtil.getModifiedStartDate(today, -1, Calendar.DATE);
				String startDate = DateUtil.getConcatDateFields(yesterday, true);
				String endDate = DateUtil.getConcatDateFields(DateUtil.getModifiedEndDate(yesterday, 1, Calendar.DATE),	true);
				
				System.out.println(startDate + " to " + endDate);
				String serverUrl = Constants.applicationProperties.getProperty("application.server.uri");
				
			    NewsResponse newsResponse = wsClient.sendGetRequest(serverUrl + Constants.applicationProperties.getProperty("all.news.api").replace("{start}", startDate).replace("{end}", endDate), NewsResponse.class);
			    List<TickerNews> newsList = newsResponse.getObjects();
			    if(newsList != null) {
		    		Gson gson = getCustomizedDateFormatterDeserializerGson();
		    		for(TickerNews news : newsList) {
		    			APINews apiFullNews = gson.fromJson(news.getJson(), APINews.class);
		    			NewsBean newsBean = new NewsBean(news.getTitle(), news.getSource(), news.getUrl(), DateUtil.formatDate(apiFullNews.getUpdated(), Constants.TIME_FORMAT));
		    			Platform.runLater(() -> {		
		    				newsContainer.getChildren().add(0, newsBean.getFormattedNews());
		    			});
		    		}			    		
			    }
			}
		};
		
		Thread newsThread = new Thread(newsFetcher);
		newsThread.start();
	}

	private void initializeWatchTableColumns() {
		watchSymbolColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("symbol"));
		watchPriceColumn.setCellValueFactory(new PropertyValueFactory<Stock, Double>("lastPrice"));
		watchChangeColumn.setCellValueFactory(new PropertyValueFactory<Stock, Double>("change"));

		watchListTableView.setItems(watchStockList);
		watchListTableView.setEditable(true);

		watchListTableView.setOnMousePressed(new EventHandler<MouseEvent>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 1) {
					TablePosition focusedCellPosition = watchListTableView.getFocusModel().getFocusedCell();
					watchListTableView.edit(focusedCellPosition.getRow(), focusedCellPosition.getTableColumn());
				}
			}
		});

		watchChangeColumn.setCellFactory(new Callback<TableColumn<Stock, Double>, TableCell<Stock, Double>>() {
			@Override
			public TableCell<Stock, Double> call(TableColumn<Stock, Double> param) {
				return new TableCell<Stock, Double>() {
					protected void updateItem(Double item, boolean empty) {
						super.updateItem(item, empty);
						if (!empty) {
							setText(String.valueOf(item));
							if (item != null) {
								if (item > 0) {
									getStyleClass().add("green-cell-text-color");
									getStyleClass().remove("red-cell-text-color");
								} else {
									getStyleClass().add("red-cell-text-color");
									getStyleClass().remove("green-cell-text-color");
								}
							} else {
								setText(Constants.EMPTY_STRING);
								setStyle(Constants.EMPTY_STRING);
							}
						} else {
							setText(Constants.EMPTY_STRING);
						}
					}
				};
			}
		});

		watchSymbolColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		watchSymbolColumn.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<Stock, String>>() {
			@Override
			public void handle(CellEditEvent<Stock, String> event) {
				doUpdateWatchList = false;
			}
		});

		watchSymbolColumn.setOnEditCancel(new EventHandler<TableColumn.CellEditEvent<Stock, String>>() {
			@Override
			public void handle(CellEditEvent<Stock, String> event) {
				doUpdateWatchList = true;
			}
		});
		watchSymbolColumn.setOnEditCommit(new EventHandler<CellEditEvent<Stock, String>>() {
			@Override
			public void handle(CellEditEvent<Stock, String> t) {
				if (t.getNewValue().equals(Constants.EMPTY_STRING)) {
					removeTicker(t.getOldValue().toUpperCase());
				} else if (!t.getOldValue().equalsIgnoreCase(t.getNewValue())
						&& Pattern.matches(Constants.TICKER_PATTERN_REGEX, t.getNewValue().toUpperCase())
						&& tickerStringList.contains(t.getNewValue().toUpperCase())) {
					removeTicker(t.getOldValue().toUpperCase());
					addTicker(t.getNewValue().toUpperCase());
				} else {
					// watchListTableView.refresh();
				}
				doUpdateWatchList = true;
			}
		});
	}

	private void initializeGapTableColumns() {
		gapSymbolColumn.setCellValueFactory(new PropertyValueFactory<AlertStock, String>("symbol"));
		gapStatusColumn.setCellValueFactory(new PropertyValueFactory<AlertStock, String>("alertStatus"));
		gapTimeColumn.setCellValueFactory(new PropertyValueFactory<AlertStock, String>("lastDateTimeString"));
		gapTriggerColumn.setCellValueFactory(new PropertyValueFactory<AlertStock, Double>("triggerPrice"));
		gapChangeColumn.setCellValueFactory(new PropertyValueFactory<AlertStock, Double>("change"));

		gapStatusColumn.setCellFactory(new Callback<TableColumn<AlertStock, String>, TableCell<AlertStock, String>>() {
			@Override
			public TableCell<AlertStock, String> call(TableColumn<AlertStock, String> param) {
				return new TableCell<AlertStock, String>() {
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if((this.getTableRow() != null) && (this.getTableRow().getChildrenUnmodifiable().size() > 0)) {
							if (!empty && (item != null)) {
								getStyleClass().remove("yellow-cell-text-color");
								getStyleClass().remove("green-cell-background");
								this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().remove("yellow-cell-background");
								this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().remove("transparent-background");
								setText(String.valueOf(item));
								if (item.equalsIgnoreCase("ACTIONABLE")) {
									this.getStyleClass().add("green-cell-background");
									this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().add("yellow-cell-background");
								} else {
									getStyleClass().add("yellow-cell-text-color");
									this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().add("transparent-background");
								}
							} else {
								setText(Constants.EMPTY_STRING);
								if (this.getTableRow().getChildrenUnmodifiable().size() > 0) {
									getStyleClass().remove("green-cell-background");
									getStyleClass().remove("yellow-cell-text-color");
									this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().remove("yellow-cell-background");
									this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().remove("transparent-background");
								}
							}
						}
					}
				};
			}
		});

		gapTriggerColumn.setCellFactory(new Callback<TableColumn<AlertStock, Double>, TableCell<AlertStock, Double>>() {
			@Override
			public TableCell<AlertStock, Double> call(TableColumn<AlertStock, Double> param) {
				return new TableCell<AlertStock, Double>() {
					protected void updateItem(Double item, boolean empty) {
						super.updateItem(item, empty);
						getStyleClass().remove("yellow-cell-text-color");
						if (!empty && (item != null)) {
							setText(String.valueOf(item));
							if((this.getTableRow() != null) && (this.getTableRow().getItem() != null)) {
								AlertStock stock = (AlertStock) this.getTableRow().getItem();
								if ((stock != null) && (stock.alertStatusProperty() != null)
										&& (stock.alertStatusProperty().get() != null)
										&& (stock.alertStatusProperty().get().equals("ACTIONABLE"))) {
									getStyleClass().add("yellow-cell-text-color");
								}
							}
						} else {
							setText(Constants.EMPTY_STRING);
							getStyleClass().remove("yellow-cell-text-color");
						}
					}
				};
			}
		});

		gapTableView.setItems(gapStockList);
	}

	private void initializeParabolicTableColumns() {
		parabolicSymbolColumn.setCellValueFactory(new PropertyValueFactory<AlertStock, String>("symbol"));
		parabolicStatusColumn.setCellValueFactory(new PropertyValueFactory<AlertStock, String>("alertStatus"));
		parabolicTimeColumn.setCellValueFactory(new PropertyValueFactory<AlertStock, String>("lastDateTimeString"));
		parabolicTriggerPriceColumn.setCellValueFactory(new PropertyValueFactory<AlertStock, Double>("triggerPrice"));
		parabolicChangeColumn.setCellValueFactory(new PropertyValueFactory<AlertStock, Double>("change"));

		parabolicStatusColumn.setCellFactory(new Callback<TableColumn<AlertStock, String>, TableCell<AlertStock, String>>() {
			@Override
			public TableCell<AlertStock, String> call(TableColumn<AlertStock, String> param) {
				return new TableCell<AlertStock, String>() {
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if((this.getTableRow() != null) && (this.getTableRow().getChildrenUnmodifiable().size() > 0)) {
							if (!empty && (item != null)) {
								getStyleClass().remove("yellow-cell-text-color");
								getStyleClass().remove("green-cell-background");
								this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().remove("yellow-cell-background");
								this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().remove("transparent-background");
								setText(String.valueOf(item));
								if (item.equalsIgnoreCase("ACTIONABLE")) {
									this.getStyleClass().add("green-cell-background");
									this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().add("yellow-cell-background");
								} else {
									getStyleClass().add("yellow-cell-text-color");
									this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().add("transparent-background");
								}
							} else {
								setText(Constants.EMPTY_STRING);
								if (this.getTableRow().getChildrenUnmodifiable().size() > 0) {
									getStyleClass().remove("green-cell-background");
									getStyleClass().remove("yellow-cell-text-color");
									this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().remove("yellow-cell-background");
									this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().remove("transparent-background");
								}
							}
						}
					}
				};
			}
		});

		parabolicTriggerPriceColumn.setCellFactory(new Callback<TableColumn<AlertStock, Double>, TableCell<AlertStock, Double>>() {
			@Override
			public TableCell<AlertStock, Double> call(TableColumn<AlertStock, Double> param) {
				return new TableCell<AlertStock, Double>() {
					protected void updateItem(Double item, boolean empty) {
						super.updateItem(item, empty);
						getStyleClass().remove("yellow-cell-text-color");
						if (!empty && (item != null)) {
							setText(String.valueOf(item));
							if(this.getTableRow() != null) {
								AlertStock stock = (AlertStock) this.getTableRow().getItem();
								if ((stock != null) && (stock.alertStatusProperty() != null)
										&& (stock.alertStatusProperty().get() != null)
										&& (stock.alertStatusProperty().get().equals("ACTIONABLE"))) {
									getStyleClass().add("yellow-cell-text-color");
								}
							}
						} else {
							setText(Constants.EMPTY_STRING);
							getStyleClass().remove("yellow-cell-text-color");
						}
					}
				};
			}
		});

		parabolicTableView.setItems(parabolicStockList);
	}

	private void initializeF1TableColumns() {
		/* F1 Scanner List Code Starts Here */
		f1SymbolColumn.setCellValueFactory(new PropertyValueFactory<F1Stock, String>("symbol"));
		f1PriceColumn.setCellValueFactory(new PropertyValueFactory<F1Stock, Double>("lastPrice"));
		f1PercentChangeColumn.setCellValueFactory(new PropertyValueFactory<F1Stock, Double>("change"));
		f1StatusColumn.setCellValueFactory(new PropertyValueFactory<F1Stock, String>("status"));
		f1HodTriggerColumn.setCellValueFactory(new PropertyValueFactory<F1Stock, String>("hodTrigger"));
		f1TriggerColumn.setCellValueFactory(new PropertyValueFactory<F1Stock, String>("trigger"));

		f1HodTriggerColumn.setCellFactory(new Callback<TableColumn<F1Stock, String>, TableCell<F1Stock, String>>() {
			@Override
			public TableCell<F1Stock, String> call(TableColumn<F1Stock, String> param) {
				return new TableCell<F1Stock, String>() {
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						getStyleClass().remove("red-cell-text-color");
						if (!empty) {
							setText(String.valueOf(item));
							if(this.getTableRow() != null) {
								F1Stock stock = (F1Stock) this.getTableRow().getItem();
								if ((stock != null)&& (stock.statusProperty()!= null) && (stock.statusProperty().get() != null)
										&& (stock.statusProperty().get().equalsIgnoreCase("ACTIONABLE"))) {
									getStyleClass().add("red-cell-text-color");
								}
							}
						} else {
							setText(Constants.EMPTY_STRING);
							getStyleClass().remove("red-cell-text-color");
						}
					}
				};
			}
		});

		f1StatusColumn.setCellFactory(new Callback<TableColumn<F1Stock, String>, TableCell<F1Stock, String>>() {
			@Override
			public TableCell<F1Stock, String> call(TableColumn<F1Stock, String> param) {
				return new TableCell<F1Stock, String>() {
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if((this.getTableRow() != null) && (this.getTableRow().getChildrenUnmodifiable().size() > 0)) {
							if (!empty && (item != null)) {
								getStyleClass().remove("yellow-cell-text-color");
								getStyleClass().remove("green-cell-background");
								this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().remove("yellow-cell-background");
								this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().remove("transparent-background");
								setText(String.valueOf(item));
								if (item.equalsIgnoreCase("ACTIONABLE")) {
									this.getStyleClass().add("green-cell-background");
									this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().add("yellow-cell-background");
								} else {
									getStyleClass().add("yellow-cell-text-color");
									this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().add("transparent-background");
								}
							} else {
								setText(Constants.EMPTY_STRING);
								if (this.getTableRow().getChildrenUnmodifiable().size() > 0) {
									getStyleClass().remove("green-cell-background");
									getStyleClass().remove("yellow-cell-text-color");
									this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().remove("yellow-cell-background");
									this.getTableRow().getChildrenUnmodifiable().get(0).getStyleClass().remove("transparent-background");
								}
							}
						}
					}
				};
			}
		});

		f1PriceColumn.setCellFactory(new Callback<TableColumn<F1Stock, Double>, TableCell<F1Stock, Double>>() {
			@Override
			public TableCell<F1Stock, Double> call(TableColumn<F1Stock, Double> param) {
				return new TableCell<F1Stock, Double>() {
					protected void updateItem(Double item, boolean empty) {
						super.updateItem(item, empty);
						getStyleClass().remove("yellow-cell-text-color");
						if (!empty && (item != null)) {
							setText(String.valueOf(item));
							if(this.getTableRow() != null) {
								F1Stock stock = (F1Stock) this.getTableRow().getItem();
								if ((stock != null) &&(stock.statusProperty()!= null) &&(stock.statusProperty().get() != null) && (stock.statusProperty().get().equals("ACTIONABLE"))) {
									getStyleClass().add("yellow-cell-text-color");
								}
							}
						} else {
							setText(Constants.EMPTY_STRING);
							getStyleClass().remove("yellow-cell-text-color");
						}
					}
				};
			}
		});

		f1TableView.setItems(f1StockList);
		/* F1 Scanner List Code Ends Here */
	}

	private void addMasterTickerEntryToDB(String tickerSymbol, Integer userId) {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				userService.insertMasterTickerDetails(tickerSymbol, userId);
				return null;
			}
		};
		new Thread(task).start();
	}

	private void addDataToMasterTable(F1Stock f1Stock, String source) {
		if(f1Stock.symbolProperty() != null) {
			if (masterStockMap.containsKey(f1Stock.symbolProperty().get())) {
				if(masterStockList.indexOf(f1Stock) > -1) {
					masterStockList.set(masterStockList.indexOf(f1Stock), f1Stock);				
				}
			} else {
				masterStockMap.put(f1Stock.symbolProperty().get(), f1Stock);
				f1Stock.setSource(new SimpleStringProperty(source));
				masterStockList.add(0, f1Stock);
				addMasterTickerEntryToDB(f1Stock.symbolProperty().get(), (Integer) Context.getContext().get(Constants.USERID_KEY));
			}			
		}
	}

	private void initializeMasterTableColumns() {
		masterSymbolColumn.setCellValueFactory(new PropertyValueFactory<F1Stock, String>("symbol"));
		masterLastPriceColumn.setCellValueFactory(new PropertyValueFactory<F1Stock, Double>("lastPrice"));
		masterTriggerColumn.setCellValueFactory(new PropertyValueFactory<F1Stock, Double>("trigger"));
		masterTimeColumn.setCellValueFactory(new PropertyValueFactory<F1Stock, String>("lastDateTimeString"));
		masterSourceColumn.setCellValueFactory(new PropertyValueFactory<F1Stock, String>("source"));

		masterListTableView.setItems(masterStockList);
	}

	private void initializeDashboardContent() {
		try {
			registerInternalSocketServerHandlers();
			startRecievingData();
			registerInternalSocketClientHandlers();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while initialize Dashboard. Full details: " + e);
		}

		initializeWatchTableColumns();
		initializeF1TableColumns();
		initializeParabolicTableColumns();
		initializeGapTableColumns();
		initializeMasterTableColumns();
	}

	private void addTicker(String symbolName) {
		try {
			boolean result = userService.saveTicker(symbolName,
					(Integer) Context.getContext().get(Constants.USERID_KEY));
			if (result) {
				if (streamerNettyClient != null && streamerNettyClient.getChannel() != null) {
					streamerNettyClient.getChannel().writeAndFlush(symbolName + "\r\n");
					addTickerToWatchMapAndWatchList(symbolName);
				}
			} else {
				ApplicationUtil.displayAlert("Unable to add ticker");
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			logger.error("Error while adding ticker. Full details:" + e);
		}
	}

	public void removeTickerFromList(String tickerSymbol) {
		Stock stock = watchStockMap.get(tickerSymbol);
		watchStockList.remove(stock);
		watchStockMap.remove(tickerSymbol);
	}

	private void removeTicker(String symbolName) {
		try {
			Boolean result = userService.deleteTicker(symbolName,
					(Integer) Context.getContext().get(Constants.USERID_KEY));
			if (result == true) {
				if (streamerNettyClient != null && streamerNettyClient.getChannel() != null) {
					streamerNettyClient.getChannel().writeAndFlush(
							Constants.applicationProperties.get("unsubscribe.keyword") + symbolName + "\r\n");
					removeTickerFromList(symbolName);
				}
			} else {
				ApplicationUtil.displayAlert("Ticker not Removed");
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			logger.error("Error while removing ticker. Full details:" + e);
		}
	}

	private ScrollPane getNewsPane() {
		/*NewsBean news1 = new NewsBean("Yandex Lower: Chairman Dead, Board Shuffled", "Barrons.com", "2h");
		NewsBean news2 = new NewsBean("Why Advanced Micro Devices and Yandex's Stocks Rose in the Trailing-5-Day Period", "Barrons", "2h");
		NewsBean news3 = new NewsBean("Samsung Galaxy S7 is Doing What Apple No Longer Do", "Yahoo Finance", "2h");
		NewsBean news4 = new NewsBean("Yandex to Buy Headquarters to Reduce Dollar-Linked Rent Expenses", "Bloomberg", "2h");
		newsContainer.getChildren().addAll(news1.getFormattedNews(), news2.getFormattedNews(), news3.getFormattedNews(), news4.getFormattedNews());*/
		ScrollPane newsPane = new ScrollPane(newsContainer);
		newsContainer.prefWidthProperty().bind(newsPane.widthProperty());
		newsPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		newsPane.getStyleClass().add("background-color");
		return newsPane;
	}

	private EventHandler<ActionEvent> f1FilterButtonHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			F1FilterDialog dialog = new F1FilterDialog();
			f1FilterBean = dialog.showDialogBox(f1FilterBean);
			if (f1FilterBean != null) {
				f1StockList.clear();
				for (F1Stock f1Stock : f1StockBackupList) {
					Double price = f1Stock.lastPriceProperty().doubleValue();
					Double volume = f1Stock.lastPriceProperty().doubleValue();
					if ((price != null) && (volume != null) && (price >= f1FilterBean.getPriceMin())
							&& (price <= f1FilterBean.getPriceMax()) && (volume >= f1FilterBean.getPriceMin())
							&& (volume <= f1FilterBean.getPriceMax())) {
						f1StockList.add(f1Stock);
					}
				}
			}
		}
	};

	@SuppressWarnings("unchecked")
	private void addTableColumnsInTableViews() {
		masterListTableView.setEditable(true);
		masterListTableView.getColumns().addAll(masterSymbolColumn, masterLastPriceColumn, masterTriggerColumn, masterTimeColumn, masterSourceColumn);
		masterListTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		watchListTableView.setEditable(true);
		watchListTableView.getColumns().addAll(watchSymbolColumn, watchPriceColumn, watchGainColumn, watchVolumnColumn, watchChangeColumn);
		watchListTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		f1TableView.getColumns().addAll(f1SymbolColumn, f1PriceColumn, f1PercentChangeColumn, f1StatusColumn, f1HodTriggerColumn, f1TriggerColumn);
		f1TableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		parabolicTableView.getColumns().addAll(parabolicSymbolColumn, parabolicTriggerPriceColumn, parabolicActualColumn, parabolicStatusColumn, parabolicTimeColumn, parabolicChangeColumn);
		parabolicTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		gapTableView.getColumns().addAll(gapSymbolColumn, gapTriggerColumn, gapActualColumn, gapStatusColumn, gapTimeColumn, gapChangeColumn);
		gapTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		squeezeZoneTableView.getColumns().addAll(squeezeZoneSymbolColumn, squeezeZoneTriggerColumn, squeezeZoneActualColumn, squeezeZoneStatusColumn, squeezeZoneTimeColumn, squeezeZoneChangeColumn);
		squeezeZoneTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	private void initializeWebview(WebView webView, ChartConfig chartConfig) {
		
		/*Runnable newsFetcher = new Runnable() {
			public void run() {
				String tickerSymbol = chartConfig.getTickerSymbol();
				System.out.println("Ticker:::::" + tickerSymbol);
				Date today = new Date();
				Date yesterday = DateUtil.getModifiedStartDate(today, -1, Calendar.DATE);
				String startDate = DateUtil.getConcatDateFields(yesterday, true);
				String endDate = DateUtil.getConcatDateFields(DateUtil.getModifiedEndDate(yesterday, 0, Calendar.YEAR),	true);
				
				System.out.println(startDate + " to " + endDate);
				String serverUrl = Constants.applicationProperties.getProperty("application.server.uri");
				
			    NewsResponse newsResponse = wsClient.sendGetRequest(serverUrl + Constants.applicationProperties.getProperty("news.api").replace("{symbol}", tickerSymbol).replace("{start}", startDate).replace("{end}", endDate), NewsResponse.class);
			    List<TickerNews> newsList = newsResponse.getObjects();
			    if(newsList != null) {
			    	if(!newsBag.contains(tickerSymbol)) {
			    		newsBag.add(tickerSymbol);
			    		Gson gson = getCustomizedDateFormatterDeserializerGson();
			    		for(TickerNews news : newsList) {
			    			APINews apiFullNews = gson.fromJson(news.getJson(), APINews.class);
			    			NewsBean newsBean = new NewsBean(news.getTitle(), news.getSource(), news.getUrl(), DateUtil.formatDate(apiFullNews.getUpdated(), Constants.TIME_FORMAT), tickerSymbol);
			    			Platform.runLater(() -> {		
			    				newsContainer.getChildren().add(0, newsBean.getFormattedNews());
			    			});
			    		}			    		
			    	}
			    }
			}
		};
		
		Thread t = new Thread(newsFetcher);
		t.start();*/
		
		Gson jsonBuilder = new Gson();
		webView.autosize();
		webView.setPrefSize(500, 500);
		webView.getEngine().load(getClass().getResource("/web/content/historicalChart.html").toExternalForm());
		webView.getEngine().setOnError(event -> { System.out.println(event); });
		webView.getEngine().setOnAlert(event -> {
			if (event.getData().equals("documment_ready")) {
				webView.setId(String.valueOf(chartConfig.getChartUID()));
				Map<String, Object> chartParameters = new HashMap<String, Object>();
				chartParameters.put("HISTORY_SERVER_URI", Constants.applicationProperties.getProperty("application.server.uri"));
				chartParameters.put("HISTORY_ONE_DAY_INTERVAL", Constants.applicationProperties.getProperty("one.day.interval.api"));
				chartParameters.put("HISTORY_CUSTOM_DURATION_API", Constants.applicationProperties.getProperty("custom.duartion.api"));
				chartParameters.put("symbolName", chartConfig.getTickerSymbol());
				chartParameters.put("width", webView.getWidth());
				chartParameters.put("height", webView.getHeight());
				chartParameters.put("chartUId", chartConfig.getChartUID());
				chartParameters.put("dataDuration", chartConfig.getDataDuration());
				chartParameters.put("apiType", chartConfig.getApiType());
				chartParameters.put("indicatorParams", chartConfig.getIndicatorParams());
				chartParameters.put("trendlineParams", chartConfig.getTrendlineParams());
				chartConfig.getTrendlineParams().forEach(chart1 -> System.out.println(chart1.getiD()));
				chartParameters.put("annotationParams", chartConfig.getAnnotationParams());
				chartParameters.put("chartUIPereferences", chartConfig.getChartParams());
				chartParameters.put("horizontalTrendlineParams", chartConfig.getHorizontalTrendlineParams());
				chartConfig.getHorizontalTrendlineParams().forEach(chart1 -> System.out.println(chart1.getiD()));
				chartParameters.put(Constants.FULL_TICKER_LIST_KEY, tickers);

				webView.getEngine().executeScript("historicalChart.init('" + jsonBuilder.toJson(chartParameters) + "') ");
				webView.getEngine().setOnError(new EventHandler<WebErrorEvent>() {
					@Override
					public void handle(WebErrorEvent event) {
						System.out.println(event.getException());
					}
				});
				addWebviewDragEvent(webView);
			} else if (event.getData().split("_")[0].equals("indicatorDialog")) {
				String indicator = event.getData().split("_")[1];
				IndicatorDialog dialog = new IndicatorDialog();
				dialog.getDialog().getDialogPane().requestFocus();
				PeriodBean periodBean = dialog.showDialogBox(indicator);
				if (periodBean != null) {
					Gson gson = new Gson();
					String jsonResult = gson.toJson(periodBean).toString();
					webView.getEngine().executeScript(" historicalChart.getIndicatorValue('" + jsonResult + "') ");
				}
			} else if (event.getData().split("_")[0].equals("groupChange")) {
				String group = event.getData().split("_")[1];
				if (countTicker.containsKey(group)) {
					webView.getEngine().executeScript(
							"historicalChart.changeTickerGraph('Alasd','" + countTicker.get(group) + "','First',null)");
				} else {
					countTicker.put(group, chartConfig.getTickerSymbol());
				}
			} else if(event.getData().split("_")[0].equals("CONTEXT-MENU")) { 
				String[] underscoreSeparatedData = event.getData().split("_");
				String chartUID = underscoreSeparatedData[1];
				int lineId = Integer.parseInt(underscoreSeparatedData[2]);
				boolean isHorizontalTrendline = Boolean.valueOf(underscoreSeparatedData[3]);

				String symbol = chartConfigs.get(Integer.parseInt(chartUID)).getTickerSymbol();

				List<TrendLineParamBean> trendLineParamList = alertTrendlineBeanMap.get(symbol);
				List<TrendLineParamBean> horizontalTrendLineParamList = alertHorizontalTrendlineBeanMap.get(symbol);
				Optional<TrendLineParamBean> trendlineBeanOptional = trendLineParamList.stream().filter(trendline -> lineId == trendline.getiD()).findFirst();
				if(!trendlineBeanOptional.isPresent()) {
					trendlineBeanOptional = horizontalTrendLineParamList.stream().filter(trendline -> lineId == trendline.getiD()).findFirst();
				}
				try {			
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TrendlineConfigDialogView.fxml"));			
					Parent page = (Parent) loader.load();			
					Stage dialogStage = new Stage();			
					dialogStage.setTitle(Constants.applicationLanguage.getProperty("label.set.alert"));			
					dialogStage.initStyle(StageStyle.UTILITY);
					dialogStage.initModality(Modality.WINDOW_MODAL);			
					dialogStage.initOwner((Stage)Context.getContext().get(Constants.PARENT_STAGE));			

					Scene scene = new Scene(page);			
					scene.getStylesheets().add(getClass().getResource("/dialogBox.css").toExternalForm());			
					dialogStage.setScene(scene);			

					TrendlineConfigDialogViewController controller = loader.getController();
					if(trendlineBeanOptional.isPresent()) {
						TrendLineParamBean trendlineParamBean = trendlineBeanOptional.get();
						controller.setCrossingAboveAndDownCheckBox(trendlineParamBean.isCheckCrossingAboveAndDown());
						controller.setCrossingAboveCheckBox(trendlineParamBean.isAlertOnlyCrossAbove());
						controller.setCrossingDownCheckBox(trendlineParamBean.isAlertOnlyCrossDown());
						controller.setFlashingTextBox(trendlineParamBean.getAlertText());
						controller.setPopupWindowRadioButton(trendlineParamBean.isShowPopup());
						controller.setSoundRadioButton(trendlineParamBean.isPlaySound());
						controller.setFlashingTextRadioButton(((trendlineParamBean.getAlertText() == null) || (trendlineParamBean.getAlertText().isEmpty()))?false:true);
						if(!trendlineParamBean.isShowPopup() && !trendlineParamBean.isPlaySound() && !controller.getFlashingTextRadioButton()) {
							trendlineParamBean.setShowPopup(true);
						}

						controller.setLineColor((trendlineParamBean.getLineColor() == null)?Constants.DEFAULT_TRENDLINE_COLOR:trendlineParamBean.getLineColor());
						controller.setLineWidth((trendlineParamBean.getHeight() == null)?Constants.DEFAULT_TRENDLINE_WIDTH:trendlineParamBean.getHeight());
					}

					dialogStage.showAndWait();	
					if(trendlineBeanOptional.isPresent()) {
						TrendLineParamBean trendlineParamBean = controller.getTrendlineParams(trendlineBeanOptional.get());
						if(trendlineParamBean != null) {
							trendlineParamBean.setEnabled(true);
							saveUpdateTrendlineInDBAndLayoutList(trendlineParamBean, chartUID, isHorizontalTrendline);
							if(!isHorizontalTrendline) {
								webView.getEngine().executeScript("historicalChart.updateTrendLineParamsList("+new Gson().toJson(trendLineParamList)+")");
								webView.getEngine().executeScript("historicalChart.updateTrendLines()");
							} else {
								webView.getEngine().executeScript("historicalChart.updateHorizontalTrendLineParamsList("+new Gson().toJson(horizontalTrendLineParamList)+")");
								webView.getEngine().executeScript("historicalChart.updateHorizontalTrendline("+trendlineParamBean.getiD()+", '"+trendlineParamBean.getLineColor()+"',"+trendlineParamBean.getHeight()+")");
							}
						}
					}
				} catch(Exception e) {			
					e.printStackTrace();			
				}	
			} else if(event.getData().split("_")[0].equals("PUT-ANNOTATION")) {
				String[] underscoreSeparatedData = event.getData().split("_");
				String chartUId = underscoreSeparatedData[1];
				String dateX = underscoreSeparatedData[2];
				String annotationCoorY = underscoreSeparatedData[3];
				/*System.out.println("chartUid::" + chartUId);
				System.out.println("annoX" + annotationCoorX);
				System.out.println("annoY" + annotationCoorY);*/
			} else {
				System.out.println(event.getData());
			}
		});
	}

	private void saveUpdateTrendlineInDBAndLayoutList(TrendLineParamBean trendlineParamBean, String chartUID, boolean isHorizontalTrendline) {
		Integer userId = (Integer) Context.getContext().get(Constants.USERID_KEY);
		try {
			Integer layoutId = userService.getLayoutIdByUserId(userId);
			if(layoutId != null && layoutId > 0) {
				String layoutJson = userService.getLayoutJsonByLayoutId(layoutId);
				if(layoutJson != null) {
					Gson gson = new Gson();
					Type type = new TypeToken<ParentLayout>(){}.getType();
					ParentLayout layoutParent = gson.fromJson(layoutJson, type);
					saveUpdateTrendlineParamBeanInLayoutNodeListAndDB(layoutParent.getLayoutList(), chartUID, trendlineParamBean, isHorizontalTrendline);
					saveUpdateTrendlineParamBeanInLayoutNodeListAndDB(layoutList, chartUID, trendlineParamBean, isHorizontalTrendline);
					userService.updateLayoutJson(layoutId, gson.toJson(layoutParent));
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	//method to delete trendline
	private void deleteTrendlineInDBAndLayoutList(Integer lineId, String chartUID) {
		Integer userId = (Integer) Context.getContext().get(Constants.USERID_KEY);
		try {
			Integer layoutId = userService.getLayoutIdByUserId(userId);
			if(layoutId != null && layoutId > 0) {
				String layoutJson = userService.getLayoutJsonByLayoutId(layoutId);
				if(layoutJson != null) {
					Gson gson = new Gson();
					Type type = new TypeToken<ParentLayout>(){}.getType();
					ParentLayout layoutParent = gson.fromJson(layoutJson, type);
					deleteTrendlineParamBeanInLayoutNodeListAndDB(layoutParent.getLayoutList(), chartUID, lineId);
					deleteTrendlineParamBeanInLayoutNodeListAndDB(layoutList, chartUID, lineId);
					userService.updateLayoutJson(layoutId, gson.toJson(layoutParent));
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteTrendlineParamBeanInLayoutNodeListAndDB(List<LayoutNode> layoutList, String chartUID, Integer lineId) {
		boolean isItemRemoved = false;
		for (LayoutNode layoutNode : layoutList) {
			if((layoutNode.getChartUID() != null) && (Integer.toString(layoutNode.getChartUID()).equalsIgnoreCase(chartUID))) {
				List<TrendLineParamBean> trendlineParamBeanList = layoutNode.getChartConfig().getHorizontalTrendlineParams();
				List<TrendLineParamBean> horizontalTrendlineParamBeanList = layoutNode.getChartConfig().getTrendlineParams();

				Iterator<TrendLineParamBean> iterator = trendlineParamBeanList.listIterator();
				while(iterator.hasNext()) {
					TrendLineParamBean trendLineParamBean = iterator.next();
					if(trendLineParamBean.getiD().intValue() == lineId.intValue()) {
						iterator.remove();
						isItemRemoved = true;
						break;
					}
				}

				if(!isItemRemoved) {
					iterator = horizontalTrendlineParamBeanList.listIterator();
					while(iterator.hasNext()) {
						TrendLineParamBean trendLineParamBean = iterator.next();
						if(trendLineParamBean.getiD().intValue() == lineId.intValue()) {
							iterator.remove();
							isItemRemoved = true;
							break;
						}
					}

					if(isItemRemoved) {
						return;
					}
				}
			}
		}
	}

	private void saveUpdateTrendlineParamBeanInLayoutNodeListAndDB(List<LayoutNode> layoutList, String chartUID, TrendLineParamBean trendlineParamBean, boolean isHorizontalTrendline) {
		for (LayoutNode layoutNode : layoutList) {
			if((layoutNode.getChartUID() != null) && (Integer.toString(layoutNode.getChartUID()).equalsIgnoreCase(chartUID))) {
				List<TrendLineParamBean> trendlineParamBeanList = null;
				if(isHorizontalTrendline) {
					trendlineParamBeanList = layoutNode.getChartConfig().getHorizontalTrendlineParams();
				} else {
					trendlineParamBeanList = layoutNode.getChartConfig().getTrendlineParams();
				}

				boolean isEntryNotFound = true;
				Iterator<TrendLineParamBean> iterator = trendlineParamBeanList.listIterator();
				while(iterator.hasNext()) {
					TrendLineParamBean trendLineParam = iterator.next();
					if(trendlineParamBean.getiD().intValue() == trendLineParam.getiD().intValue()) {
						if(isHorizontalTrendline) {
							layoutNode.getChartConfig().getHorizontalTrendlineParams().set(layoutNode.getChartConfig().getHorizontalTrendlineParams().indexOf(trendLineParam), trendlineParamBean);
						} else {
							layoutNode.getChartConfig().getTrendlineParams().set(layoutNode.getChartConfig().getTrendlineParams().indexOf(trendLineParam), trendlineParamBean);
						}
						isEntryNotFound = false;
					}
				}

				if(isEntryNotFound) {
					if(isHorizontalTrendline) {
						layoutNode.getChartConfig().getHorizontalTrendlineParams().add(trendlineParamBean);
					} else {
						layoutNode.getChartConfig().getTrendlineParams().add(trendlineParamBean);
					}
				}
			}
		}
	}

	private void addWebviewDragEvent(WebView webView) {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				webView.widthProperty().addListener(new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth,
							Number newSceneWidth) {
						// webView.getEngine().executeScript("
						// historicalChart.updateChartDimensions('First','"+newSceneWidth+"','"+webView.getHeight()+"')
						// " );
						webView.getEngine().executeScript(" historicalChart.updateChartDimensions('First','"
								+ newSceneWidth + "','" + webView.getHeight() + "') ");
					}
				});
				webView.heightProperty().addListener(new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight,
							Number newSceneHeight) {
						// webView.getEngine().executeScript("
						// historicalChart.updateChartDimensions('First','"+webView.getWidth()+"','"+newSceneHeight+"')
						// " );
						webView.getEngine().executeScript(" historicalChart.updateChartDimensions('First','"
								+ webView.getWidth() + "','" + newSceneHeight + "') ");
					}
				});
				return null;
			}
		};
		new Thread(task).start();
	}

	private void initializeComboBoxTickerAndDuration(DockNode dockNode, ChartConfig chartConfig) {
		ChartTitleBar chartTitleBar = ((ChartTitleBar) dockNode.getChildren().get(0));
		chartTitleBar.setTickerCode(chartConfig.getTickerSymbol());
		if(chartConfig.getApiType() == Constants.API_TYPE.INTER_DAY.getType()) {
			//(new IntervalComboBoxBean(TickerDuration.ONE_MIN.getTickerDuration()+Constants.MINUTE, TickerDuration.DAILY.getTickerDuration()))
			chartTitleBar.getTimeDurationDropDown().getSelectionModel().selectLast();
		} else {
			ObservableList<IntervalComboBoxBean> items = chartTitleBar.getTimeDurationDropDown().getItems();
			int itemIndex = -1;
			for(int i = 0, length = items.size(); i < length; i++) {
				IntervalComboBoxBean item = items.get(i);
				if(item.getInterval().equalsIgnoreCase(String.valueOf(chartConfig.getDataDuration()))) { //String comparison due to "daily" item
					itemIndex = i;
					break;
				}
			}
			chartTitleBar.getTimeDurationDropDown().getSelectionModel().select(itemIndex);
			/*if(chartConfig.getDataDuration() == Integer.parseInt(TickerDuration.ONE_MIN.getMinutes())){
				//chartTitleBar.getTimeDurationDropDown().setValue(new IntervalComboBoxBean(TickerDuration.ONE_MIN.getTickerDuration()+Constants.MINUTE, TickerDuration.ONE_MIN.toString()));
			} else if(chartConfig.getDataDuration() == Integer.parseInt(TickerDuration.TWO_MIN.getMinutes())){
				//chartTitleBar.getTimeDurationDropDown().setValue(new IntervalComboBoxBean(TickerDuration.TWO_MIN.getTickerDuration()+Constants.MINUTE, TickerDuration.TWO_MIN.getTickerDuration()));
			} else if(chartConfig.getDataDuration() == Integer.parseInt(TickerDuration.FIVE_MIN.getMinutes())){
				//chartTitleBar.getTimeDurationDropDown().setValue(new IntervalComboBoxBean(TickerDuration.FIVE_MIN.getTickerDuration()+Constants.MINUTE, TickerDuration.FIVE_MIN.getTickerDuration()));
			}*/
		}
	}

	private EventHandler<ActionEvent> addChartButtonHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			Random random = new Random();
			int chartUID = random.nextInt();
			String defaultChartConfig = Constants.applicationProperties.getProperty("chart.ui.default.properties");
			ChartPreferenceBean chartPreferences = new Gson().fromJson(defaultChartConfig, ChartPreferenceBean.class);
			List<IndicatorParamBean> indicators = new ArrayList<IndicatorParamBean>();
			indicators.add(new IndicatorParamBean(random.nextInt(), 2, IndicatorType.VMA.toString()));
			
			ChartConfig chartConfig = new ChartConfig(chartUID, "AAPL", 1, indicators, "#ffffff",1, new ArrayList<TrendLineParamBean>(),new ArrayList<AnnotationParamBean>(), chartPreferences, new ArrayList<TrendLineParamBean>());
			LayoutNode node = new LayoutNode(null, null, Constants.CHART_NODE, null, chartUID, true, 500.0, 500.0, 50, 50, chartConfig, DockPanePosition.MIDDLE);
			registerChartTickerForReceivingData(chartConfig);
			node.setFloating(true);
			layoutList.add(node);
			addChartDockNode(chartConfig, node);
		}
	};

	private void addDimensionChangeListenerToNode(DockNode dockNode) {
		Platform.runLater(new Runnable() {
			public void run() {
				dockNode.floatingProperty().addListener(new InvalidationListener() {
					@Override
					public void invalidated(Observable observable) {
						dockNode.getLayoutNode().setFloating(dockNode.isFloating());
						registerPositionChangeListener(dockNode);
					}
				});
				dockNode.widthProperty().addListener(new InvalidationListener() {
					@Override
					public void invalidated(Observable observable) {
						dockNode.getLayoutNode().setWidth(dockNode.getWidth());
					}
				});
				dockNode.heightProperty().addListener(new InvalidationListener() {
					@Override
					public void invalidated(Observable observable) {
						dockNode.getLayoutNode().setHeight(dockNode.getHeight());
					}
				});
			}
		});
		registerPositionChangeListener(dockNode);
	}

	private void registerPositionChangeListener(DockNode dockNode) {
		Platform.runLater(new Runnable() {
			public void run() {
				if(dockNode.getScene() != null) {
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					dockNode.getScene().getWindow().xProperty().addListener(new InvalidationListener() {
						@Override
						public void invalidated(Observable observable) {
							if(dockNode.isFloating()) {
								dockNode.getLayoutNode().setxPosPercent((dockNode.getScene().getWindow().getX()/screenSize.getWidth())*100);
							} else {
								dockNode.getLayoutNode().setxPosPercent(0.0);
							}
						}
					});
					dockNode.getScene().getWindow().yProperty().addListener(new InvalidationListener() {
						@Override
						public void invalidated(Observable observable) {
							if(dockNode.isFloating()) {
								dockNode.getLayoutNode().setyPosPercent((dockNode.getScene().getWindow().getY()/screenSize.getHeight())*100);
							} else {
								dockNode.getLayoutNode().setyPosPercent(0.0);
							}
						}
					});
				}
			}
		});
	}

	private EventHandler<ActionEvent> chartCloseButtonHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			Button closeButton = (Button)event.getSource();
			String closeButtonID = closeButton.getId();
			String chartUID = closeButtonID.substring(0, closeButtonID.length()-5);

			DockNode dockNode = dockNodesMap.get(chartUID);

			ChartTitleBar titleBar = (ChartTitleBar) dockNode.getDockTitleBar();

			String tickerCode = titleBar.getTickerCode();
			int tickerCodeCount = 0;
			for (ChartConfig chartConfig : chartConfigs.values()) 
				if(chartConfig.getTickerSymbol().equalsIgnoreCase(tickerCode))
					tickerCodeCount++;

			if(tickerCodeCount == 1)
				if(tickerDataMapInter.containsKey(tickerCode)) 
					tickerDataMapInter.remove(tickerCode);
				else
					tickerDataMapIntra.remove(tickerCode);

			removeEntryFromTickerTrendlineDataSet(chartUID);
			dockNode.close();
			removeEntryFromConfigLayoutMapsAndList(dockNode);
			//			checkIfOneNodeIsRemainingInPane(true);
		}
	};

	/*private void checkIfOneNodeIsRemainingInPane(boolean registerEventHandler) {
		List<Node> nodeList = middleDockPane.getChildren();
		DockNode dockNode = null;
		int dockNodeCount = 0;
		for (Node node : nodeList) {
			if(node instanceof DockNode) {
				dockNodeCount++;
				dockNode = (DockNode) node;
			}
		}
		if(registerEventHandler) {
			if(dockNodeCount == 2) {
				DockTitleBar titleBar = ((DockNode) nodeList.get(0)).getDockTitleBar();
				if(!((DockNode) nodeList.get(0)).getDockTitleBar().isHandlerRegistered()) {
					titleBar.addEventHandlers();
				}
				titleBar = ((DockNode) nodeList.get(1)).getDockTitleBar();
				if(!((DockNode) nodeList.get(1)).getDockTitleBar().isHandlerRegistered()) {
					titleBar.addEventHandlers();
				}
			}
		} else {
			if(dockNodeCount == 1) {
				DockTitleBar titleBar = dockNode.getDockTitleBar();
				titleBar.removeEventHandlers();
			}
		}
	}*/

	public void removeEntryFromTickerTrendlineDataSet(String entry) {
		for (String data : tickerTrendlineDataSet) {
			if(data.startsWith(entry)) {
				tickerTrendlineDataSet.remove(data);
			}
		}
	}

	private void copyLayoutNodeToAnother(LayoutNode rightNode, LayoutNode nodeToBeRemoved) {
		nodeToBeRemoved.setChartConfig(rightNode.getChartConfig());
		nodeToBeRemoved.setChartUID(rightNode.getChartUID());
		nodeToBeRemoved.setFloating(rightNode.isFloating());
		nodeToBeRemoved.setHeight(rightNode.getHeight());
		nodeToBeRemoved.setNodeTitle(rightNode.getNodeTitle());
		nodeToBeRemoved.setNodeType(rightNode.getNodeType());
		nodeToBeRemoved.setWidth(rightNode.getWidth());
		nodeToBeRemoved.setxPosPercent(rightNode.getxPosPercent());
		nodeToBeRemoved.setyPosPercent(rightNode.getyPosPercent());
	}

	private void removeEntryFromConfigLayoutMapsAndList(DockNode dockNode) {
		if(dockNodesMap.values().contains(dockNode)) {
			dockNodesMap.values().remove(dockNode);
		}

		if(chartConfigs.containsKey(dockNode.getLayoutNode().getChartConfig().getChartUID())) {
			chartConfigs.remove(dockNode.getLayoutNode().getChartConfig().getChartUID());
		}

		if(layoutList.contains(dockNode.getLayoutNode())) {
			LayoutNode nodeToBeRemoved = dockNode.getLayoutNode();
			LayoutNode leftNode = null;
			LayoutNode rightNode = null;
			LayoutNode topNode = null;
			LayoutNode bottomNode = null;
			for (LayoutNode layoutNode : layoutList) {
				if((layoutNode.getDockPanePosition() == DockPanePosition.MIDDLE) && (layoutNode.getRelativeDockNode() != null) && (layoutNode != nodeToBeRemoved) && (layoutNode.getRelativeDockNode().equalsIgnoreCase(Integer.toString(nodeToBeRemoved.getChartUID())))) {
					if(layoutNode.getDockPos() == DockPos.LEFT) {
						leftNode = layoutNode;
					} else if(layoutNode.getDockPos() == DockPos.RIGHT) {
						rightNode = layoutNode;
					} else if(layoutNode.getDockPos() == DockPos.TOP) {
						topNode = layoutNode;
					} else if(layoutNode.getDockPos() == DockPos.BOTTOM) {
						bottomNode = layoutNode;
					}
				}
			}

			if(leftNode != null) {
				copyLayoutNodeToAnother(leftNode, nodeToBeRemoved);
				layoutList.remove(leftNode);

				if(rightNode != null) {
					rightNode.setRelativeDockNode(Integer.toString(nodeToBeRemoved.getChartUID()));
				} 
				if(topNode != null) {
					topNode.setRelativeDockNode(Integer.toString(nodeToBeRemoved.getChartUID()));
				}
				if(bottomNode != null) {
					bottomNode.setRelativeDockNode(Integer.toString(nodeToBeRemoved.getChartUID()));
				}
			} else if(rightNode != null) {
				copyLayoutNodeToAnother(rightNode, nodeToBeRemoved);
				layoutList.remove(rightNode);
				if(topNode != null) {
					topNode.setRelativeDockNode(Integer.toString(nodeToBeRemoved.getChartUID()));
				}
				if(bottomNode != null) {
					bottomNode.setRelativeDockNode(Integer.toString(nodeToBeRemoved.getChartUID()));
				}
			} else if (topNode != null) {
				copyLayoutNodeToAnother(topNode, nodeToBeRemoved);

				layoutList.remove(topNode);

				if(bottomNode != null) {
					bottomNode.setRelativeDockNode(Integer.toString(nodeToBeRemoved.getChartUID()));
				}
			} else if(bottomNode != null) {
				copyLayoutNodeToAnother(bottomNode, nodeToBeRemoved);
				layoutList.remove(bottomNode);
			} else {
				layoutList.remove(nodeToBeRemoved);
			}
		}
	}

	private void initializeDefaultWebViews() {
		String layoutJson = null;
		Integer userId = (Integer) Context.getContext().get(Constants.USERID_KEY);
		try {
			layoutId = userService.getLayoutIdByUserId(userId);
			if(layoutId != null && layoutId > 0) {
				layoutJson = userService.getLayoutJsonByLayoutId(layoutId);
			} else {
				layoutJson = Constants.applicationProperties.getProperty("layout.default.properties");
				Integer newLayoutId = userService.saveLayoutListByUserId(userId, layoutJson, Constants.applicationLanguage.getProperty("label.default.layout"));
				if(newLayoutId != null) {
					userService.saveLayoutIdByUserId(userId, newLayoutId);
				}
			}
			if(layoutJson != null) {
				Gson gson = new Gson();
				Type type = new TypeToken<ParentLayout>(){}.getType();
				ParentLayout layoutParent = gson.fromJson(layoutJson, type);
				dashboardController.drawCustomSavedLayout(layoutParent);
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	private void registerInternalSocketServerHandlers() {
		InternalSocketServer.getInstance().getServerHandler().addEventListener("subscribeTicker", String.class,
				new DataListener<String>() {
			public void onData(SocketIOClient client, String data, AckRequest ackRequest) {
				InternalSocketServer.getInstance().getServerHandler().getBroadcastOperations()
				.sendEvent("subscribeTicker", data);
				if (!watchChartStockMap.containsKey(data)) {
					watchChartStockMap.put(data, data);
					oneMinuteNettyClient.getChannel().writeAndFlush(data + "\r\n");
				}
			}
		});

		InternalSocketServer.getInstance().getServerHandler().addEventListener("subscribeOneMinuteCandle", String.class,
				new DataListener<String>() {
			public void onData(SocketIOClient client, String data, AckRequest ackRequest) {
				if(data != null) {
					String cachedTicker = watchChartStockMap.get(data);
					if(cachedTicker == null) {						
						watchChartStockMap.put(data, data);
						streamerNettyClient.getChannel().writeAndFlush(data + "\r\n");
					}
					oneMinuteNettyClient.getChannel().writeAndFlush(data + "\r\n");
				}
			}
		});

		InternalSocketServer.getInstance().getServerHandler().addEventListener("unSubscribeTicker", String.class,
				new DataListener<String>() {
			public void onData(SocketIOClient client, String data, AckRequest ackRequest) {
				InternalSocketServer.getInstance().getServerHandler().getBroadcastOperations()
				.sendEvent("unSubscribeTicker", data);
				if (watchChartStockMap.containsKey(data)) {
					watchChartStockMap.remove(data);
					oneMinuteNettyClient.getChannel()
					.writeAndFlush(Constants.STREAMER_UNSUBSCRIBE_TICKER_PREFIX + data + "\r\n");
					if (!watchStockMap.containsKey(data)) {
						streamerNettyClient.getChannel()
						.writeAndFlush(Constants.STREAMER_UNSUBSCRIBE_TICKER_PREFIX + data + "\r\n");
					}
				}
			}
		});

		InternalSocketServer.getInstance().getServerHandler().addEventListener("publishTickerDataOneMinute",
				String.class, new DataListener<String>() {
			public void onData(SocketIOClient client, String data, AckRequest ackRequest) {
				InternalSocketServer.getInstance().getServerHandler().getBroadcastOperations()
				.sendEvent("publishTickerDataOneMinute", data);
			}
		});

		InternalSocketServer.getInstance().getServerHandler().addEventListener("publishTickerData", String.class,
				new DataListener<String>() {
			public void onData(SocketIOClient client, String data, AckRequest ackRequest) {
				JSONObject tempObj = new JSONObject(data);
				if (tempObj.get("symbolName").equals("AAPL")) {
					// System.out.println(data);
				}
				InternalSocketServer.getInstance().getServerHandler().getBroadcastOperations()
				.sendEvent("publishTickerData", data);
				try {
					if (data.startsWith("{")) {
						Stock stock = JSONToStockMapper.toWatchStock(data);
						Platform.runLater(() -> {
							updateWatchListTableView(stock);
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(
							"Errornous data received in publishTickerData socketServer. Full details: " + e);
				}
			}
		});

		InternalSocketServer.getInstance().getServerHandler().addEventListener("editTrendLineRequest",
				TrendLine.class, new DataListener<TrendLine>() {
			public void onData(SocketIOClient client, TrendLine data, AckRequest ackRequest) {
				String chartUID  = data.getChartUID();
				ChartConfig selectedChartConfig = chartConfigs.get(Integer.parseInt(chartUID));

				for(TrendLineParamBean currentTrendLine: selectedChartConfig.getTrendlineParams() ){
					if(currentTrendLine.getiD().intValue() == data.getTrendlineData().getiD().intValue()){
						currentTrendLine.setX1(data.getTrendlineData().getX1());
						currentTrendLine.setX2(data.getTrendlineData().getX2());
						currentTrendLine.setY1(data.getTrendlineData().getY1());
						currentTrendLine.setY2(data.getTrendlineData().getY2());
						break;
					}
				}
				emitTrendLineDataToSameColorGroup(selectedChartConfig);
			}                                                                                         
		});

		InternalSocketServer.getInstance().getServerHandler().addEventListener("newAnnotationRequest",
				AnnotationRequest.class, new DataListener<AnnotationRequest>() {
			public void onData(SocketIOClient client, AnnotationRequest data, AckRequest ackRequest) {
				String chartUID = data.getChartUID();
				ChartConfig selectedChartConfig = chartConfigs.get(Integer.parseInt(chartUID));
				selectedChartConfig.getAnnotationParams().add(data.getAnnotationData());

			}                                                                                         
		});

		InternalSocketServer.getInstance().getServerHandler().addEventListener("editAnnotationRequest",
				AnnotationRequest.class, new DataListener<AnnotationRequest>() {
			public void onData(SocketIOClient client, AnnotationRequest data, AckRequest ackRequest) {
				String chartUID = data.getChartUID();
				ChartConfig selectedChartConfig = chartConfigs.get(Integer.parseInt(chartUID));

				for(AnnotationParamBean currentAnnotation: selectedChartConfig.getAnnotationParams()){
					if(currentAnnotation.getiD().intValue() == data.getAnnotationData().getiD().intValue()){
						currentAnnotation.setX1(data.getAnnotationData().getX1());
						currentAnnotation.setY1(data.getAnnotationData().getY1());
						break;
					}
				}
			}                                                                                         
		});

		InternalSocketServer.getInstance().getServerHandler().addEventListener("removeAnnotationRequest",
				AnnotationRequest.class, new DataListener<AnnotationRequest>() {
			public void onData(SocketIOClient client, AnnotationRequest data, AckRequest ackRequest) {
				String chartUID = data.getChartUID();
			}
		});

		InternalSocketServer.getInstance().getServerHandler().addEventListener("removeTrendlineRequest",
				TrendLine.class, new DataListener<TrendLine>() {
			public void onData(SocketIOClient client, TrendLine data, AckRequest ackRequest) {
				String chartUID = data.getChartUID();
				TrendLineParamBean oldTrendLine = data.getTrendlineData();
				deleteTrendlineInDBAndLayoutList(oldTrendLine.getiD(), chartUID);
				Optional<ChartConfig> selectedChart = chartConfigs.values().stream().filter(chartConfig -> chartConfig.getChartUID() == Integer.parseInt(chartUID)).findFirst();
				if(selectedChart.isPresent()) {
					String ticker = selectedChart.get().getTickerSymbol();
					List<TrendLineParamBean> horizontalTrendlines = alertHorizontalTrendlineBeanMap.get(ticker);
					List<TrendLineParamBean> trendlines = alertTrendlineBeanMap.get(ticker);
					tickerTrendlineDataSet.remove(chartUID+Constants.UNDERSCORE+oldTrendLine.getiD());
					removeTrendlineFromMap(horizontalTrendlines, oldTrendLine.getiD());
					removeTrendlineFromMap(trendlines, oldTrendLine.getiD());

				}
			}
		});

		InternalSocketServer.getInstance().getServerHandler().addEventListener("newtrendLineRequest",
				TrendLine.class, new DataListener<TrendLine>() {
			public void onData(SocketIOClient client, TrendLine data, AckRequest ackRequest) {
				String chartUID = data.getChartUID();
				ChartConfig selectedChartConfig = chartConfigs.get(Integer.parseInt(chartUID));
				TrendLineParamBean newTrendLine = data.getTrendlineData();
				newTrendLine.setPlaySound(true);
				newTrendLine.setShowPopup(true);
				newTrendLine.setEnabled(false);
				newTrendLine.setTickerName(selectedChartConfig.getTickerSymbol());
				String tickerSymbol = selectedChartConfig.getTickerSymbol();
				selectedChartConfig.getTrendlineParams().add(data.getTrendlineData());
				if(alertTrendlineBeanMap.get(tickerSymbol) != null) {
					alertTrendlineBeanMap.get(selectedChartConfig.getTickerSymbol()).add(newTrendLine);
				} else {
					List<TrendLineParamBean> newTrendLineList = new ArrayList<TrendLineParamBean>();
					newTrendLineList.add(newTrendLine);
					alertTrendlineBeanMap.put(tickerSymbol, newTrendLineList);	
				}
				tickerTrendlineDataSet.add(chartUID+Constants.UNDERSCORE+newTrendLine.getiD());
				double slope = calculateSlope(newTrendLine.getX1Index(), newTrendLine.getX2Index(), newTrendLine.getY1(), newTrendLine.getY2());
				newTrendLine.setSlope(slope);
				saveUpdateTrendlineInDBAndLayoutList(newTrendLine, chartUID, false);
				emitTrendLineDataToSameColorGroup(selectedChartConfig);
			}
		});

		InternalSocketServer.getInstance().getServerHandler().addEventListener("horizontalTrendlineAdded", TrendLine.class, new DataListener<TrendLine>() {
			public void onData(SocketIOClient client, TrendLine data, AckRequest ackRequest) {
				String chartUID = data.getChartUID();
				ChartConfig selectedChartConfig = chartConfigs.get(Integer.parseInt(chartUID));
				String tickerSymbol = selectedChartConfig.getTickerSymbol();
				TrendLineParamBean newTrendLine = data.getTrendlineData();
				newTrendLine.setPlaySound(true);
				newTrendLine.setShowPopup(true);
				newTrendLine.setEnabled(false);
				newTrendLine.setTickerName(selectedChartConfig.getTickerSymbol());
				if(alertHorizontalTrendlineBeanMap.get(tickerSymbol) != null) {
					alertHorizontalTrendlineBeanMap.get(selectedChartConfig.getTickerSymbol()).add(newTrendLine);
				} else {
					List<TrendLineParamBean> newTrendLineList = new ArrayList<TrendLineParamBean>();
					newTrendLineList.add(newTrendLine);
					alertHorizontalTrendlineBeanMap.put(tickerSymbol, newTrendLineList);	
				}
				tickerTrendlineDataSet.add(chartUID+Constants.UNDERSCORE+newTrendLine.getiD());
				for(LayoutNode layoutNode : layoutList) {
					if((layoutNode.getChartConfig() != null) && (layoutNode.getChartConfig().getChartUID() == Integer.parseInt(data.getChartUID()))) {
						saveUpdateTrendlineInDBAndLayoutList(data.getTrendlineData(), data.getChartUID(), true);
					}
				}
			}
		});

		InternalSocketServer.getInstance().getServerHandler().addEventListener("groupColorChange",
				GroupColorRequest.class, new DataListener<GroupColorRequest>() {
			public void onData(SocketIOClient client, GroupColorRequest data, AckRequest ackRequest) {

				ChartConfig selectedChartConfig = chartConfigs.get(Integer.parseInt(data.getChartUID()));
				selectedChartConfig.setGroupColor(data.getGroupColor());
				if (!data.getGroupColor().equals("#ffffff")) {
					List<ChartConfig> sameGroupChartList = ChartGroupUtil.getSameGroupChart(chartConfigs,
							selectedChartConfig);

					for (ChartConfig currentChartConfig : sameGroupChartList) {
						selectedChartConfig.setTickerSymbol(currentChartConfig.getTickerSymbol());
						/*String trendLineParam = new String(currentChartConfig.getTrendLineParamBean().getTrendlineParam());
						TrendLineParamBean trendLineParamBean = new TrendLineParamBean(currentChartConfig.getTrendLineParamBean().getTrendlineCount(),trendLineParam );
						selectedChartConfig.setTrendLineParamBean(trendLineParamBean);*/

						List<TrendLineParamBean> trendLineParam = new ArrayList<>(currentChartConfig.getTrendlineParams());
						selectedChartConfig.setTrendlineParams(trendLineParam);


						DockNode dockNode = dockNodesMap.get(Integer.toString(selectedChartConfig.getChartUID()));
						ChartTitleBar chartTitleBar = (ChartTitleBar) dockNode.getDockTitleBar();

						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								EventHandler<ActionEvent> tickerDropDownEventHandler = chartTitleBar
										.getTickerDropDown().getOnAction();
								chartTitleBar.getTickerDropDown().setOnAction(null);
								chartTitleBar.getTickerDropDown()
								.setValue(selectedChartConfig.getTickerSymbol());
								chartTitleBar.getTickerDropDown().setOnAction(tickerDropDownEventHandler);
							}
						});

						Gson jsonBuilder = new Gson();
						Map<String, Object> chartParameters = new HashMap<String, Object>();
						chartParameters.put("chartUID", selectedChartConfig.getChartUID());
						chartParameters.put("tickerSymbol", selectedChartConfig.getTickerSymbol());
						if (Integer.parseInt(data.getApiSerial()) == Constants.API_TYPE.INTER_DAY.getType()) {
							chartParameters.put("dataDuration", "Daily");
						} else {
							chartParameters.put("dataDuration", selectedChartConfig.getDataDuration());
						}
						chartParameters.put("indicatorParam", selectedChartConfig.getIndicatorParams());
						chartParameters.put("trendlineParam", selectedChartConfig.getTrendlineParams());
						chartParameters.put("annotationParam", selectedChartConfig.getAnnotationParams());

						InternalSocketServer.getInstance().getServerHandler().getBroadcastOperations()
						.sendEvent("changeColorGroup", jsonBuilder.toJson(chartParameters));

						break;
					}
				}
			}
		});

		InternalSocketServer.getInstance().getServerHandler().addEventListener("splitData",
				SocketTickerDataObject.class, new DataListener<SocketTickerDataObject>() {
			@Override
			public void onData(SocketIOClient arg0, SocketTickerDataObject ticker, AckRequest arg2)
					throws Exception {

				/*
				 * List<IndicatorParamBean> indicatorParamsArr = new
				 * ArrayList<IndicatorParamBean>(); IndicatorParamBean
				 * indicatorParamBean = new IndicatorParamBean(14, 0, 0,
				 * 0, 0); indicatorParamBean.setAlgorithm(Constants.
				 * IndicatorType.RSI.toString());
				 * indicatorParamsArr.add(indicatorParamBean);
				 */
				// ticker.setChartUID(ticker.getChartUID());
				// ticker.setIndicatorParams(indicatorParamsArr);
				String groupColor = null;
				ChartConfig chartConfig = chartConfigs.get(ticker.getChartUID());
				int apiSerial = Integer.parseInt(ticker.getApiSerial());
				if (chartConfig == null) {
					/*chartConfigs.put(ticker.getChartUID(),
							new ChartConfig(ticker.getChartUID(), ticker.getTicker(),
									Integer.parseInt(String.valueOf(ticker.getSplitDuration().charAt(0))), ticker.getIndicatorParams(),
									"#ffffff", 1));
					groupColor = "#ffffff";*/
				} else {
					if (apiSerial != Constants.API_TYPE.INTER_DAY.getType()){
						chartConfig.setDataDuration(Integer.parseInt(String.valueOf(ticker.getSplitDuration())));
					}
					// chartConfig.setIndicatorParams(ticker.getIndicatorParams());
					ticker.setIndicatorParams(chartConfig.getIndicatorParams());
					chartConfig.setTickerSymbol(ticker.getTicker());
					groupColor = chartConfig.getGroupColor();
				}
				// chartConfigs.put(2, new ChartConfig(2, "AAPL", 2,
				// null));

				List<AbstractTickerCandlePriceHistory> candleData = null;
				Map<String, Object> fullData = new HashMap<String, Object>();
				if (apiSerial == Constants.API_TYPE.INTRA_DAY.getType()) {
					if (!tickerDataMapIntra.containsKey(ticker.getTicker())) {
						mapOneMinuteData(ticker);
					}

				} else if (apiSerial == Constants.API_TYPE.INTER_DAY.getType()) {
					if (!tickerDataMapInter.containsKey(ticker.getTicker())) {
						mapOneMinuteData(ticker);
					}
				}
				/*
				 * else if(apiSerial == 3){
				 * if(tickerDataMapIntra.containsKey(ticker.getTicker())
				 * ){ if(tickerDataMapIntra.get(ticker.getTicker()).
				 * getNoOfDays() == 1){ mapOneMinuteData(ticker); } } }
				 */
				if(ticker.getSplitDuration().equalsIgnoreCase(TickerDuration.DAILY.toString())) {
					candleData = tickerDataUtil.getSplittedData(tickerDataMapIntra, tickerDataMapInter, Integer.valueOf(ticker.getSplitDuration()), ticker.getTicker(), Integer.valueOf(ticker.getApiSerial()));
				} else {
					candleData = tickerDataUtil.getSplittedData(tickerDataMapIntra, tickerDataMapInter, Integer.valueOf(String.valueOf(ticker.getSplitDuration())), ticker.getTicker(), Integer.valueOf(ticker.getApiSerial()));
				}


				Iterator<AbstractTickerCandlePriceHistory> historyCandleDataItr = candleData.iterator();
				while(historyCandleDataItr.hasNext()) {
					AbstractTickerCandlePriceHistory historyCandle = historyCandleDataItr.next();
					if(!((historyCandle.getHigh().compareTo(0d) != 0) && (historyCandle.getLow().compareTo(0d) != 0) && (historyCandle.getOpen().compareTo(0d) != 0) && (historyCandle.getClose().compareTo(0d) != 0))) {
						historyCandleDataItr.remove();
					}
				}
				
				
				fullData.put(Constants.CANDLE_DATA_KEY, candleData);
				fullData.put("chartUID", ticker.getChartUID());
				fullData.put("groupColor", groupColor);

				Map<String, List<UITickerCalculatedSymbolData>> indicatorDataMap = new HashMap<>();

				for (IndicatorParamBean indicatorParam : ticker.getIndicatorParams()) {
					SocketCalculateDataObject calculationParams = new SocketCalculateDataObject(
							ticker.getTicker(), Integer.valueOf(ticker.getApiSerial()),
							indicatorParam.getAlgorithm(), indicatorParam.getPeriod(),
							indicatorParam.getFastPeriod(), indicatorParam.getFastKPeriod(),
							indicatorParam.getSlowPeriod(), indicatorParam.getSlowDPeriod(),
							indicatorParam.getSlowKPeriod(), indicatorParam.getSignalPeriod(),
							indicatorParam.getChartIndex());
					calculationParams.setChartUID(String.valueOf(ticker.getChartUID()));
					indicatorDataMap.put(indicatorParam.getIndicatorUID().toString(),
							getCalculatedData(calculationParams, candleData));
				}
				fullData.put("indicatorData", indicatorDataMap);

				Gson jsonBuilder = new Gson();
				InternalSocketServer.getInstance().getServerHandler().getBroadcastOperations().sendEvent("dataSplitted", jsonBuilder.toJson(fullData));
			}
		});

		InternalSocketServer.getInstance().getServerHandler().addEventListener("unsubscribeTicker", String.class,
				new DataListener<String>() {
			public void onData(SocketIOClient client, String tickerSymbol, AckRequest ackRequest) {
				tickerDataMapIntra.remove(tickerSymbol);
				tickerDataMapInter.remove(tickerSymbol);
			}
		});
		InternalSocketServer.getInstance().getServerHandler().addEventListener("addGraph", SocketCalculateDataObject.class, new DataListener<SocketCalculateDataObject>() {
			@Override
			public void onData(SocketIOClient client, SocketCalculateDataObject data, AckRequest ackSender) throws Exception {
				Random indicatorUIDGenerator = new Random();
				Integer indicatorUID = indicatorUIDGenerator.nextInt();
				ChartConfig selectedChartConfig = chartConfigs.get(Integer.parseInt(data.getChartUID()));

				if ((selectedChartConfig != null) && !(data.getAlgorithm().equalsIgnoreCase(IndicatorType.VMA.getIndicatorType()))) {
					selectedChartConfig.getIndicatorParams()
					.add(new IndicatorParamBean(indicatorUID, data.getPeriod(), data.getFastPeriod(),
							data.getSlowPeriod(), data.getSignalPeriod(), data.getFastKPeriod(),
							data.getSlowKPeriod(), data.getSlowDPeriod(), data.getAlgorithm()));
				}
				List<AbstractTickerCandlePriceHistory> candleData = null;
				if(selectedChartConfig.getApiType() == 2){
					candleData = tickerDataUtil.getSplittedData(tickerDataMapIntra, tickerDataMapInter, 1, selectedChartConfig.getTickerSymbol(), 2);
				}
				else{
					candleData = tickerDataUtil.getSplittedData(tickerDataMapIntra, tickerDataMapInter, selectedChartConfig.getDataDuration(), selectedChartConfig.getTickerSymbol(), 1);
				}
				candleData = candleData.stream().filter(candle -> candle.getHigh().compareTo(0d) != 0 && candle.getLow().compareTo(0d) != 0 && candle.getOpen().compareTo(0d) != 0 && candle.getClose().compareTo(0d) != 0).collect(Collectors.toList());

				List<UITickerCalculatedSymbolData> finalConvertedList = getCalculatedData(data,candleData);
				Map<String, Object> indicatorData = new HashMap<String, Object>();
				indicatorData.put("indicatorUID", indicatorUID);
				indicatorData.put("finalConvertedList", finalConvertedList);
				indicatorData.put("indicatorParamList", selectedChartConfig.getIndicatorParams());

				Gson json = new Gson();
				InternalSocketServer.getInstance().getServerHandler().getBroadcastOperations().sendEvent("calculatedData", json.toJson(indicatorData).toString());
			}
		});

		InternalSocketServer.getInstance().getServerHandler().addEventListener("removeGraph", RemoveChartRequest.class,
				new DataListener<RemoveChartRequest>() {
			@Override
			public void onData(SocketIOClient client, RemoveChartRequest data, AckRequest ackSender)
					throws Exception {

				ChartConfig selectedChartConfig = chartConfigs.get(Integer.parseInt(data.getChartUID()));
				if(data.getIndicatorUID() != null) {
					Integer indicatorUID = Integer.parseInt(data.getIndicatorUID());
					
					for (Iterator<IndicatorParamBean> indicatorParam = selectedChartConfig.getIndicatorParams()
							.iterator(); indicatorParam.hasNext();) {
						if (indicatorParam.next().getIndicatorUID().intValue() == indicatorUID.intValue()) {
							indicatorParam.remove();
							break;
						}
					}					
				}
			}
		});
		
		InternalSocketServer.getInstance().getServerHandler().addEventListener("editEMA", EditEMARequest.class,
				new DataListener<EditEMARequest>() {
			@Override
			public void onData(SocketIOClient client, EditEMARequest data, AckRequest ackSender)
					throws Exception {

				ChartConfig selectedChartConfig = chartConfigs.get(Integer.parseInt(data.getChartUID()));
				if(data.getIndicatorUID() != null) {
					Integer indicatorUID = Integer.parseInt(data.getIndicatorUID());
					
									
				}
			}
		});
	}

	private void removeTrendlineFromMap(List<TrendLineParamBean> trendlines, int trendLineIdToRemove) {
		Iterator<TrendLineParamBean> savedTrendlinesItr = trendlines.iterator();
		while(savedTrendlinesItr.hasNext()) {
			TrendLineParamBean trendline = savedTrendlinesItr.next();
			if(trendline.getiD() == trendLineIdToRemove) {
				savedTrendlinesItr.remove();							
			}
		}
	}


	private EventHandler<ActionEvent> chartUIHandler = new EventHandler<ActionEvent>(){	
		@Override			
		public void handle(ActionEvent event) {			
			try{			
				List<LayoutNode> layoutNodes = new ArrayList<>();
				for (LayoutNode layoutNode : layoutList) {
					if(layoutNode.getChartUID() != null) {
						layoutNodes.add(layoutNode);
					}
				}
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
				chartUIController.setLayoutNode(layoutNodes);
				chartUIController.setDashboardController(dashboardController);
				chartUIController.setUserValuesToColorPicker();
				dialogStage.showAndWait();	
			} catch(Exception e) {			
				e.printStackTrace();			
			}			
		}			
	};

	private double calculateSlope(int x1, int x2, double y1, double y2) {
		double slope = (y2 - y1) / (x2 - x1);
		return slope;
	}

	private double getTrendlineProjectedPrice(double slope, int x1Index, int px, double py) {
		double projectedPrice = slope * (x1Index - px) + py;
		return projectedPrice;
	}    

	private void requestAlertList() {
		try {
			subscribeAlertListChannel();

			Calendar calendar = Calendar.getInstance();
			String date = ApplicationUtil.getStringifiedStartDateWithTrailingZeros(calendar.getTime());
			String f1ApiUrl = Constants.applicationProperties.getProperty("application.server.uri") + Constants.applicationProperties.getProperty("alert.ticker.list.api");
			AlertTickerResponseBean alertTickerListResponse = wsClient.sendGetRequest(f1ApiUrl.replace("{date}", date), AlertTickerResponseBean.class);

			if (alertTickerListResponse.getStatus() == ResultStatus.SUCCESS) {
				Map<String, List<AlertTicker>> alertTickerMap = alertTickerListResponse.getObjectsMap();
				List<AlertTicker> parabolicTickersList = (List<AlertTicker>) alertTickerMap.get(DataFlowType.PARABOLIC.toString());
				List<AlertTicker> f1TickersList = (List<AlertTicker>) alertTickerMap.get(DataFlowType.F1.toString());
				List<AlertTicker> gapTickersList = (List<AlertTicker>) alertTickerMap.get(DataFlowType.GAP.toString());

				if((f1TickersList != null) && (f1TickersList.size() == 0)) {
					Calendar calendar1 = Calendar.getInstance();
					calendar1.add(Calendar.DATE, -1);
					date = ApplicationUtil.getStringifiedStartDateWithTrailingZeros(calendar.getTime());
					alertTickerListResponse = wsClient.sendGetRequest(f1ApiUrl.replace("{date}", date), AlertTickerResponseBean.class);
					alertTickerMap = alertTickerListResponse.getObjectsMap();
					f1TickersList = (List<AlertTicker>) alertTickerMap.get(DataFlowType.F1.toString());
				}

				if((parabolicTickersList != null) && (parabolicTickersList.size() == 0)) {
					Calendar calendar1 = Calendar.getInstance();
					calendar1.add(Calendar.DATE, -1);
					date = ApplicationUtil.getStringifiedStartDateWithTrailingZeros(calendar.getTime());
					alertTickerListResponse = wsClient.sendGetRequest(f1ApiUrl.replace("{date}", date), AlertTickerResponseBean.class);
					alertTickerMap = alertTickerListResponse.getObjectsMap();
					parabolicTickersList = (List<AlertTicker>) alertTickerMap.get(DataFlowType.PARABOLIC.toString());
				}

				if((gapTickersList != null) && (gapTickersList.size() == 0)) {
					Calendar calendar1 = Calendar.getInstance();
					calendar1.add(Calendar.DATE, -1);
					date = ApplicationUtil.getStringifiedStartDateWithTrailingZeros(calendar.getTime());
					alertTickerListResponse = wsClient.sendGetRequest(f1ApiUrl.replace("{date}", date), AlertTickerResponseBean.class);
					alertTickerMap = alertTickerListResponse.getObjectsMap();
					gapTickersList = (List<AlertTicker>) alertTickerMap.get(DataFlowType.GAP.toString());
				}

				if((f1TickersList != null) && (f1TickersList.size() != 0)) 
					subscribeF1TickersFromList(f1TickersList.size(), f1TickersList);
				if((parabolicTickersList != null) && (parabolicTickersList.size() != 0))
					subscribeParabolicTickersFromList(parabolicTickersList.size(), parabolicTickersList);
				if((gapTickersList != null) && (gapTickersList.size() != 0))
					subscribeGapTickersFromList(gapTickersList.size(), gapTickersList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while fetching the converting the F1 tickers. Full details:" + e);
		}
	}

	private void subscribeAlertListChannel() {
		alertNettyClient.getChannel().writeAndFlush(DataFlowType.F1.toString() + "\r\n");
		alertNettyClient.getChannel().writeAndFlush(DataFlowType.PARABOLIC.toString() + "\r\n");
		alertNettyClient.getChannel().writeAndFlush(DataFlowType.GAP.toString() + "\r\n");
	}

	private void subscribeF1TickersFromList(int f1ListLength, List<AlertTicker> tickers) {
		for (int i = 0; i < f1ListLength; i++) {
			subscribeF1Ticker(tickers.get(i));
		}
	}
	private void subscribeF1Ticker(AlertTicker ticker) {
		String subscribeTicker = ticker.getAlgorithm() + Constants.UNDERSCORE + ticker.getTickerCode();
		alertNettyClient.getChannel().writeAndFlush(subscribeTicker + "\r\n");
		addTickerToF1MapAndF1List(ticker);
	}
	private void subscribeParabolicTickersFromList(int parabolicListLength, List<AlertTicker> tickers) {
		for (int i = 0; i < parabolicListLength; i++) {
			subscribeParabolicTicker(tickers.get(i));
		}
	}
	private void subscribeParabolicTicker(AlertTicker ticker) {
		String subscribeTicker = ticker.getAlgorithm() + Constants.UNDERSCORE + ticker.getTickerCode();
		alertNettyClient.getChannel().writeAndFlush(subscribeTicker + "\r\n");
		addTickerToParabolicMapAndList(ticker);
	}
	private void subscribeGapTickersFromList(int gapListLength, List<AlertTicker> tickers) {
		for (int i = 0; i < gapListLength; i++) {
			subscribeGapTicker(tickers.get(i));
		}
	}
	private void subscribeGapTicker(AlertTicker ticker) {
		String subscribeTicker = ticker.getAlgorithm() + Constants.UNDERSCORE + ticker.getTickerCode();
		alertNettyClient.getChannel().writeAndFlush(subscribeTicker + "\r\n");
		addTickerToGapMapAndList(ticker);
	}

	private void addTickerToParabolicMapAndList(AlertTicker ticker) {
		AlertStock parabolicStock= createAlertStockFromTicker(ticker);
		parabolicStockMap.put(ticker.getTickerCode(), parabolicStock);
		addNewTickerToParabolicList(parabolicStock);
	}

	private AlertStock createAlertStockFromTicker(AlertTicker ticker) {
		if (ticker.getPrice() == null) {
			ticker.setPrice(Constants.ZERO_DOUBLE);
		}

		AlertStock alertStock = new AlertStock(new SimpleStringProperty(ticker.getTickerCode()),
				new SimpleDoubleProperty(ticker.getPrice()), null, null,
				new SimpleStringProperty(Constants.EMPTY_STRING), new SimpleStringProperty(ticker.getAlgorithm()),
				null);

		return alertStock;
	}

	private void addTickerToGapMapAndList(AlertTicker ticker) {
		AlertStock gapStock= createAlertStockFromTicker(ticker);
		gapStockMap.put(ticker.getTickerCode(), gapStock);
		addNewTickerToGapList(gapStock);
	}

	private void addNewTickerToParabolicList(AlertStock stock) {
		parabolicStockList.add(0, stock);
	}

	private void addNewTickerToGapList(AlertStock stock) {
		gapStockList.add(0, stock);
	}

	private void subscribeChartTickerFromMap() {
		if((watchChartStockMap != null) && (watchChartStockMap.size() > 0)) {
			for (String tickerName : watchChartStockMap.values()) {
				oneMinuteNettyClient.getChannel().writeAndFlush(tickerName+ "\r\n");
				streamerNettyClient.getChannel().writeAndFlush(tickerName+ "\r\n");
			}
		}
	}

	public void startRecievingData() {
		ConnectionLostDialog dialog = new ConnectionLostDialog();
		String sessionId = (String) Context.getContext().get(Constants.SESSION_ID);
		try {
			streamerNettyClient = new StreamerNettyClient(DataFlowType.REALTIME, this);
			streamerNettyClient.connect(Constants.applicationProperties.getProperty("stock.data.feeder.host"),
					Integer.parseInt(Constants.applicationProperties.getProperty("stock.data.feeder.port")));
			streamerNettyClient.getChannel().writeAndFlush(Constants.SESSION_KEYWORD + sessionId + "\r\n");
			if(streamerNettyClient.getChannel().isActive()) {
				requestTickerData();
			} else {
				dialog.showLostConnectionDialog();
			}

			oneMinuteNettyClient = new StreamerNettyClient(DataFlowType.ONE_MINUTE, this);
			oneMinuteNettyClient.connect(Constants.applicationProperties.getProperty("stock.data.feeder.host"),
					Integer.parseInt(Constants.applicationProperties.getProperty("stock.data.feeder.port.one.minute")));
			oneMinuteNettyClient.getChannel().writeAndFlush(Constants.SESSION_KEYWORD + sessionId + "\r\n");
			if(oneMinuteNettyClient.getChannel().isActive()) {
				subscribeChartTickerFromMap();
			} else {
				dialog.showLostConnectionDialog();
			}

			alertNettyClient = new StreamerNettyClient(DataFlowType.ALL_ALERTS, this);
			alertNettyClient.connect(Constants.applicationProperties.getProperty("stock.f1.data.feeder.host"),
					Integer.parseInt(Constants.applicationProperties.getProperty("stock.f1.data.feeder.port")));
			alertNettyClient.getChannel().writeAndFlush(Constants.SESSION_KEYWORD + sessionId + "\r\n");
			if(alertNettyClient.getChannel().isActive()) {
				requestAlertList();
			} else {
				dialog.showLostConnectionDialog();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Couldn't connect to netty server. Full details: " + e);
			dialog.showLostConnectionDialog();
		}
	}

	private void registerInternalSocketClientHandlers() throws Exception {
		InternalSocketClient.getInstance().getClientHandler().on("subscribeTicker", new Emitter.Listener() {
			public void call(Object... response) {
				if (!watchChartStockMap.containsKey(response[0].toString())) {
					watchChartStockMap.put(response[0].toString(), response[0].toString());
					streamerNettyClient.getChannel().writeAndFlush(response[0].toString() + "\r\n");
				}
			}
		});

		InternalSocketClient.getInstance().getClientHandler().on("unSubscribeTicker", new Emitter.Listener() {
			public void call(Object... response) {
				if (watchChartStockMap.containsKey(response[0].toString())) {
					watchChartStockMap.remove(response[0].toString());
					streamerNettyClient.getChannel().writeAndFlush(
							Constants.STREAMER_UNSUBSCRIBE_TICKER_PREFIX + response[0].toString() + "\r\n");
				}
			}
		});
	}

	private void requestTickerData() {
		try {
			List<String> subscribedTickers = userService
					.getSubscribedTickers((Integer) Context.getContext().get(Constants.USERID_KEY));
			if (subscribedTickers == null) {
				ApplicationUtil.displayAlert("Unable to fetch Subscribed tickers");
			} else {
				for (int i = 0; i < subscribedTickers.size(); i++) {
					streamerNettyClient.getChannel().writeAndFlush(subscribedTickers.get(i) + "\r\n");
					addTickerToWatchMapAndWatchList(subscribedTickers.get(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in requestTickerData. Full details: " + e);
		}
	}

	private void addTickerToWatchMapAndWatchList(String symbolName) {
		// Stock stock = new Stock(symbol, lastSize, lastDateTime, change);
		// Stock ss = new Stock(symbol, lastSize, lastDateTime, change,
		// algorithm)
		Stock stock = new Stock(new SimpleStringProperty(symbolName), new SimpleDoubleProperty(Constants.ZERO_DOUBLE),
				null, null, null);
		if (!watchStockMap.containsKey(symbolName)) {
			watchStockMap.put(symbolName, stock);
			addNewTickerToWatchList(stock);
		}
	}

	private void addNewTickerToWatchList(Stock stock) {
		watchStockList.add(stock);
	}

	private void addTickerToF1MapAndF1List(AlertTicker ticker) {
		if (ticker.getPrice() == null) {
			ticker.setPrice(Constants.ZERO_DOUBLE);
		}

		F1Stock f1Stock = new F1Stock(new SimpleStringProperty(ticker.getTickerCode()),
				new SimpleDoubleProperty(ticker.getPrice()), new SimpleStringProperty(Constants.EMPTY_STRING),
				new SimpleStringProperty(Constants.EMPTY_STRING), new SimpleDoubleProperty(Constants.ZERO_DOUBLE));
		f1StockMap.put(ticker.getTickerCode(), f1Stock);
		addNewTickerToF1List(f1Stock);
	}

	private void addNewTickerToF1List(F1Stock f1Stock) {
		f1StockList.add(0, f1Stock);
		f1StockBackupList.add(0, f1Stock);
	}

	private void updateWatchListTableView(Stock stock) {
		Stock oldStock = watchStockMap.get(stock.symbolProperty().get());
		if (oldStock != null) {
			updateWatchTickerValues(stock, oldStock);
		}
	}

	private void updateAlertTableViews(AlertStock stock) {
		if (stock.algorithmProperty().get().equalsIgnoreCase(DataFlowType.PARABOLIC.toString())) {
			AlertStock oldStock = parabolicStockMap.get(stock.symbolProperty().get());
			if (oldStock != null) {
				updateParabolicTickerValues(stock, oldStock);
				if((stock.alertStatusProperty() != null) && !masterStockMap.containsKey(stock.symbolProperty().get()) && stock.alertStatusProperty().get().equalsIgnoreCase(Constants.ACTIONABLE)) {
					addDataToMasterTable(oldStock, DataFlowType.PARABOLIC.toString());
				}
			} else {
				AlertTicker ticker = new AlertTicker(stock.symbolProperty().get(), (stock.lastPriceProperty()!= null)?stock.lastPriceProperty().doubleValue():0.0, stock.algorithmProperty().get());
				subscribeParabolicTicker(ticker);
			}
		} else if(stock.algorithmProperty().get().equalsIgnoreCase(DataFlowType.F1.toString())) {
			F1Stock oldStock = f1StockMap.get(stock.symbolProperty().get());
			if (oldStock != null) {
				updateF1TickerValues(stock, oldStock);
				if((stock.alertStatusProperty() != null) && !masterStockMap.containsKey(stock.symbolProperty().get()) && stock.alertStatusProperty().get().equalsIgnoreCase(Constants.ACTIONABLE)) {
					addDataToMasterTable(oldStock, DataFlowType.F1.toString());
				}
			}else {
				AlertTicker ticker = new AlertTicker(stock.symbolProperty().get(), (stock.lastPriceProperty()!= null)?stock.lastPriceProperty().doubleValue():0.0, stock.algorithmProperty().get()); 
				subscribeF1Ticker(ticker);
			}
		} else {
			AlertStock oldStock = gapStockMap.get(stock.symbolProperty().get());
			if (oldStock != null) {
				updateGapTickerValues(stock, oldStock);
				if((stock.alertStatusProperty() != null) && !masterStockMap.containsKey(stock.symbolProperty().get()) && stock.alertStatusProperty().get().equalsIgnoreCase(Constants.ACTIONABLE)) {
					addDataToMasterTable(oldStock, DataFlowType.GAP.toString());
				}
			}else {
				AlertTicker ticker = new AlertTicker(stock.symbolProperty().get(), (stock.lastPriceProperty()!= null)?stock.lastPriceProperty().doubleValue():0.0, stock.algorithmProperty().get());
				subscribeGapTicker(ticker);
			}
		}
	}

	private void updateParabolicTickerValues(AlertStock source, AlertStock destination) {
		destination = copyAlertStockContent(source, destination);
		int index = parabolicStockList.indexOf(destination);
		parabolicStockList.set(index, destination);
	}

	private AlertStock copyAlertStockContent(AlertStock source, AlertStock destination) {
		destination.setAlertStatus(source.alertStatusProperty());
		destination.setAlgorithm(source.algorithmProperty());
		destination.setChange(source.changeProperty());
		destination.setExpireTime(source.expireTimeProperty());
		destination.setLastDateTime(source.lastDateTimeProperty());
		destination.setLastPrice(source.lastPriceProperty());
		destination.setLastSize(source.lastSizeProperty());
		destination.setTriggerPrice(source.triggerPriceProperty());
		destination.setVolume(source.volumeProperty());

		return destination;
	}

	private void updateGapTickerValues(AlertStock source, AlertStock destination) {
		destination = copyAlertStockContent(source, destination);
		int index = gapStockList.indexOf(destination);
		gapStockList.set(index, destination);
	}

	private void updateWatchTickerValues(Stock source, Stock destination) {
		if (destination.lastPriceProperty() != null && source.lastPriceProperty() != null) {
			String ticker = source.symbolProperty().get();
			UITicker uiTickerObj = null;
			for (UITicker uiTicker : tickers) {
				if (uiTicker.getTickerCode().equalsIgnoreCase(ticker)) {
					uiTickerObj = uiTicker;
					break;
				}
			}
			if (uiTickerObj != null && uiTickerObj.getPreviousDayClose() != null) {
				double change = source.lastPriceProperty().get() - uiTickerObj.getPreviousDayClose();
				destination.setChange(new SimpleDoubleProperty(change));
			}
		}
		destination.setLastPrice(source.lastPriceProperty());
		destination.setLastSize(source.lastSizeProperty());
		destination.setLastDateTime(source.lastDateTimeProperty());

		int index = watchStockList.indexOf(destination);
		if (doUpdateWatchList) {
			watchStockList.set(index, destination);
		}
	}

	private void updateF1TickerValues(F1Stock source, F1Stock destination) {
		destination.setChange(source.changeProperty());
		destination.setLastPrice(source.lastPriceProperty());
		destination.setLastSize(source.lastSizeProperty());
		destination.setLastDateTime(source.lastDateTimeProperty());
		destination.setHodTrigger(source.hodTriggerProperty());
		destination.setStatus(source.statusProperty());
		destination.setTrigger(source.triggerProperty());
		destination.setLastDateTime(source.lastDateTimeProperty());
		destination.setLastDateTimeString(source.lastDateTimeStringProperty());

		int index = f1StockList.indexOf(destination);
		f1StockList.set(index, destination);
	}

	@Override
	public void receiver(DataFlowType chartType, String msg) {
		try {
			msg = CompressionUtility.GzipTOJson(msg);
			if (msg.startsWith("{")) {
				if (chartType == DataFlowType.REALTIME) {
					Stock stock = JSONToStockMapper.toWatchStock(msg);
					String ticker = stock.symbolProperty().get();
					Platform.runLater(() -> {
						if(tickerDataMapIntra.get(ticker) != null) {
							int dataIndex = tickerDataMapIntra.get(ticker).getAbstractTickerCandlePriceHistory().size() - 1;
							showUserAlerts(alertTrendlineBeanMap.get(ticker), stock,  dataIndex);
							showUserHorizontalAlerts(alertHorizontalTrendlineBeanMap.get(ticker), stock);
						}
						updateWatchListTableView(stock);
					});
					if (watchChartStockMap.containsKey(stock.symbolProperty().get())) {
						InternalSocketServer.getInstance().getServerHandler().getBroadcastOperations().sendEvent("updateRealPrices", msg);
					}
				} else if (chartType == DataFlowType.ALL_ALERTS) {
					AlertStock stock = JSONToStockMapper.toAlertStock(msg);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							updateAlertTableViews(stock);
						}
					});
				} else if (chartType == DataFlowType.ONE_MINUTE) {
					AbstractTickerCandlePriceHistory candle = AbstractBeanConverter.toBean(msg, AbstractTickerCandlePriceHistory.class);
					candle.setTradeDate(new Date(candle.getTimeStamp()));
					candle.setDate(candle.getTimeStamp());
					if (tickerDataMapIntra.get(candle.getTickerSymbol()) != null) {
						tickerDataMapIntra.get(candle.getTickerSymbol()).getAbstractTickerCandlePriceHistory().add(candle);
						tickerCandleUpdator.updateTickerCandle(candle, tickerDataMapIntra, tickerDataMapInter, chartConfigs);
						/*List<AbstractTickerCandlePriceHistory> withoutZeroPaddedCandles = tickerDataMapIntra.get(candle.getTickerSymbol()).getAbstractTickerCandlePriceHistory().stream().filter(oneMinuteCandle -> oneMinuteCandle.getHigh().compareTo(0d) != 0 && oneMinuteCandle.getLow().compareTo(0d) != 0 && oneMinuteCandle.getOpen().compareTo(0d) != 0 && oneMinuteCandle.getClose().compareTo(0d) != 0).collect(Collectors.toList());
						Platform.runLater(() -> {
							if(candle.getHigh().compareTo(0d) != 0 && candle.getLow().compareTo(0d) != 0 && candle.getOpen().compareTo(0d) != 0 && candle.getClose().compareTo(0d) != 0) {

							}
						});*/
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while receiving data from the server. Full details: " + e);
		}
	}

	@Override
	public void close() {
		((Stage)alertPopupVBox.getScene().getWindow()).close();
		try {
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					InternalSocketServer.destroyInstance();
					InternalSocketClient.destroyInstance();
					InternalSocketServer.removeInstance();
					InternalSocketClient.removeInstance();
					streamerNettyClient.disconnect();
					oneMinuteNettyClient.disconnect();
					alertNettyClient.disconnect();
					return null;
				}
			};
			new Thread(task).start();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while closing the localSocketServer socket. Full details:" + e);
		}
	}

	private EventHandler<ActionEvent> exitHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			close();
			System.exit(0);
		}
	};

	private EventHandler<ActionEvent> saveLayoutMenuHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			try { 
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/SaveLayoutDialog.fxml"));
				AnchorPane page = (AnchorPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Save Layout");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner((Stage)Context.getContext().get(Constants.PARENT_STAGE));
				Scene scene = new Scene(page);
				dialogStage.setScene(scene);
				SaveLayoutController saveLayoutController = loader.getController();
				parentLayout.setShowRightMenuBar(rightMenuBar.isVisible());
				setSplitPaneDividersPosition(parentLayout);
				saveLayoutController.setDashboardController(dashboardController);
				dialogStage.showAndWait();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void setSplitPaneDividersPosition(ParentLayout parentLayout) {
		if(leftDockPane != null) {
			List<Double> leftPaneDividerPositions = new ArrayList<>();
			List<Node> leftPaneNodeList = getAllSplitPaneInDockPane(leftDockPane);
			leftPaneNodeList.removeAll(Collections.singleton(null));

			for (Node node : leftPaneNodeList) {
				SplitPane splitpane = (SplitPane) node;
				for (Divider divider : splitpane.getDividers()) {
					leftPaneDividerPositions.add(divider.getPosition());
				}
			}
			parentLayout.setLeftPaneDividerPositions(leftPaneDividerPositions);
		}

		if(middleDockPane != null) {
			List<Double> middlePaneDividerPositions = new ArrayList<>();
			List<Node> middlePaneNodeList = getAllSplitPaneInDockPane(middleDockPane);
			middlePaneNodeList.removeAll(Collections.singleton(null));

			for (Node node : middlePaneNodeList) {
				SplitPane splitPane = (SplitPane) node;
				for (Divider divider : splitPane.getDividers()) {
					middlePaneDividerPositions.add(divider.getPosition());
				}
			}
			parentLayout.setMiddlePaneDividerPositions(middlePaneDividerPositions);
		}

		if(rightDockPane != null) {
			List<Double> rightPaneDividerPositions = new ArrayList<>();
			List<Node> rightPaneNodeList = getAllSplitPaneInDockPane(rightDockPane);
			rightPaneNodeList.removeAll(Collections.singleton(null));

			for (Node node : rightPaneNodeList) {
				SplitPane splitpane = (SplitPane) node;
				for (Divider divider : splitpane.getDividers()) {
					rightPaneDividerPositions.add(divider.getPosition());
				}
			}
			parentLayout.setRightPaneDividerPositions(rightPaneDividerPositions);
		}
	}

	public List<Node> getAllSplitPaneInDockPane(Parent root) {
		List<Node> nodes = new ArrayList<Node>();
		addAllDescendentsOfNodesInPane(root, nodes);
		ListIterator<Node> iterator = nodes.listIterator();
		while(iterator.hasNext()) {
			Node node = iterator.next();
			if(!(node instanceof SplitPane)) {
				iterator.remove();
			}
		}
		return nodes;
	}

	private void addAllDescendentsOfNodesInPane(Parent parent, List<Node> nodes) {
		for (Node node : parent.getChildrenUnmodifiable()) {
			nodes.add(node);
			if (node instanceof Parent) {
				addAllDescendentsOfNodesInPane((Parent)node, nodes);
			}
		}
	}

	private EventHandler<ActionEvent> openLayoutHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			try{ 
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChangeLayoutDialog.fxml"));
				AnchorPane page = (AnchorPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Change Layout");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner((Stage)Context.getContext().get(Constants.PARENT_STAGE));
				Scene scene = new Scene(page);
				dialogStage.setScene(scene);
				ChangeLayoutController layoutController = loader.getController();
				layoutController.setDashboardController(dashboardController);
				dialogStage.showAndWait();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void resetDockPaneMapAndConfig(ParentLayout parentLayout) {
		closeFloatingDockNodes();

		chartConfigs.clear();
		layoutList.clear();
		dockNodesMap.clear();
		middleSplitPane.getItems().clear();
		tickerDataMapIntra.clear();
		tickerDataMapInter.clear();

		this.parentLayout.setShowSideBars(parentLayout.isShowSideBars());

		middleSplitPane.setPadding(new Insets(15, 15, 15, 15));
		/*leftDockPane = new DockPane();
		middleDockPane = new DockPane();
		rightDockPane = new DockPane();*/

		/*middleDockPane.setDockPanePosition(DockPanePosition.MIDDLE);
		leftDockPane.setDockPanePosition(DockPanePosition.LEFT);
		rightDockPane.setDockPanePosition(DockPanePosition.RIGHT);*/

		/*middleSplitPane.getItems().addAll(leftDockPane, middleDockPane, rightDockPane);*/

		/*DockPane.setMargin(leftDockPane, new Insets(0.0, 7.0, 0.0, 0.0));
		DockPane.setMargin(rightDockPane, new Insets(0.0, 0.0, 0.0, 7.0));*/

		/*middleSplitPane.getDividers().get(0).setPosition(0.2);
		middleSplitPane.getDividers().get(1).setPosition(0.8);*/

		/*Context.getContext().put(Constants.LEFT_PANE, leftDockPane);
		Context.getContext().put(Constants.MIDDLE_PANE, middleDockPane);
		Context.getContext().put(Constants.RIGHT_PANE, rightDockPane);*/
	}

	private Map<String, List<LayoutNode>> getSeparateNodeListByPane(List<LayoutNode> nodeList) {
		Map<String, List<LayoutNode>> separatedNodeMap = new HashMap<String, List<LayoutNode>>();
		List<LayoutNode> realTimeFeedList = new ArrayList<LayoutNode>();
		List<LayoutNode> candleFeedList = new ArrayList<LayoutNode>();
		List<LayoutNode> algoFeedList = new ArrayList<LayoutNode>();
		for (LayoutNode layoutNode : nodeList) {
			if(layoutNode.getDockPanePosition() == DockPanePosition.MIDDLE) {
				candleFeedList.add(layoutNode);
			} else if((layoutNode.getNodeTitle() != null) && (layoutNode.getNodeTitle() == SideDockNodeTitle.WATCH_LIST)) {
				realTimeFeedList.add(layoutNode);
			} else {
				algoFeedList.add(layoutNode);
			}
		}
		separatedNodeMap.put(Entitlements.REAL_TIME_FEED.toString(), realTimeFeedList);
		separatedNodeMap.put(Entitlements.CANDLE_FEED.toString(), candleFeedList);
		separatedNodeMap.put(Entitlements.ALGO_FEED.toString(), algoFeedList);
		return separatedNodeMap;
	}

	private void addChartDockNode(ChartConfig config, LayoutNode layoutNode) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		ChartConfig chartConfig = layoutNode.getChartConfig();
		chartConfigs.put(chartConfig.getChartUID(), chartConfig);
		ChartTitleBar chartTitleBar = new ChartTitleBar(null, this);
		WebView webView = new WebView();
		DockNode newFloatingNode = new DockNode(webView, parentContainer.getScene().getWindow(), chartTitleBar, layoutNode);
		chartTitleBar.setDockNode(newFloatingNode);
		initializeComboBoxTickerAndDuration(newFloatingNode, chartConfig);
		newFloatingNode.setDockTitleBar(chartTitleBar);
		newFloatingNode.setDockPane(middleDockPane);
		newFloatingNode.setMinSize(100, 100);
		if (!newFloatingNode.isFloating())
			newFloatingNode.setFloating(true, parentContainer.getScene().getWindow());
		dockNodesMap.put(Integer.toString(chartConfig.getChartUID()), newFloatingNode);
		newFloatingNode.setLayoutNode(layoutNode);
		chartTitleBar.getCloseButton().setOnAction(chartCloseButtonHandler);
		chartTitleBar.getCloseButton().setId(String.valueOf(layoutNode.getChartConfig().getChartUID())+"CLOSE");
		addDimensionChangeListenerToNode(newFloatingNode);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				newFloatingNode.getStage().setWidth(layoutNode.getWidth());
				newFloatingNode.getStage().setHeight(layoutNode.getHeight());
				newFloatingNode.getStage().setX(layoutNode.getxPosPercent()*screenSize.getWidth()/100);
				newFloatingNode.getStage().setY(layoutNode.getyPosPercent()*screenSize.getHeight()/100);

				initializeWebview(webView, chartConfig);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void drawCustomSavedLayout(ParentLayout parentLayout) {
		if((parentLayout.getLayoutList() != null) && (parentLayout.getLayoutList().size() > 0)) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			resetDockPaneMapAndConfig(parentLayout);
			Map<String, List<LayoutNode>> separatedList = getSeparateNodeListByPane(parentLayout.getLayoutList());
			List<UIUserGroupEntitlement> entitlements = (List<UIUserGroupEntitlement>) Context.getContext().get(Constants.ENTITLEMENTS);

			boolean isAlgoFeedEntitled = false;
			boolean isCandleFeedEntitled = false;

			for (UIUserGroupEntitlement uiUserGroupEntitlement : entitlements) {
				if(uiUserGroupEntitlement.getName().equalsIgnoreCase(Entitlements.CANDLE_FEED.getEntitlements())) {
					isCandleFeedEntitled = true;
					drawCandleFeedComponents(screenSize, separatedList.get(Entitlements.CANDLE_FEED.toString()));
				} else if(uiUserGroupEntitlement.getName().equalsIgnoreCase(Entitlements.REAL_TIME_FEED.getEntitlements())) {
					drawSideComponents(screenSize, separatedList.get(Entitlements.REAL_TIME_FEED.toString()), true);
				} else if(uiUserGroupEntitlement.getName().equalsIgnoreCase(Entitlements.ALGO_FEED.getEntitlements())) {
					isAlgoFeedEntitled = true;
					drawSideComponents(screenSize, separatedList.get(Entitlements.ALGO_FEED.toString()), false);
				}
			}

			Context.getContext().put(Constants.LAYOUT_LIST, layoutList);

			if(isAlgoFeedEntitled && isCandleFeedEntitled) {
				if(!parentLayout.isShowSideBars()) {
					hideSideBars();
				} else {
					showSideBarsMenuItem.setDisable(true);
					hideSideBarsMenuItem.setDisable(false);
				}
			} else {
				file.getItems().removeAll(showSideBarsMenuItem, hideSideBarsMenuItem);
			}

			if(middleSplitPane.getItems().contains(middleDockPane)) {
				if(leftDockPane != null) {
					leftDockPane.setMaxWidth(280);
					leftDockPane.setPrefWidth(250);
					leftDockPane.setMinWidth(235);
				}

				if(rightDockPane != null) {
					rightDockPane.setMaxWidth(300);
					rightDockPane.setPrefWidth(250);
					rightDockPane.setMinWidth(260);
				}
			}

			if(parentLayout.isShowRightMenuBar()) {
				showRightMenubar();
			} else {
				hideRightMenubar();
			}

			Platform.runLater(new Runnable() {
				public void run() {
					Timeline timeline = new Timeline(new KeyFrame(
							Duration.millis(1000),
							ae -> {
								setDividerPositionsAsSpecifiedSavedLayout(parentLayout);
							}));
					timeline.setCycleCount(1);
					timeline.play();
				}
			});
		}
	}

	private void setDividerPositionsAsSpecifiedSavedLayout(ParentLayout parentLayout) {
		if(leftDockPane != null) {
			List<Node> leftPaneNodeList = getAllSplitPaneInDockPane(leftDockPane);
			leftPaneNodeList.removeAll(Collections.singleton(null));
			List<Double> leftPaneDividerPositions = parentLayout.getLeftPaneDividerPositions();

			int index = 0;
			for (Node node : leftPaneNodeList) {
				SplitPane splitpane = (SplitPane) node;
				for (Divider divider : splitpane.getDividers()) {
					divider.setPosition(leftPaneDividerPositions.get(index++));
				}
			}
		}

		if(middleDockPane != null) {
			List<Node> middlePaneNodeList = getAllSplitPaneInDockPane(middleDockPane);
			middlePaneNodeList.removeAll(Collections.singleton(null));
			List<Double> middlePaneDividerPositions = parentLayout.getMiddlePaneDividerPositions();

			int index = 0;
			for (Node node : middlePaneNodeList) {
				SplitPane splitpane = (SplitPane) node;
				for (Divider divider : splitpane.getDividers()) {
					divider.setPosition(middlePaneDividerPositions.get(index++));
				}
			}
		}

		if(rightDockPane != null) {
			List<Node> rightPaneNodeList = getAllSplitPaneInDockPane(rightDockPane);
			rightPaneNodeList.removeAll(Collections.singleton(null));
			List<Double> rightPaneDividerPositions = parentLayout.getRightPaneDividerPositions();

			int index = 0;
			for (Node node : rightPaneNodeList) {
				SplitPane splitpane = (SplitPane) node;
				for (Divider divider : splitpane.getDividers()) {
					divider.setPosition(rightPaneDividerPositions.get(index++));
				}
			}
		}
	}

	private DockNode getSideDockNode(SideDockNodeTitle nodeTitle, LayoutNode layoutNode) {
		switch (nodeTitle) {
		case F1_LIST:
			F1ListTitleBar f1TitleBar = new F1ListTitleBar(layoutNode.getNodeTitle().getSideDockNodeTitle());
			DockNode f1DockNode = new DockNode(f1TableView, f1TitleBar);
			f1TitleBar.setDockNode(f1DockNode);
			f1DockNode.setDockTitleBar(f1TitleBar);
			f1TitleBar.getFilterButton().setOnAction(f1FilterButtonHandler);
			return f1DockNode;
		case GAP_LIST:
			DockTitleBar gapTitleBar = new DockTitleBar(layoutNode.getNodeTitle().getSideDockNodeTitle());
			DockNode gapDockNode = new DockNode(gapTableView, gapTitleBar);
			gapTitleBar.setDockNode(gapDockNode);
			gapDockNode.setDockTitleBar(gapTitleBar);
			return gapDockNode;
		case MASTER_LIST:
			DockTitleBar masterTitleBar = new DockTitleBar(layoutNode.getNodeTitle().getSideDockNodeTitle());
			DockNode masterDockNode = new DockNode(masterListTableView, masterTitleBar);
			masterTitleBar.setDockNode(masterDockNode);
			masterDockNode.setDockTitleBar(masterTitleBar);
			return masterDockNode;
		case NEWS:
			DockTitleBar newsTitleBar = new DockTitleBar(layoutNode.getNodeTitle().getSideDockNodeTitle());
			DockNode newsDockNode = new DockNode(getNewsPane(), newsTitleBar);
			newsTitleBar.setDockNode(newsDockNode);
			newsDockNode.setDockTitleBar(newsTitleBar);
			return newsDockNode;
		case PARABOLIC_LIST:
			DockTitleBar parabolicTitleBar = new DockTitleBar(layoutNode.getNodeTitle().getSideDockNodeTitle());
			DockNode parabolicDockNode = new DockNode(parabolicTableView, parabolicTitleBar);
			parabolicTitleBar.setDockNode(parabolicDockNode);
			parabolicDockNode.setDockTitleBar(parabolicTitleBar);
			return parabolicDockNode;
		case SQUEEZE_ZONE:
			DockTitleBar squeezeTitleBar = new DockTitleBar(layoutNode.getNodeTitle().getSideDockNodeTitle());
			DockNode squeezeDockNode = new DockNode(squeezeZoneTableView, squeezeTitleBar);
			squeezeTitleBar.setDockNode(squeezeDockNode);
			squeezeDockNode.setDockTitleBar(squeezeTitleBar);
			return squeezeDockNode;
		case WATCH_LIST:
			WatchTitleBar watchTitleBar = new WatchTitleBar(layoutNode.getNodeTitle().getSideDockNodeTitle());
			DockNode watchDockNode = new DockNode(watchListTableView, watchTitleBar);
			watchTitleBar.setDockNode(watchDockNode);
			watchDockNode.setDockTitleBar(watchTitleBar);
			addEditTickerButtonOnTitleBar(watchDockNode);
			return watchDockNode;
		}
		return null;
	}

	private void drawSideComponents(Dimension screenSize, List<LayoutNode> localLayoutList, boolean isRealTimeEntitlement) {
		if(!middleSplitPane.getItems().contains(leftDockPane)) {
			leftDockPane = new DockPane();
			leftDockPane.setDockPanePosition(DockPanePosition.LEFT);
			DockPane.setMargin(leftDockPane, new Insets(0.0, 7.0, 0.0, 0.0));
			Context.getContext().put(Constants.LEFT_PANE, leftDockPane);
			middleSplitPane.getItems().add(0, leftDockPane);
		}
		if(!isRealTimeEntitlement) {
			rightDockPane = new DockPane();
			rightDockPane.setDockPanePosition(DockPanePosition.RIGHT);
			DockPane.setMargin(rightDockPane, new Insets(0.0, 0.0, 0.0, 7.0));
			Context.getContext().put(Constants.RIGHT_PANE, rightDockPane);
			middleSplitPane.getItems().add(middleSplitPane.getItems().size(), rightDockPane);
		}

		for (LayoutNode layoutNode : localLayoutList) {
			layoutList.add(layoutNode);
			DockNode dockNode = getSideDockNode(layoutNode.getNodeTitle(), layoutNode);
			dockNode.getStyleClass().add("black-background");
			dockNode.setPrefSize(100, 150);
			dockNode.setLayoutNode(layoutNode);
			if(layoutNode.getDockPanePosition() == DockPanePosition.LEFT) {
				dockNode.setDockPane(leftDockPane);
			} else if(layoutNode.getDockPanePosition() == DockPanePosition.MIDDLE) {
				dockNode.setDockPane(middleDockPane);
			} else {
				dockNode.setDockPane(rightDockPane);
			}
			if(layoutNode.isFloating()) {
				dockNode.setFloating(true, parentContainer.getScene().getWindow());
				dockNode.getStage().setWidth(layoutNode.getWidth());
				dockNode.getStage().setHeight(layoutNode.getHeight());
				dockNode.getStage().setX((screenSize.getWidth()*layoutNode.getxPosPercent())/100);
				dockNode.getStage().setY((screenSize.getHeight()*layoutNode.getyPosPercent())/100);
			} else {
				DockPane dockPane = getParentDockPaneBasedOnLayoutNode(layoutNode);
				if((layoutNode.getRelativeDockNode() != null) && dockNodesMap.containsKey(layoutNode.getRelativeDockNode())) {
					dockNode.dock(dockPane, layoutNode.getDockPos(), dockNodesMap.get(layoutNode.getRelativeDockNode()), false);
				} else {
					dockNode.dock(dockPane, layoutNode.getDockPos(), false);
				}
			}
			dockNodesMap.put(layoutNode.getNodeTitle().toString(), dockNode);
			addDimensionChangeListenerToNode(dockNode);
		}
	}

	private DockPane getParentDockPaneBasedOnLayoutNode(LayoutNode layoutNode) {
		if(layoutNode.getDockPanePosition() == DockPanePosition.LEFT) {
			return leftDockPane;
		} else {
			return rightDockPane;
		}
	}

	private void registerChartTickerForReceivingData(ChartConfig chartConfig) {
		oneMinuteNettyClient.getChannel().writeAndFlush(chartConfig.getTickerSymbol()+ "\r\n");
		streamerNettyClient.getChannel().writeAndFlush(chartConfig.getTickerSymbol()+ "\r\n");
		if(!watchChartStockMap.containsKey(chartConfig.getTickerSymbol())) {
			watchChartStockMap.put(chartConfig.getTickerSymbol(), chartConfig.getTickerSymbol());
		}
	}

	private void drawCandleFeedComponents(Dimension screenSize, List<LayoutNode> localLayoutList) {
		middleDockPane = new DockPane();
		middleDockPane.setDockPanePosition(DockPanePosition.MIDDLE);
		Context.getContext().put(Constants.MIDDLE_PANE, middleDockPane);
		if((middleSplitPane.getItems().size() == 0) || (middleSplitPane.getItems().size() == 1)) {
			middleSplitPane.getItems().add(middleDockPane); 
		} else {
			middleSplitPane.getItems().add(1, middleDockPane);
		}
		for (LayoutNode layoutNode : localLayoutList) {
			layoutList.add(layoutNode);
			registerChartTickerForReceivingData(layoutNode.getChartConfig());
			if(layoutNode.isFloating()) {
				addChartDockNode(layoutNode.getChartConfig(), layoutNode);
			} else {
				chartConfigs.put(layoutNode.getChartConfig().getChartUID(), layoutNode.getChartConfig());
				DockNode dockNode = getChartNodeFromLayoutNode(layoutNode, screenSize);
				dockNode.setLayoutNode(layoutNode);
				addDimensionChangeListenerToNode(dockNode);
				dockNodesMap.put(Integer.toString(layoutNode.getChartUID()), dockNode);
			}
			
			for (TrendLineParamBean trendLineParam : layoutNode.getChartConfig().getTrendlineParams()) {
				trendLineParam.setAlertDirection(null);
				tickerTrendlineDataSet.add(trendLineParam.getChartUID()+Constants.UNDERSCORE+trendLineParam.getiD());
			}
			
			for (TrendLineParamBean trendLineParam : layoutNode.getChartConfig().getHorizontalTrendlineParams()) {
				trendLineParam.setAlertDirection(null);
				tickerTrendlineDataSet.add(trendLineParam.getChartUID()+Constants.UNDERSCORE+trendLineParam.getiD());
			}
		}
	}

	private DockNode getChartNodeFromLayoutNode(LayoutNode layoutNode, Dimension screenSize) {
		WebView webView = new WebView();
		ChartTitleBar titleBar = new ChartTitleBar(null, this);
		DockNode webViewDock = new DockNode(webView, titleBar, layoutNode);
		titleBar.setDockNode(webViewDock);
		webViewDock.setMinSize(100, 100);
		initializeComboBoxTickerAndDuration(webViewDock, layoutNode.getChartConfig());
		titleBar.getCloseButton().setOnAction(chartCloseButtonHandler);
		titleBar.getCloseButton().setId(String.valueOf(layoutNode.getChartConfig().getChartUID())+"CLOSE");
		if(layoutNode.isFloating()) {
			webViewDock.setFloating(true, parentContainer.getScene().getWindow());
			webViewDock.getStage().setWidth(layoutNode.getWidth());
			webViewDock.getStage().setHeight(layoutNode.getHeight());
			webViewDock.getStage().setX((screenSize.getWidth()*layoutNode.getxPosPercent())/100);
			webViewDock.getStage().setY((screenSize.getHeight()*layoutNode.getyPosPercent())/100);
		} else {
			if((layoutNode.getRelativeDockNode() != null) && (dockNodesMap.containsKey(layoutNode.getRelativeDockNode()))) {
				webViewDock.dock(middleDockPane, layoutNode.getDockPos(), dockNodesMap.get(layoutNode.getRelativeDockNode()), false);
			} else {
				webViewDock.dock(middleDockPane, layoutNode.getDockPos(), false);
			}
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initializeWebview(webView, layoutNode.getChartConfig());
			}
		});
		return webViewDock;
	}

	public void createMenuBar() {
		Menu adminConfiguration = new Menu("Admin Configuration");
		Menu myAccount = new Menu("My Account");
		Menu help = new Menu("Help");

		// Initializing menu items for file menu
		MenuItem openLayoutMenuItem = new MenuItem("Open Layout");
		MenuItem saveCurrentLayoutMenuItem = new MenuItem("Save Current Layout");
		MenuItem saveAsMenuItem = new MenuItem("Save as");
		MenuItem changeChartUI = new MenuItem("Change Chart UI");

		saveCurrentLayoutMenuItem.setOnAction(saveLayoutMenuHandler);
		openLayoutMenuItem.setOnAction(openLayoutHandler);
		changeChartUI.setOnAction(chartUIHandler);

		showSideBarsMenuItem.setOnAction(showSideBarsHandler);
		showSideBarsMenuItem.setDisable(true);

		hideSideBarsMenuItem.setOnAction(hideSideBarsHandler);
		hideSideBarsMenuItem.setDisable(false);

		MenuItem logoutMenuItem = new MenuItem(Constants.applicationLanguage.getProperty("label.sign.out"));
		logoutMenuItem.setOnAction(logoutHandler);

		MenuItem exitMenuItem = new MenuItem("Exit");
		exitMenuItem.setOnAction(exitHandler);
		// File menu item initialization finished

		file.getItems().addAll(openLayoutMenuItem, saveCurrentLayoutMenuItem, saveAsMenuItem, changeChartUI, showSideBarsMenuItem, hideSideBarsMenuItem, logoutMenuItem, exitMenuItem);
		topLeftMenuBar.getMenus().addAll(file, adminConfiguration, myAccount, help);

		toggleRightMenuButton = new Button("Extended");
		toggleRightMenuButton.getStyleClass().addAll("button-background", "top-center-menu-padding",
				"extend-right-menu-button");

		toggleRightMenuButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (toggleRightMenuButton.getText().equals("Extended")) {
					hideRightMenubar();
				} else {
					showRightMenubar();
				}
			}
		});

		Button fousalert = new Button(Constants.EMPTY_STRING, new ImageView(new Image("images/fousalert.png")));
		fousalert.getStyleClass().addAll("button-background", "right-menu-bar-padding");
		Button alert = new Button(Constants.EMPTY_STRING, new ImageView(new Image("images/bell.png")));
		alert.getStyleClass().addAll("button-background", "right-menu-bar-padding", "bell-icon");
		Button flame = new Button(Constants.EMPTY_STRING, new ImageView(new Image("images/flame.png")));
		flame.getStyleClass().addAll("button-background", "right-menu-bar-padding");
		Pane emptyPane = new Pane();
		VBox.setVgrow(emptyPane, Priority.ALWAYS);
		Button horn = new Button(Constants.EMPTY_STRING, new ImageView(new Image("images/horn.png")));
		horn.getStyleClass().addAll("button-background", "right-menu-bar-padding");
		Button wheel = new Button(Constants.EMPTY_STRING, new ImageView(new Image("images/wheel.png")));
		wheel.getStyleClass().addAll("button-background", "right-menu-bar-padding");

		rightMenuBar.getItems().addAll(fousalert, flame, alert, emptyPane, horn, wheel);

		Button trading = new Button("Trading");
		trading.getStyleClass().addAll("bottom-menu-Button");
		Button tcClassic = new Button("Tc classic");
		tcClassic.getStyleClass().addAll("bottom-menu-Button", "border-color");
		Button drillDown = new Button("DrillDown");
		drillDown.getStyleClass().addAll("bottom-menu-Button", "border-color");
		Button sectorByYear = new Button("Sector by year");
		sectorByYear.getStyleClass().addAll("bottom-menu-Button", "border-color");
		Button oneChart = new Button("1-chart");
		oneChart.getStyleClass().addAll("bottom-menu-Button", "border-color");
		Button fourTimeFrame = new Button("4-timeframe");
		fourTimeFrame.getStyleClass().addAll("bottom-menu-Button", "border-color");
		Button addNew = new Button(Constants.EMPTY_STRING, new ImageView(new Image("images/plus-icon.png")));
		addNew.getStyleClass().addAll("bottom-menu-Button", "border-color");

		bottomMenubar.getItems().addAll(trading, tcClassic, drillDown, sectorByYear, oneChart, fourTimeFrame, addNew);
	}

	private void hideRightMenubar() {
		toggleRightMenuButton.setText("Hidden");
		rightMenuBar.setVisible(false);
	}

	private void showRightMenubar() {
		toggleRightMenuButton.setText("Extended");
		rightMenuBar.setVisible(true);
	}

	private void setupComboBoxTextEventHandlers(TextField textField, ListView<String> listView, Stage comboBoxStage) {

		textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				String value = textField.getText().toString();
				if ((Pattern.matches(Constants.TICKER_PATTERN_REGEX, event.getCode().toString())
						|| (event.getCode().equals(KeyCode.BACK_SPACE)) || (event.getCode().equals(KeyCode.DELETE)))
						&& ((value != null) && (!value.isEmpty()) && (tickers.size() != 0)
								&& (Pattern.matches(Constants.TICKER_PATTERN_REGEX, value)))) {
					listView.getItems().clear();
					for (int i = 0; i < tickers.size(); i++) {
						if (tickers.get(i).getTickerCode().startsWith(value.toUpperCase())
								|| tickers.get(i).getTickerName().toLowerCase().startsWith(value.toLowerCase())) {
							listView.getItems().add(tickers.get(i).getTickerCode());
						}
					}
				} else {
					listView.getItems().addAll(tickerStringList);
				}
			}
		});

		textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().isArrowKey()) {
					listView.requestFocus();
				}
			}
		});

		textField.setOnAction(new EventHandler<ActionEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handle(ActionEvent event) {
				String selectedValue = null;
				ObservableList<String> list = ((ListView<String>) ((VBox) ((TextField) event.getSource()).getParent())
						.getChildren().get(1)).getItems();
				if (list.size() > 0)
					selectedValue = list.get(0);
				if ((selectedValue != null) && !selectedValue.isEmpty()
						&& Pattern.matches(Constants.TICKER_PATTERN_REGEX, selectedValue)
						&& tickerStringList.contains(selectedValue.toUpperCase())) {
					addTicker(selectedValue.toUpperCase());
				}
				comboBoxStage.close();
			}
		});

		listView.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					onTickerListItemSelected(listView, comboBoxStage);
				}
			}
		});

		listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				onTickerListItemSelected(listView, comboBoxStage);
			}
		});

		comboBoxStage.focusedProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (!comboBoxStage.isFocused()) {
					comboBoxStage.close();
				}
			}
		});
	}

	private void onTickerListItemSelected(ListView<String> listView, Stage comboBoxStage) {
		String selectedValue = listView.getSelectionModel().getSelectedItem();
		if(selectedValue != null) {
			if (Pattern.matches(Constants.TICKER_PATTERN_REGEX, selectedValue)) {
				addTicker(selectedValue.toUpperCase());
			}			
		}
		comboBoxStage.close();
	}

	private EventHandler<ActionEvent> addTickerButtonHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			Button addTickerButton = (Button) event.getSource();
			DockTitleBar dockTitleBar = (DockTitleBar) addTickerButton.getParent();
			Bounds boundsInScreen = dockTitleBar.localToScreen(dockTitleBar.getBoundsInLocal());
			double xPos = boundsInScreen.getMinX();
			double yPos = boundsInScreen.getMinY();
			CustomComboBox customBox = new CustomComboBox(xPos, yPos);
			Stage comboBoxStage = customBox.getStage();
			customBox.getListView().getItems().addAll(FXCollections.observableList(tickerStringList));
			setupComboBoxTextEventHandlers(customBox.getTextField(), customBox.getListView(), comboBoxStage);
		}
	};

	private void addEditTickerButtonOnTitleBar(DockNode node) {
		WatchTitleBar dockTitleBar = (WatchTitleBar) node.getChildren().get(0);
		Button addTickerButton = dockTitleBar.getAddTickerButton();
		addTickerButton.setVisible(true);
		addTickerButton.setDisable(false);
		addTickerButton.setOnAction(addTickerButtonHandler);
	}

	private String mapOneMinuteData(SocketTickerDataObject ticker) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		URI url = null;
		String serverUrl = Constants.applicationProperties.getProperty("application.server.uri");
		SocketDataCacheObject socketDataObjectBean = new SocketDataCacheObject();
		try {
			if (Integer.parseInt(ticker.getApiSerial()) == Constants.API_TYPE.INTRA_DAY.getType()) {
				socketDataObjectBean.setNoOfDays(1);
				url = new URI(serverUrl + Constants.applicationProperties.getProperty("custom.duartion.api")
				.replace("{symbol}", ticker.getTicker()).replace("{size}", Constants.applicationProperties
						.getProperty("no.of.previous.years.to.fetch.daily.data")));
			} else if (Integer.parseInt(ticker.getApiSerial()) == Constants.API_TYPE.INTER_DAY.getType()) {
				Date today = new Date();
				String startDate = DateUtil.getConcatDateFields(
						DateUtil.getModifiedStartDate(today,
								-Integer.parseInt(Constants.applicationProperties
										.getProperty("no.of.previous.years.to.fetch.daily.data")),
								Calendar.YEAR),
						true);
				String endDate = DateUtil.getConcatDateFields(DateUtil.getModifiedEndDate(today, 0, Calendar.YEAR),
						true);
				socketDataObjectBean.setNoOfDays(1);
				url = new URI(serverUrl + Constants.applicationProperties.getProperty("one.day.interval.api")
				.replace("{symbol}", ticker.getTicker()).replace("{start}", startDate)
				.replace("{end}", endDate));
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		// TickerCandlePriceHistoryResponseBean tickerResponse =
		// restTemplate.getForObject(url,
		// TickerCandlePriceHistoryResponseBean.class);
		/*
		 * ResponseBean tickerResponse = restTemplate.getForObject(url,
		 * ResponseBean.class);
		 */
		CandlePriceHistoryResponseBean tickerResponse = restTemplate.getForObject(url,
				CandlePriceHistoryResponseBean.class);

		JSONObject jsonObject = new JSONObject(tickerResponse.getObjects());
		int apiSerial = Integer.parseInt(ticker.getApiSerial());
		List<AbstractTickerCandlePriceHistory> tickerDataTemp = new ArrayList<AbstractTickerCandlePriceHistory>();
		tickerDataTemp = tickerResponse.getObjects().stream().map(p -> {
			p.setTickerSymbol(ticker.getTicker());
			return p;
		}).collect(Collectors.toList());

		socketDataObjectBean.setAbstractTickerCandlePriceHistory(tickerDataTemp);

		if (apiSerial == 2) {
			tickerDataMapInter.put(ticker.getTicker(), socketDataObjectBean);
		} else {
			tickerDataMapIntra.put(ticker.getTicker(), socketDataObjectBean);
		}
		return jsonObject.toString();
	}

	@SuppressWarnings("unchecked")
	private List<UITickerCalculatedSymbolData> getCalculatedData(SocketCalculateDataObject data, List<AbstractTickerCandlePriceHistory> candleData) {
		List<LiveTicker> dataList = new ArrayList<LiveTicker>();
		ListDataStore<LiveTicker> ds = new MemoryListDataStore<LiveTicker>(null, -1);
		tickerDataList = candleData;
		if(tickerDataList == null){
			if (data.getApiSerial() == Constants.API_TYPE.INTER_DAY.getType()) {
				tickerDataList = tickerDataMapInter.get(data.getTicker()).getAbstractTickerCandlePriceHistory();
			} else {
				tickerDataList = tickerDataMapIntra.get(data.getTicker()).getAbstractTickerCandlePriceHistory();
			}	
		}
		AbstractTickerCandlePriceHistory tickerEntry;
		for (int i = 0; i < tickerDataList.size(); i++) {
			tickerEntry = tickerDataList.get(i);
			LiveTicker liveTicker = new LiveTicker(data.getTicker(), tickerEntry.getClose(), tickerEntry.getTradeDate(),
					tickerEntry.getVolume(), tickerEntry.getHigh(), tickerEntry.getLow());
			dataList.add(liveTicker);
		}
		ds.put(dataList);
		CalculationUtil calculationUtil = new CalculationUtil();
		CalculatedOutput calculatedOutput = calculationUtil.getCalculatedData(data.getAlgorithm(), ds,
				new IndicatorParamBean(data.getPeriod(), data.getFastPeriod(), data.getSlowPeriod(),
						data.getSignalPeriod(), data.getSlowDPeriod(), data.getFastKPeriod(), data.getSlowKPeriod()));

		List<UITickerCalculatedSymbolData> finalConvertedList = convertToTickerCalculatedData(data.getTicker(),
				(List<UITickerCalculatedData>) calculatedOutput.getData(), tickerDataList, data.getAlgorithm(),
				data.getChartIndex(), data.getChartUID());

		return finalConvertedList;
	}

	private List<UITickerCalculatedSymbolData> convertToTickerCalculatedData(String tickerSymbol,
			List<UITickerCalculatedData> dataList, List<AbstractTickerCandlePriceHistory> tickerDataList,
			String algorithm, String chartIndex, String chartUID) {
		List<UITickerCalculatedSymbolData> finalConvertedDataList = new ArrayList<>();
		for (UITickerCalculatedData tickerData : dataList) {
			UITickerCalculatedSymbolData symbolCalculatedBean = new UITickerCalculatedSymbolData();
			if (Constants.IndicatorType.getByValue(algorithm) == IndicatorType.VMA) {
				symbolCalculatedBean.setVolume(tickerData.getVolume());
			}
			symbolCalculatedBean.setClosePrice(tickerData.getClosePrice());
			symbolCalculatedBean.setDate(tickerData.getTradeDate().getTime());
			symbolCalculatedBean.setTickerSymbol(tickerSymbol);
			symbolCalculatedBean.setAlgorithm(algorithm);
			symbolCalculatedBean.setChartIndex(chartIndex);
			symbolCalculatedBean.setChartUID(Integer.parseInt(chartUID));
			finalConvertedDataList.add(symbolCalculatedBean);
		}
		return finalConvertedDataList;
	}

	private void emitTrendLineDataToSameColorGroup(ChartConfig selectedChartConfig){
		List<ChartConfig> sameGroupChartList = ChartGroupUtil.getSameGroupChart(chartConfigs, selectedChartConfig);                                                         
		for(ChartConfig currentChartConfig : sameGroupChartList){

			List<TrendLineParamBean> trendLineParam = new ArrayList<>(selectedChartConfig.getTrendlineParams());
			currentChartConfig.setTrendlineParams(trendLineParam);

			Gson jsonBuilder = new Gson();
			Map<String, Object> chartParameters = new HashMap<String, Object>();
			chartParameters.put("chartUID", currentChartConfig.getChartUID());
			chartParameters.put("trendlineParam", currentChartConfig.getTrendlineParams());

			InternalSocketServer.getInstance().getServerHandler().getBroadcastOperations()
			.sendEvent("drawTrendLinesRequest", jsonBuilder.toJson(chartParameters));
		}
	} 

	private void saveChartPreferencesInLayoutListAndDB(List<LayoutNode> layoutList, ChartPreferenceBean chartPreference, Integer chartUID) {
		for (LayoutNode layoutNode : layoutList) {
			if((layoutNode.getChartUID() != null) && (layoutNode.getChartUID().intValue() == chartUID.intValue())) {
				layoutNode.getChartConfig().setChartParams(chartPreference);
			}
		}
	}

	private void saveChartPreferences(ChartPreferenceBean chartPreferences, Integer chartUID) {
		Integer userId = (Integer) Context.getContext().get(Constants.USERID_KEY);
		try {
			Integer layoutId = userService.getLayoutIdByUserId(userId);
			if(layoutId != null && layoutId > 0) {
				String layoutJson = userService.getLayoutJsonByLayoutId(layoutId);
				if(layoutJson != null) {
					Gson gson = new Gson();
					Type type = new TypeToken<ParentLayout>(){}.getType();
					ParentLayout layoutParent = gson.fromJson(layoutJson, type);
					saveChartPreferencesInLayoutListAndDB(layoutParent.getLayoutList(), chartPreferences, chartUID);
					saveChartPreferencesInLayoutListAndDB(layoutList, chartPreferences, chartUID);
					userService.updateLayoutJson(layoutId, gson.toJson(layoutParent));
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void changeChartUIParameters(ChartPreferenceBean chartPreferences, Integer chartUID) {	
		Map<String, Object> paramsMap = new HashMap<>();

		paramsMap.put(Constants.applicationLanguage.getProperty("label.chart.preferences"), chartPreferences);
		paramsMap.put(Constants.applicationLanguage.getProperty("label.chart.uid"), chartUID);
		String chartParamsString = new Gson().toJson(paramsMap);

		InternalSocketServer.getInstance().getServerHandler().getBroadcastOperations().sendEvent("changeChartUIPreferences", chartParamsString);
		saveChartPreferences(chartPreferences, chartUID);
	}

	public Integer getLayoutId() {
		return layoutId;
	}
	public void setLayoutId(Integer layoutId) {
		this.layoutId = layoutId;
	}

	public List<LayoutNode> getLayoutList() {
		return layoutList;
	}

	public void setLayoutList(List<LayoutNode> layoutList) {
		this.layoutList = layoutList;
	}

	public ParentLayout getParentLayout() {
		return parentLayout;
	}

	public void setParentLayout(ParentLayout parentLayout) {
		this.parentLayout = parentLayout;
	}
	
	public Gson getCustomizedDateFormatterDeserializerGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

			@Override
			public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {
				return new Date(json.getAsJsonPrimitive().getAsLong()); 
			}
		});
		
		return gsonBuilder.create();
	}
}
