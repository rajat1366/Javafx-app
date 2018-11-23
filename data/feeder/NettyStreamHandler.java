package com.fousalert.data.feeder;


import org.json.JSONException;

import com.fousalert.application.interfaces.ClientHandler;
import com.fousalert.controller.DashboardController;
import com.fousalert.utils.ConnectionLostDialog;
import com.fousalert.utils.Constants.DataFlowType;

import io.netty.channel.ChannelHandlerContext;


public class NettyStreamHandler extends ClientHandler {

	private DashboardController dashboardController;
	private DataFlowType chartType = null;

	public NettyStreamHandler(DataFlowType chartType, DashboardController dashboardController) {
		super();
		this.chartType = chartType;
		this.dashboardController = dashboardController;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		try {
			if(dashboardController!=null){
				dashboardController.receiver(chartType, msg);
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
		ConnectionLostDialog dialog = new ConnectionLostDialog();
		dialog.showLostConnectionDialog();
	}
}
