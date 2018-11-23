package com.fousalert.utils;

import com.fousalert.bean.IndicatorParamBean;
import com.fousalert.calculationEngine.algo.indicator.CalculatedOutput;
import com.fousalert.calculationEngine.algo.indicator.MovingIndicator;
import com.fousalert.calculationEngine.algo.indicator.MovingIndicatorProvider;
import com.fousalert.datastore.bean.LiveTicker;
import com.fousalert.datastore.storage.ListDataStore;
import com.fousalert.utils.Constants.IndicatorType;

public class CalculationUtil {
	
	@SuppressWarnings("unchecked")
	public CalculatedOutput getCalculatedData(String algorithm, ListDataStore<LiveTicker> ds, IndicatorParamBean params) {
		
		CalculatedOutput calculatedOutput = new CalculatedOutput();
		int dataListSize =  ds.getAll().size();
		Integer period = params.getPeriod();
		if(algorithm.equalsIgnoreCase(IndicatorType.RSI.getIndicatorType())){
			MovingIndicator<LiveTicker> taRsiMovingIndicator = MovingIndicatorProvider.getMovingIndicatorProvider().getRSIMovingIndicator(period, dataListSize);
			calculatedOutput = taRsiMovingIndicator.calculate(ds);
		} else if(algorithm.equalsIgnoreCase(IndicatorType.SMA.getIndicatorType())){
			MovingIndicator<LiveTicker> taEmaMovingIndicator = MovingIndicatorProvider.getMovingIndicatorProvider().getSMAMovingIndicator(period, dataListSize);
			calculatedOutput = taEmaMovingIndicator.calculate(ds);
		}
		else if(algorithm.equalsIgnoreCase(IndicatorType.EMA.getIndicatorType())){
			MovingIndicator<LiveTicker> taEmaMovingIndicator = MovingIndicatorProvider.getMovingIndicatorProvider().getEMAMovingIndicator(period, dataListSize);
			calculatedOutput = taEmaMovingIndicator.calculate(ds);
		}
		else if(algorithm.equalsIgnoreCase(IndicatorType.VMA.getIndicatorType())){
			MovingIndicator<LiveTicker> taVmaMovingIndicator = MovingIndicatorProvider.getMovingIndicatorProvider().getVMAMovingIndicator(period, dataListSize);
			calculatedOutput = taVmaMovingIndicator.calculate(ds);
		}
		else if(algorithm.equalsIgnoreCase(IndicatorType.MACD.getIndicatorType())){
			MovingIndicator<LiveTicker> taMacdMovingIndicator = MovingIndicatorProvider.getMovingIndicatorProvider().getMACDMovingIndicator(dataListSize, params.getFastPeriod(), params.getSlowPeriod(), params.getSignalPeriod());
			calculatedOutput = taMacdMovingIndicator.calculate(ds);
		} else if(algorithm.equalsIgnoreCase(IndicatorType.STOCH.getIndicatorType())) {
			//MovingIndicator<LiveTicker> taStochasticMovingIndicator = MovingIndicatorProvider.getMovingIndicatorProvider().getSTOCHMovingIndicator(dataListSize, data.getFastPeriod(), data.getSlowPeriod(), data.getSlowDPeriod());
			MovingIndicator<LiveTicker> taStochasticMovingIndicator = MovingIndicatorProvider.getMovingIndicatorProvider().getSTOCHMovingIndicator(dataListSize, params.getFastKPeriod(), params.getSlowKPeriod(), params.getSlowDPeriod());
			calculatedOutput = taStochasticMovingIndicator.calculate(ds);
		} else if(algorithm.equalsIgnoreCase(IndicatorType.MOVING_AVG.getIndicatorType())) {
			MovingIndicator<LiveTicker> movingAverage = MovingIndicatorProvider.getMovingIndicatorProvider().getMovingIndicator(period, dataListSize);
			calculatedOutput = movingAverage.calculate(ds);
		}
		
		return calculatedOutput;
	}
	
	
}
