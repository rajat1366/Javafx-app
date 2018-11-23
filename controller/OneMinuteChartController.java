package com.fousalert.controller;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.fousalert.ZeroMqClient;
import com.fousalert.application.interfaces.Closeable;
import com.fousalert.application.interfaces.StockDataReceivable;
import com.fousalert.utils.Constants;
import com.fousalert.utils.Constants.DataFlowType;
import com.fousalert.utils.Context;
import com.fousalert.utils.InternalSocketClient;
import com.fousalert.utils.InternalSocketServer;
import com.fousalert.utils.StreamerNettyClient;
import com.github.nkzawa.emitter.Emitter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class OneMinuteChartController implements Closeable,StockDataReceivable {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
   @FXML private WebView oneMinuteChart;
   @FXML private Button backButton;
   private WebEngine webEngine;
  
   private StreamerNettyClient streamerNettyClient;
    
	private ZeroMqClient zMQClient = null;

	@FXML private void onBackButtonClicked(ActionEvent event) {
		Stage stage = (Stage) Context.getContext().get(Constants.PARENT_STAGE);
		Scene scene = (Scene) Context.getContext().get(Constants.PARENT_SCENE);
		stage.setScene(scene);
	}
	
   @FXML private void initialize() throws URISyntaxException {
	   
	  try {
	
		  startRecievingOneMinuteData();
	   registerInternalSocketServerHandlers();
	   registerInternalSocketClientHandlers();
	   
	   webEngine = oneMinuteChart.getEngine();
		webEngine.load(getClass().getResource("/oneMinute.html").toExternalForm());
	   
	   webEngine.getLoadWorker().stateProperty().addListener(
				new ChangeListener<State>() {
					public void changed(ObservableValue observaleValue, State oldState, State newState) {
						if (newState == State.SUCCEEDED) {
							
//							webEngine.executeScript( "  candleChart.getData() " );   

						}
					}
				});
	   
	   
	   } catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while initialize OneMinuteChartController", e);
		}
	   
   }
   private void startRecievingOneMinuteData(){
	   try {
		streamerNettyClient = new StreamerNettyClient(DataFlowType.ONE_MINUTE, new DashboardController());
		streamerNettyClient.connect(Constants.applicationProperties.getProperty("stock.data.feeder.host"),Integer.parseInt(Constants.applicationProperties.getProperty("stock.data.feeder.port.one.minute")));
	   } catch (Exception e) {
		   e.printStackTrace();
	   }
	   
	 }
   
   private void registerInternalSocketClientHandlers() throws Exception {
	   InternalSocketClient.getInstance().getClientHandler().on("subscribeTickerOneMinute", new Emitter.Listener() {
		  public void call(Object... response) {
			  	streamerNettyClient.getChannel().writeAndFlush(response[0].toString() + "\r\n");
		  }
		});
	   
	   
	   InternalSocketClient.getInstance().getClientHandler().on("unSubscribeTickerOneMinute", new Emitter.Listener() {
			  public void call(Object... response) {
				  streamerNettyClient.getChannel().writeAndFlush(Constants.STREAMER_UNSUBSCRIBE_TICKER_PREFIX + response[0].toString() + "\r\n");
			  }
			});
		}

private void registerInternalSocketServerHandlers() {
	InternalSocketServer.getInstance().getServerHandler().addEventListener("subscribeTickerOneMinute", String.class, new DataListener<String>() {
		@Override
		public void onData(SocketIOClient arg0, String data, AckRequest arg2) throws Exception {
				System.out.println(data);
				InternalSocketServer.getInstance().getServerHandler().getBroadcastOperations().sendEvent("subscribeTickerOneMinute", data);
				
			}
		});
	   
	InternalSocketServer.getInstance().getServerHandler().addEventListener("unSubscribeTickerOnMinute", String.class, new DataListener<String>() {
           public void onData(SocketIOClient client, String data, AckRequest ackRequest) {
        	   InternalSocketServer.getInstance().getServerHandler().getBroadcastOperations().sendEvent("unSubscribeTickerOneMinute", data);
           }
       });
       
	InternalSocketServer.getInstance().getServerHandler().addEventListener("publishTickerDataOneMinute", String.class, new DataListener<String>() {
           public void onData(SocketIOClient client, String data, AckRequest ackRequest) {
        	   InternalSocketServer.getInstance().getServerHandler().getBroadcastOperations().sendEvent("publishTickerDataOneMinute", data);
           }
       });
	   
   }
   
	@Override
	public void close() {
		streamerNettyClient.disconnect();
	}

	@Override
	public void receiver(DataFlowType dataFlowType, String msg) {
		System.out.println(msg);
		InternalSocketServer.getInstance().getServerHandler().getBroadcastOperations().sendEvent("publishTickerDataOneMinute", msg);
	}
}
