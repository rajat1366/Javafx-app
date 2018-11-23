package com.fousalert.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fousalert.bean.AbstractTickerCandlePriceHistory;
import com.fousalert.bean.ChartConfig;
import com.fousalert.bean.IndicatorParamBean;
import com.fousalert.bean.SocketDataCacheObject;
import com.fousalert.bean.UITickerCalculatedSymbolData;
import com.fousalert.calculationEngine.algo.indicator.CalculatedOutput;
import com.fousalert.calculationEngine.algo.indicator.UITickerCalculatedData;
import com.fousalert.datastore.bean.LiveTicker;
import com.fousalert.datastore.storage.ListDataStore;
import com.fousalert.datastore.storage.impl.MemoryListDataStore;
import com.fousalert.utils.Constants.IndicatorType;
import com.fousalert.utils.Constants.TickerDuration;
import com.google.gson.Gson;

public class SplitTickerCandleUpdator {

	private TickerDataUtil tickerDataUtil = new TickerDataUtil();
	private CalculationUtil calculationUtil = new CalculationUtil();

	@SuppressWarnings("unchecked")
	public void updateTickerCandle(AbstractTickerCandlePriceHistory candle, Map<String, SocketDataCacheObject> tickerDataMapIntra, Map<String, SocketDataCacheObject> tickerDataMapInter, Map<Integer, ChartConfig> chartConfigs) {
		Collection<ChartConfig> chartConfigList = chartConfigs.values();

		for (TickerDuration tickerDuration : TickerDuration.values()) {
			if(!tickerDuration.getTickerDuration().equalsIgnoreCase(TickerDuration.DAILY.toString())) {
				Integer currentDataInterval = Integer.parseInt(tickerDuration.getTickerDuration());
				
				List<ChartConfig> minuteWiseChartConfigs = chartConfigList.stream().filter(chartConfig -> {
					return chartConfig.getDataDuration() == currentDataInterval.intValue();
				}).collect(Collectors.toList());

				for(ChartConfig chartConfig : minuteWiseChartConfigs) {
					List<AbstractTickerCandlePriceHistory> convertedData = tickerDataUtil.getSplittedData(tickerDataMapIntra, tickerDataMapInter, currentDataInterval, chartConfig.getTickerSymbol(), 1);
					ListDataStore<LiveTicker> ds = new MemoryListDataStore<LiveTicker>(null, -1);
					List<LiveTicker> dataList = new ArrayList<LiveTicker>();
					List<IndicatorParamBean> params = new ArrayList<IndicatorParamBean>();
					params = chartConfig.getIndicatorParams();

					convertedData = convertedData.stream().filter(realtimeCandle -> realtimeCandle.getHigh().compareTo(0d) != 0 && realtimeCandle.getLow().compareTo(0d) != 0 && realtimeCandle.getOpen().compareTo(0d) != 0 && realtimeCandle.getClose().compareTo(0d) != 0).collect(Collectors.toList());
					
					Map<String, Object> finalOutput = new HashMap<String, Object>();
					finalOutput.put(Constants.CANDLE_DATA_KEY, convertedData);
					finalOutput.put(Constants.CHART_UID_KEY, chartConfig.getChartUID());

					AbstractTickerCandlePriceHistory tickerEntry = null;
					if(tickerDataMapIntra.get(chartConfig.getTickerSymbol()) != null) {
						List<AbstractTickerCandlePriceHistory> tickerDataList = tickerDataMapIntra.get(chartConfig.getTickerSymbol()).getAbstractTickerCandlePriceHistory();

						for(int i = 0; i < convertedData.size(); i++){
							tickerEntry = convertedData.get(i);
							LiveTicker liveTicker = new LiveTicker(tickerEntry.getTickerCode(), tickerEntry.getClose(), tickerEntry.getTradeDate(), tickerEntry.getVolume());
							dataList.add(liveTicker);
						}

						ds.put(dataList);


						Map<String, List<UITickerCalculatedSymbolData>> indicatorDataMap = new HashMap<>();

						for(IndicatorParamBean param : params) {
							CalculatedOutput calculatedOutput = calculationUtil.getCalculatedData(param.getAlgorithm(), ds, param);
							indicatorDataMap.put(param.getIndicatorUID().toString(), convertToTickerCalculatedData(candle.getTickerSymbol(), ((List<UITickerCalculatedData>) calculatedOutput.getData()), tickerDataList, param.getAlgorithm(), "", chartConfig.getChartUID()));
						}
						finalOutput.put("indicatorData", indicatorDataMap);

						Gson jsonBuilder = new Gson();
						if(currentDataInterval.equals(chartConfig.getDataDuration())) {
							InternalSocketServer.getInstance().getServerHandler().getBroadcastOperations().sendEvent("publishTickerDataOneMinuteCron", jsonBuilder.toJson(finalOutput));			
						}
					}
				}
			}
		}
	}

	private List<UITickerCalculatedSymbolData> convertToTickerCalculatedData(String tickerSymbol, List<UITickerCalculatedData> dataList,List<AbstractTickerCandlePriceHistory> tickerDataList, String algorithm, String chartIndex, int chartUID) {
		List<UITickerCalculatedSymbolData> finalConvertedDataList = new ArrayList<>();
		for(UITickerCalculatedData tickerData : dataList) {
			UITickerCalculatedSymbolData symbolCalculatedBean = new UITickerCalculatedSymbolData();
			if (Constants.IndicatorType.getByValue(algorithm) == IndicatorType.VMA) {
				symbolCalculatedBean.setVolume(tickerData.getVolume());
			}
			symbolCalculatedBean.setClosePrice(tickerData.getClosePrice());
			if(tickerData.getTradeDate() != null) {				
				symbolCalculatedBean.setDate(tickerData.getTradeDate().getTime());
			}
			symbolCalculatedBean.setTickerSymbol(tickerSymbol);
			symbolCalculatedBean.setAlgorithm(algorithm);
			symbolCalculatedBean.setChartIndex(chartIndex);

			symbolCalculatedBean.setChartUID(chartUID);
			finalConvertedDataList.add(symbolCalculatedBean); 
		}
		return finalConvertedDataList;
	}
}