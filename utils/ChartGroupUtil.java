package com.fousalert.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fousalert.bean.ChartConfig;

public class ChartGroupUtil {

		public static List<ChartConfig> getSameGroupChart(Map<Integer, ChartConfig> chartConfigs,ChartConfig selectedChartConfig) {
			List<ChartConfig> sameGroupChartList = new ArrayList<ChartConfig>();
			for(Map.Entry<Integer, ChartConfig> chartConfig:chartConfigs.entrySet()) {
				ChartConfig currentChartConfig=(ChartConfig)chartConfig.getValue();
				if(currentChartConfig != selectedChartConfig && !currentChartConfig.getGroupColor().equals("#ffffff")) {
					if(currentChartConfig.getGroupColor().equals(selectedChartConfig.getGroupColor())) {
						sameGroupChartList.add(currentChartConfig);
					}
				}
			}
			return sameGroupChartList;
		}
}
