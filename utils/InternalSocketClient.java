package com.fousalert.utils;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

public class InternalSocketClient {
	
	
	private static InternalSocketClient instance;
	private Socket clientHandler = null;
	
	private InternalSocketClient() throws Exception {
		
		IO.Options options = new IO.Options();
		options.forceNew = true;
		
		String internalSocketServerURI = "http://" + Constants.INTERNAL_SOCKET_SERVER_HOST + ":" + Constants.INTERNAL_SOCKET_SERVER_PORT;
		
		clientHandler = IO.socket(internalSocketServerURI,options);
		clientHandler.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
				public void call(Object... args) {}
				}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
				public void call(Object... error) {}
				});
		clientHandler.connect();
		}
	
	public static InternalSocketClient getInstance() throws Exception
	{
		if(instance == null)
		{
			instance = new InternalSocketClient();
		}
		
		return instance;
	}
	
	public static void destroyInstance()
	{
		if(instance != null)
		{
			instance.getClientHandler().disconnect();
			instance = null;
		}
	}

	public Socket getClientHandler() {
		return clientHandler;
	}
	public static void removeInstance(){
		instance = null;
	}

}
