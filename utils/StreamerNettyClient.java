package com.fousalert.utils;

import com.fousalert.controller.DashboardController;
import com.fousalert.data.feeder.NettyStreamHandler;
import com.fousalert.data.feeder.StockStreamClientInitializer;
import com.fousalert.utils.Constants.DataFlowType;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class StreamerNettyClient {
	
	private EventLoopGroup eventLoop;
	private Bootstrap bootstrap;
	private Channel channel;
	private NettyStreamHandler clientHandler;
	
	public StreamerNettyClient(DataFlowType chartType, DashboardController dashboardController) throws Exception {
		 this.clientHandler = new NettyStreamHandler(chartType, dashboardController);
		
		 eventLoop = new NioEventLoopGroup();
		 bootstrap = new Bootstrap()
	               .group(eventLoop)
	               .channel(NioSocketChannel.class)
	               .handler(new StockStreamClientInitializer(clientHandler));
		
		}
	
	public void connect(String host,int port) throws Exception
	{
		 channel = bootstrap.connect(host,port).sync().channel();
		 
	}
	
	public void disconnect()
	{
		if(eventLoop != null){
				eventLoop.shutdownGracefully();
		}
	}
	
	public Channel getChannel() {
		return channel;
	}
}
