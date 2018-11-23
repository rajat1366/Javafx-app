package com.fousalert.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.fousalert.bean.AbstractTickerCandlePriceHistory;
import com.fousalert.bean.SocketDataCacheObject;

public class TickerDataUtil {
	
	
	public List<AbstractTickerCandlePriceHistory> getSplittedDataNew(List<AbstractTickerCandlePriceHistory> intraDayTickerCandlePriceHistories, int splitDuration, String ticker, int apiType) {

		List<AbstractTickerCandlePriceHistory> tickerCandlePriceHistories = new ArrayList<AbstractTickerCandlePriceHistory>();
		int maxMinute = 0;
		if(!intraDayTickerCandlePriceHistories.isEmpty())
		{
			if(splitDuration == 1) {
				for (AbstractTickerCandlePriceHistory intraDayTickerCandlePriceHistory : intraDayTickerCandlePriceHistories) {
					intraDayTickerCandlePriceHistory.setApiSerial(apiType);
					intraDayTickerCandlePriceHistory.setTickerSymbol(ticker);
					//intraDayTickerCandlePriceHistory.setTradeDate(intraDayTickerCandlePriceHistory.getTradeDate());
					if(intraDayTickerCandlePriceHistory.getTradeDate() != null) {
						intraDayTickerCandlePriceHistory.setDate(intraDayTickerCandlePriceHistory.getTimeStamp());						
					}
					intraDayTickerCandlePriceHistory.setSplitDuration(splitDuration);					
				}
				tickerCandlePriceHistories = intraDayTickerCandlePriceHistories;
			} else {
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(intraDayTickerCandlePriceHistories.get(0).getTimeStamp());
				int minutes = calendar.get(Calendar.MINUTE);
				if(minutes < splitDuration)
				{
					maxMinute = splitDuration;
				}
				else if(minutes % splitDuration == 0)
				{
					maxMinute = minutes + splitDuration;
				}
				else{
					maxMinute = ((minutes/splitDuration)+1)*splitDuration;
				}
				
				List<AbstractTickerCandlePriceHistory> durationBatchList = new ArrayList<>(splitDuration);
				for (AbstractTickerCandlePriceHistory intraDayTickerCandlePriceHistory : intraDayTickerCandlePriceHistories) {
					calendar = Calendar.getInstance();
					/*if(intraDayTickerCandlePriceHistory.getTradeDate() == null) {
						intraDayTickerCandlePriceHistory.setTradeDate(intraDayTickerCandlePriceHistory.getTimeStamp()));
					}*/
					calendar.setTimeInMillis(intraDayTickerCandlePriceHistory.getTimeStamp());
					minutes = calendar.get(Calendar.MINUTE);
					if((maxMinute == 0 && minutes > (60 - splitDuration) && minutes <= 59) || maxMinute > 0 && minutes < maxMinute)
					{
						durationBatchList.add(intraDayTickerCandlePriceHistory);
					}
					else
					{
						Double open = durationBatchList.get(0).getOpen();
						//durationBatchList.get(0).getTradeDate();
						Double close = durationBatchList.get(durationBatchList.size()-1).getClose();
						Double high = durationBatchList.stream().max(Comparator.comparingDouble(AbstractTickerCandlePriceHistory::getHigh)).get().getHigh();
						Double low = durationBatchList.stream().min(Comparator.comparingDouble(AbstractTickerCandlePriceHistory::getLow)).get().getLow();
						long volume = durationBatchList.stream().mapToLong(volume1 -> volume1.getVolume()).sum();
						
						Calendar conslidatedCalendar = Calendar.getInstance();
						conslidatedCalendar.setTimeInMillis(durationBatchList.get(0).getTimeStamp());
						conslidatedCalendar.set(Calendar.MINUTE, maxMinute - splitDuration);
						
						AbstractTickerCandlePriceHistory consolidatedTickerCandlePriceHistory = durationBatchList.get(0);
						consolidatedTickerCandlePriceHistory.setOpen(open);
						consolidatedTickerCandlePriceHistory.setClose(close);
						consolidatedTickerCandlePriceHistory.setHigh(high);
						consolidatedTickerCandlePriceHistory.setLow(low);
						//consolidatedTickerCandlePriceHistory.setTradeDate(conslidatedCalendar.getTime());
						consolidatedTickerCandlePriceHistory.setVolume(volume);
						consolidatedTickerCandlePriceHistory.setApiSerial(apiType);
						consolidatedTickerCandlePriceHistory.setTickerSymbol(ticker);
						consolidatedTickerCandlePriceHistory.setDate(conslidatedCalendar.getTime().getTime());
						consolidatedTickerCandlePriceHistory.setSplitDuration(splitDuration);
						tickerCandlePriceHistories.add(consolidatedTickerCandlePriceHistory);
						
						
						maxMinute = maxMinute + splitDuration;
						if(maxMinute == 60)
						{
							maxMinute = 0;
						}
						durationBatchList = new ArrayList<>(splitDuration);
						durationBatchList.add(intraDayTickerCandlePriceHistory);
					}
				}
				
			}


		}
		
		return tickerCandlePriceHistories;
	}
	
	
	
	
	

	public List<AbstractTickerCandlePriceHistory> getSplittedData(Map<String, SocketDataCacheObject> tickerDataMapIntra, Map<String, SocketDataCacheObject> tickerDataMapInter, int splitDuration, String tickerSymbol, int apiType) {
		List<AbstractTickerCandlePriceHistory> result = new ArrayList<AbstractTickerCandlePriceHistory>();
		List<AbstractTickerCandlePriceHistory> tickerDataList = new ArrayList<AbstractTickerCandlePriceHistory>();
		
		if(apiType == 2) {
			if(tickerDataMapInter.get(tickerSymbol) != null) {
				tickerDataList = tickerDataMapInter.get(tickerSymbol).getAbstractTickerCandlePriceHistory();
				for(AbstractTickerCandlePriceHistory tickerData : tickerDataList) {
					tickerData.setApiSerial(apiType);
					tickerData.setTickerSymbol(tickerSymbol);
					//tickerData.setTradeDate(tickerData.getTradeDate());
					//tickerData.setDate(tickerData.getTradeDate().getTime());
					tickerData.setSplitDuration(splitDuration);
				}				
			}
		}
		else{
			if(tickerDataMapIntra.get(tickerSymbol) != null) {
				tickerDataList = tickerDataMapIntra.get(tickerSymbol).getAbstractTickerCandlePriceHistory();
				tickerDataList = getSplittedDataNew(tickerDataList, splitDuration, tickerSymbol, apiType);
			}
		}
		
		

		/*int splitDurationVar = Integer.parseInt(splitDuration);
		if(Integer.parseInt(splitDuration) != 1 ){

			int startVar = 0;
			int endVar = startVar + Integer.parseInt(splitDuration);
			double openCustom;
			double closeCustom;
			double highCustom;
			double lowCustom;
			Date dateCustom;
			long volumeCustom;
			long date;

			AbstractTickerCandlePriceHistory abstractTickerCandlePriceHistoryTemp = new AbstractTickerCandlePriceHistory();
			abstractTickerCandlePriceHistoryTemp.setClose(tickerDataList.get(startVar).getClose());
			abstractTickerCandlePriceHistoryTemp.setHigh(tickerDataList.get(startVar).getHigh());
			abstractTickerCandlePriceHistoryTemp.setLow(tickerDataList.get(startVar).getLow());
			abstractTickerCandlePriceHistoryTemp.setOpen(tickerDataList.get(startVar).getOpen());
			
			if(tickerDataList.get(startVar).getTradeDate() == null) {
				abstractTickerCandlePriceHistoryTemp.setTradeDate(new Date(tickerDataList.get(startVar).getTimeStamp()));
			} else {
				abstractTickerCandlePriceHistoryTemp.setTradeDate(tickerDataList.get(startVar).getTradeDate());				
			}
			
			abstractTickerCandlePriceHistoryTemp.setVolume(tickerDataList.get(startVar).getVolume());
			abstractTickerCandlePriceHistoryTemp.setTimeStamp(tickerDataList.get(startVar).getTimeStamp());
			abstractTickerCandlePriceHistoryTemp.setDate(tickerDataList.get(startVar).getTradeDate().getTime());
			abstractTickerCandlePriceHistoryTemp.setTickerSymbol(tickerDataList.get(startVar).getTickerSymbol());
			abstractTickerCandlePriceHistoryTemp.setApiSerial(apiSerial);
			abstractTickerCandlePriceHistoryTemp.setSplitDuration(splitDurationVar);
			result.add(abstractTickerCandlePriceHistoryTemp);

			while(endVar < tickerDataList.size()){
				AbstractTickerCandlePriceHistory tickerEntry = tickerDataList.get(startVar);
				openCustom = tickerEntry.getOpen();
				highCustom = tickerEntry.getHigh();
				lowCustom = tickerEntry.getLow();
				int i;
				for(i = startVar + 1; i <= endVar; i++){
					tickerEntry = tickerDataList.get(i);
					if(highCustom < tickerEntry.getHigh()){
						highCustom = tickerEntry.getHigh();
					}
					if(lowCustom > tickerEntry.getLow()){
						lowCustom = tickerEntry.getLow();
					}
				}
				tickerEntry = tickerDataList.get(i-1);
				closeCustom = tickerEntry.getClose();
				volumeCustom = tickerEntry.getVolume();
				if(tickerEntry.getTradeDate() == null) {
					dateCustom = new Date(tickerEntry.getTimeStamp());
				} else {
					dateCustom = tickerEntry.getTradeDate();
				}
				date = dateCustom.getTime();

				AbstractTickerCandlePriceHistory abstractTickerCandlePriceHistory = new AbstractTickerCandlePriceHistory();
				abstractTickerCandlePriceHistory.setClose(closeCustom);
				abstractTickerCandlePriceHistory.setHigh(highCustom);
				abstractTickerCandlePriceHistory.setLow(lowCustom);
				abstractTickerCandlePriceHistory.setOpen(openCustom);
				abstractTickerCandlePriceHistory.setTradeDate(dateCustom);
				abstractTickerCandlePriceHistory.setVolume(volumeCustom);
				abstractTickerCandlePriceHistory.setDate(date);
				abstractTickerCandlePriceHistory.setTickerSymbol(tickerSymbol);
				abstractTickerCandlePriceHistory.setApiSerial(apiSerial);
				abstractTickerCandlePriceHistory.setSplitDuration(splitDurationVar);
				if(abstractTickerCandlePriceHistory.getOpen() != 0 && abstractTickerCandlePriceHistory.getClose() != 0){
					result.add(abstractTickerCandlePriceHistory);					
				}
				startVar = i;
				endVar = i + splitDurationVar -1;
			}
		} 
		else {
			for(int i = 0; i < tickerDataList.size(); i++){
				AbstractTickerCandlePriceHistory abstractTickerCandlePriceHistoryTemp = new AbstractTickerCandlePriceHistory();
				abstractTickerCandlePriceHistoryTemp.setClose(tickerDataList.get(i).getClose());
				abstractTickerCandlePriceHistoryTemp.setHigh(tickerDataList.get(i).getHigh());
				abstractTickerCandlePriceHistoryTemp.setLow(tickerDataList.get(i).getLow());
				abstractTickerCandlePriceHistoryTemp.setOpen(tickerDataList.get(i).getOpen());
				if(tickerDataList.get(i).getTradeDate() == null) {
					tickerDataList.get(i).setTradeDate(new Date(tickerDataList.get(i).getTimeStamp()));
				} else {
					abstractTickerCandlePriceHistoryTemp.setTradeDate(tickerDataList.get(i).getTradeDate());					
				}
				abstractTickerCandlePriceHistoryTemp.setVolume(tickerDataList.get(i).getVolume());
				abstractTickerCandlePriceHistoryTemp.setDate(tickerDataList.get(i).getTradeDate().getTime());
				abstractTickerCandlePriceHistoryTemp.setTickerSymbol(tickerSymbol);
				abstractTickerCandlePriceHistoryTemp.setApiSerial(apiSerial);
				abstractTickerCandlePriceHistoryTemp.setSplitDuration(splitDurationVar);
				if(abstractTickerCandlePriceHistoryTemp.getOpen() != 0 && abstractTickerCandlePriceHistoryTemp.getClose() != 0){
					result.add(abstractTickerCandlePriceHistoryTemp);					
				}
			}
		}*/

		return tickerDataList;
	}
	
}
