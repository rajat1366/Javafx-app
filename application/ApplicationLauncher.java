package com.fousalert.application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fousalert.application.interfaces.Closeable;
import com.fousalert.service.BaseService;
import com.fousalert.utils.ApplicationUtil;
import com.fousalert.utils.Constants;
import com.fousalert.utils.Context;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ApplicationLauncher extends Application {

	private ApplicationUtil singleApplicationInstanceChecker = new ApplicationUtil();
	private BaseService baseService;
	private static final Logger LOGGER = Logger.getLogger(ApplicationLauncher.class.getName());

	@Override
	public void start(Stage primaryStage) {
		try {
			initializeApp();
			if(isApplicationRunning()){  
				ApplicationUtil.displayAlert("Application is already running");
			} else {       
				readProperties("dev");
				readLanguage(Constants.LANG_EN);
				Boolean result = checkAndCreateDB();
				if(result==true){
					Parent root = FXMLLoader.load(getClass().getResource("/LoginController.fxml"));
					Scene scene = new Scene(root);
					primaryStage.setTitle(Constants.applicationLanguage.getProperty("label.fous.alerts") + " v1.22");
					scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
					primaryStage.setScene(scene);
					primaryStage.show();
					primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
						@Override
						public void handle(WindowEvent event) {
							System.exit(0);
						}
					});
					Context.getContext().put(Constants.PARENT_STAGE, primaryStage);
				}
				else{
					ApplicationUtil.displayAlert("Unable to create db");
				}
			}


		}
		catch (Exception e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE,e.toString());
		}
	}
	@Override
	public void stop(){
		singleApplicationInstanceChecker.closeResources();
		System.exit(0);
	}
	private void initializeApp() {
		Context context = Context.getContext();
		context.put(Constants.CLOSEABLE_KEY, new ArrayList<Closeable>());
		baseService = new BaseService();
	}

	public static void main(String[] args) {
		launch(args);

	}

	private boolean isApplicationRunning() throws IOException{

		singleApplicationInstanceChecker.setFile("lock");
		if(singleApplicationInstanceChecker.getFile().exists()){
			singleApplicationInstanceChecker.getFile().delete();
		}
		singleApplicationInstanceChecker.setFileChannel("rw");
		singleApplicationInstanceChecker.setFileLock();

		if(singleApplicationInstanceChecker.getFileLock() == null) {
			return true;
		}
		else {
			return false;
		}

	}

	public void readProperties(String profile) {
		loadPropertiesFromFile("fousalert-" + profile + ".properties", Constants.applicationProperties);
	}
	
	public void readLanguage(String language) {
		loadPropertiesFromFile("fousalert-lang-" + language + ".properties", Constants.applicationLanguage);
	}
	
	public void loadPropertiesFromFile(String file, Properties properties) {
		InputStream input = null;
		try {
			input = getClass().getClassLoader().getResourceAsStream(file);
			properties.load(input);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private boolean checkAndCreateDB() throws ClassNotFoundException, SQLException{
		File file = new File (Constants.applicationProperties.getProperty("db.fileName").replace("{version}", "v1.22"));
		Boolean result = false;
		if(file.exists()) 
		{
			if(file.length()==0) {
				result= baseService.createDB();
			}
			else {
				result= true;
			}
		}
		else{
			result = baseService.createDB();
		}
		return result;
	}

}
