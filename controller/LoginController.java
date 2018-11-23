package com.fousalert.controller;

import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fousalert.bean.Login;
import com.fousalert.commonconstants.UtilityConstants;
import com.fousalert.customfx.AppLoaderDialog;
import com.fousalert.restservice.LoginTemplate;
import com.fousalert.service.UserService;
import com.fousalert.utils.ApplicationUtil;
import com.fousalert.utils.Constants;
import com.fousalert.utils.Context;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class LoginController implements Initializable {

	@FXML private TextField username;
	@FXML private TextField password;
	@FXML private Button loginButton;
	@FXML private Label status;

	private boolean isAuthenticated = false;
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	private UserService userService= new UserService();
	private AppLoaderDialog appLoader;
	
	@FXML
	public void Login(ActionEvent event) throws Exception {
		Context context = Context.getContext();

		LoginTemplate loginclient = new LoginTemplate();
		Login login = new Login();
		login.setEmail(username.getText());
		login.setPassword(password.getText());

		AuthenticateUser(loginclient,login,event,context);


	}
	@FXML
	public void handleEnterPressed(KeyEvent event) throws Exception{
		if (event.getCode() == KeyCode.ENTER) {

			Context context = Context.getContext();

			LoginTemplate loginclient = new LoginTemplate();
			Login login = new Login();
			login.setEmail(username.getText());
			login.setPassword(password.getText());

			AuthenticateUser(loginclient,login,event,context);
		}
	}
	
	private Service<Void> getAuthenticationService(LoginTemplate loginclient,Login login,Event event,Context context) {
		Service<Void> syncServiceTask = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						username.getStyleClass().remove("invalid-login-color");
						Object userId = loginclient.loginAuthentication(login);
						System.out.println(userId);
						if (userId !=null) {
							try {
								isAuthenticated = true;
								Integer userId1=userService.fetchUserId(username.getText());
								if(userId1==null){
									ApplicationUtil.displayAlert("Unable to access db");
								} else {    
									if( userId1 > 0 ) {
										context.put(Constants.USERID_KEY, userId1);
									} else {  
										Integer parsedUserId = Integer.parseInt(userId.toString());
										Boolean result = userService.insertUserDetails(parsedUserId,username.getText());
										if(result) { 
											context.put(Constants.USERID_KEY, parsedUserId);
										} else {
											ApplicationUtil.displayAlert("unable to create user in the database");
										}
									}
								}
							}
							catch (ClassNotFoundException | SQLException e) {
								username.getStyleClass().add("invalid-login-color");
								logger.error("Error has occurred. Full details:", e);
								e.printStackTrace();
							} catch(Exception exception) {
								username.getStyleClass().add("invalid-login-color");
								logger.error("Error has occurred. Full details:", exception);
								exception.printStackTrace();
							}
						} else {
							username.getStyleClass().add("invalid-login-color");
							onFail();
						}
						return null;
					}
				};
			}
		};
		return syncServiceTask;
	}
	
	private EventHandler<WorkerStateEvent> setOnSyncSucceded = new EventHandler<WorkerStateEvent>() {
		@Override
		public void handle(WorkerStateEvent event) {
			appLoader.stopProgressBar();
			appLoader = null;
			if(isAuthenticated) {
				((Stage)Context.getContext().get(Constants.PARENT_STAGE)).hide();
				DashboardController dashboardController = new DashboardController();
				try {
					dashboardController.initialize();
				} catch (SchedulerException | URISyntaxException e) {
					e.printStackTrace();
				} 
			} else {
				((Stage)Context.getContext().get(Constants.PARENT_STAGE)).show();
				status.setText(Constants.applicationLanguage.getProperty("error.log.in"));
				status.getStyleClass().add("invalid-login-message-color");
			}
			
		}
	};
	
	private void AuthenticateUser(LoginTemplate loginclient,Login login,Event event,Context context) {   
		appLoader = new AppLoaderDialog();
		appLoader.startProgressBar(Constants.applicationLanguage.getProperty("label.please.wait"), "");
		Service<Void> syncServiceTask = getAuthenticationService(loginclient, login, event, context);
		syncServiceTask.restart();
		syncServiceTask.setOnSucceeded(setOnSyncSucceded);
	}

	public void onFail() {
		isAuthenticated = false;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		username.setText("user1@fousalert.com");
		password.setText("password");
	}
}
