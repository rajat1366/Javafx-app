package com.fousalert.utils;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

public class InternalSocketServer {
	
	private static InternalSocketServer instance;
	private SocketIOServer serverHandler = null;
	
	private InternalSocketServer() {
		
		Configuration config = new Configuration();
	    config.setHostname(Constants.INTERNAL_SOCKET_SERVER_HOST);
	    config.setPort(Constants.INTERNAL_SOCKET_SERVER_PORT);
	    serverHandler = new SocketIOServer(config);
	    serverHandler.start();
	}
	
	public static InternalSocketServer getInstance()
	{
		if(instance == null)
		{
			instance = new InternalSocketServer();
		}
		
		return instance;
	}
	
	public static void destroyInstance()
	{
		if(instance != null)
		{
			instance.getServerHandler().stop();
		}
	}

	public SocketIOServer getServerHandler() {
		return serverHandler;
	}
	public static void removeInstance(){
		instance = null;
	}
}
