package com.fousalert.application.interfaces;

import com.fousalert.utils.Constants.DataFlowType;

public interface StockDataReceivable {

	 void receiver(DataFlowType chartType, String msg);
}
