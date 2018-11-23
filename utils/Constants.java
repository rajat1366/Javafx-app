package com.fousalert.utils;

import java.util.Properties;

public class Constants {

	public static Properties applicationProperties = new Properties();
	public static Properties applicationLanguage = new Properties();

	public static final String STREAMER_UNSUBSCRIBE_TICKER_PREFIX = "unsubscribe";

	public static final String PARENT_STAGE = "parentStage";
	public static final String PARENT_SCENE = "parentScene";
	public static final String PARENT_CONTAINER = "parentContainer";

	public static final String TICKER_LIST_AS_STRING = "tickerListAsString";

	public static final String CLOSEABLE_KEY = "closeable";
	public static final String BROWSER_KEY = "browser";
	public static final String USERID_KEY = "userId";
	public static final String STOCK_STREAM_CLIENT_HANDLER_KEY = "stockStreamHandler";
	public static final String LOCAL_SOCKET_SERVER_KEY = "localSocketServer";
	public static final String LOCAL_SOCKET_SERVER_SOCKET_KEY = "socket";

	public static final String INTERNAL_SOCKET_SERVER_HOST = "localhost";
	public static final int INTERNAL_SOCKET_SERVER_PORT = 9092;

	public static final String EMPTY_STRING = "";
	public static final double ZERO_DOUBLE = 0.0d;

	public static final String FULL_TICKER_LIST_KEY = "fullTickerList";

	public static final String CANDLE_DATA_KEY = "candleData";

	public static final Object SCHEDULER_KEY = "scheduler";

	public static final String CALCULATION_JOB_DATA_SCHEDULER_KEY = "calculationJobData";

	public static final String SELECTED_INDICATORS_KEY = "selectedIndicators";

	public static final String CHART_UID_KEY = "chartUID";

	public static final String CHART_DATA_DURATION_INTERVAL_KEY = "chartDataDuration";

	public static final String CHART_CONFIG_MAP_KEY = "chartConfigs";

	public static final String TICKER_PATTERN_REGEX = "[a-zA-Z-_]+";
	public static final String LANG_EN = "en";
	public static final String NEGATIVE_POSITIVE_NUMBER_REGEX = "-?[0-9]+";
	public static final String POSITIVE_NUMBER_REGEX="[0-9]+";
	public static final Object APP_LOADER = "appLoader";

	public static final String DATE_TIME_FORMAT = "MM-dd-yyyy hh:mm";
	public static final String TIME_FORMAT = "hh:mm";
	public static final String DATE_TIME_FORMAT_ALERT_POPUP = "hh:mm MMMMM dd";
	
	public static final int DECIMALS_TO_ROUND_OF = 2;
	public static final String ACTIONABLE = "actionable";
	
	public static final long PRICE_MIN = 0L;
	public static final long PRICE_MAX = 1000000L;
	
	public static final long VOLUME_MIN = 0L;
	public static final long VOLUME_MAX = 1000000L;

	public static final String UNDERSCORE = "_";
	public static final String MINUTE = " Min";
	public static final String HOUR = " Hour";
	
	public static final String CHART_NODE = "chartNode";
	public static final String SIDE_NODE = " sideNode";
	public static final String MIDDLE_PANE = "middlePane";
	public static final String LEFT_PANE = " leftPane";
	public static final String RIGHT_PANE = "rightPane";
	public static final String LAYOUT_PARENT = "layoutParent";
	public static final String LAYOUT_LIST = "layoutList";
	
	public static final String TIMEZONE_EST_ID = "America/New_York";
	public static final String DECIMAL_NUMBER_FORMAT_2 = ".00";
	public static final String SESSION_ID="sessionId";
	public static final String ENTITLEMENTS="entitlements";
	public static final String SESSION_KEYWORD="JSESSIONID_";
	
	public static final double HUNDRED_DOUBLE = 100d;
	public static final int DEFAULT_ALERT_WAKE_MINUTES = 1;
	public static final long DEFAULT_ALERT_WAKE_MILLIS = DEFAULT_ALERT_WAKE_MINUTES * 60 * 1000;
	
	public static final String DEFAULT_TRENDLINE_COLOR="#ffffff";
	public static final Double DEFAULT_TRENDLINE_WIDTH=1.0;
	
	public enum DataFlowType {

		REALTIME("realtime"), ONE_MINUTE("oneMinute"), F1("f1"), PARABOLIC("parabolic"), GAP("gap"), ALL_ALERTS("allAlerts");

		private String dataFlowType;

		private DataFlowType(String dataFlowType) {
			this.dataFlowType = dataFlowType;
		}

		public String getDataFlowType() {
			return dataFlowType;
		}

		public void setDataFlowType(String dataFlowType) {
			this.dataFlowType = dataFlowType;
		}
    }


	public enum IndicatorType {

		SMA("sma"), EMA("ema"), RSI("rsi"), VMA("vma"), MACD("macd"), STOCH("stoch"), MOVING_AVG("movingAvg");
		private String indicatorType;

		private IndicatorType(String indicatorType) {
			this.indicatorType = indicatorType;
		}

		public String getIndicatorType() {
			return indicatorType;
		}

		public void setIndicatorType(String indicatorType) {
			this.indicatorType = indicatorType;
		}

		public static IndicatorType getByValue(String indicator) {
			IndicatorType finalIndicatorType = null;
			for(IndicatorType indicatorType : values()) {
				if(indicatorType.getIndicatorType().equalsIgnoreCase(indicator)) {
					finalIndicatorType = indicatorType;
				}
			}

			return finalIndicatorType;
		}
	}
	
	public enum API_TYPE {
		INTRA_DAY, INTER_DAY;
		
		public static API_TYPE getByValue(int value) {
			return values()[value - 1];
		}
		
		public int getType() {
			return ordinal() + 1;
		}
		
	}
	
	public enum TickerDuration {

		DAILY("Daily", "Daily"), ONE_MIN("1", "1"), TWO_MIN("2", "2"), FIVE_MIN("5", "5"), TEN_MIN("10", "10"), FIFTEEN_MIN("15", "15"), THIRTY_MIN("30", "30"), HOUR("1", "60");
		
		private String tickerDuration;
		private String minutes;

		private TickerDuration(String tickerDuration, String minutes) {
			this.tickerDuration = tickerDuration;
			this.minutes = minutes;
		}

		public String getTickerDuration() {
			return tickerDuration;
		}

		public void setTickerDuration(String tickerDuration) {
			this.tickerDuration = tickerDuration;
		}

		public String getMinutes() {
			return minutes;
		}

		public void setMinutes(String minutes) {
			this.minutes = minutes;
		}
	}
	
	public enum DockPanePosition {

		LEFT("left"), MIDDLE("middle"), RIGHT("right");
		
		private String dockPanePosition;

		private DockPanePosition(String dockPanePosition) {
			this.dockPanePosition = dockPanePosition;
		}

		public String getDockPanePosition() {
			return dockPanePosition;
		}

		public void setDockPanePosition(String dockPanePosition) {
			this.dockPanePosition = dockPanePosition;
		}
	}
	
	public enum SideDockNodeTitle {

		NEWS(Constants.applicationLanguage.getProperty("label.news")), MASTER_LIST(Constants.applicationLanguage.getProperty("label.scanner.log")), WATCH_LIST(Constants.applicationLanguage.getProperty("label.watch.list")), 
		F1_LIST(Constants.applicationLanguage.getProperty("label.f1.breakouts")), PARABOLIC_LIST(Constants.applicationLanguage.getProperty("label.parabolic.over.under.short")), 
		GAP_LIST(Constants.applicationLanguage.getProperty("label.gap.to.short")), SQUEEZE_ZONE(Constants.applicationLanguage.getProperty("label.squeeze.zone"));
		
		private String sideDockNodeTitle;

		private SideDockNodeTitle(String sideDockNodeTitle) {
			this.sideDockNodeTitle = sideDockNodeTitle;
		}

		public String getSideDockNodeTitle() {
			return sideDockNodeTitle;
		}

		public void setSideDockNodeTitle(String sideDockNodeTitle) {
			this.sideDockNodeTitle = sideDockNodeTitle;
		}
	}
	
	public enum Entitlements {

		CANDLE_FEED("candle_feed"), ALGO_FEED("algo_feed"), REAL_TIME_FEED("real_time_feed");
		
		private String entitlements;

		private Entitlements(String entitlements) {
			this.entitlements = entitlements;
		}

		public String getEntitlements() {
			return entitlements;
		}

		public void setEntitlements(String entitlements) {
			this.entitlements = entitlements;
		}
	}
	
	public enum AlertDirection {
		UP, DOWN;
	}
}