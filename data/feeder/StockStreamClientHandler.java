/*package com.fousalert.data.feeder;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.fousalert.application.interfaces.ClientHandler;
import com.fousalert.application.interfaces.StockDataReceivable;

import io.netty.channel.ChannelHandlerContext;


public class StockStreamClientHandler extends ClientHandler {

	private List<StockDataReceivable> tickerRecieverList = new ArrayList<StockDataReceivable>();
	
	public StockStreamClientHandler() {
		super();
	}
	
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		//System.out.println(msg);
		try {
			if(tickerRecieverList!=null && !tickerRecieverList.isEmpty()){
					for(StockDataReceivable recievingStockData : tickerRecieverList)
					{   
		           		recievingStockData.receiver(msg);
					}
			}
					
		} catch(JSONException je) {
			je.printStackTrace();
			throw je;
		}
		
	}
	
	

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}
	public void registerReciever(StockDataReceivable recieverStockData){
		
         tickerRecieverList.add(recieverStockData);
	}
	public void stopReceiever(StockDataReceivable recieverStockData)
	{  
		 tickerRecieverList.remove(recieverStockData);
	}
}
*/