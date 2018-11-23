package com.fousalert.controller;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.fousalert.bean.Layout;
import com.fousalert.layout.ParentLayout;
import com.fousalert.service.UserService;
import com.fousalert.utils.ApplicationUtil;
import com.fousalert.utils.Constants;
import com.fousalert.utils.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ChangeLayoutController {
	
	@FXML private Button cancelButton;
	@FXML private Button openButton;
	@FXML private Button deleteButton;
	
	ObservableList<Layout> layouts;
	@FXML private ListView<Layout> layoutListView;
	
	private DashboardController dashboardController;
	
	private UserService userService;
	
	public void setDashboardController(DashboardController dashboardController) {
		this.dashboardController = dashboardController;
	}
	public ChangeLayoutController() {
		userService = new UserService();
	}
	@FXML private void initialize() {
		openButton.setDisable(true);
		deleteButton.setDisable(true);
		setEvents();
		showSavedLayout();
	}
	@FXML private void close() {
		Stage stage = (Stage)cancelButton.getScene().getWindow();
		stage.close();
	}
	@FXML private void openLayout() {
		Layout layout = (Layout)layoutListView.getSelectionModel().getSelectedItem();
		if(layout != null) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle(Constants.applicationLanguage.getProperty("message.change.layout"));
			alert.setHeaderText(Constants.applicationLanguage.getProperty("message.confirm.change.layout").replace("{layout}", layout.layoutNameProperty()));
			alert.initOwner((Stage)cancelButton.getScene().getWindow());

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
				close();
				Integer layoutId = layout.getLayoutID();
				try {
					changeLayout(layoutId);
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
			}
		} else {
			ApplicationUtil.displayAlert("Select a layout");
		}
	}
	@FXML private void deleteLayout() {
		Layout layout = (Layout)layoutListView.getSelectionModel().getSelectedItem();
		if(layout != null) {
			
			if(layout.getLayoutID() == dashboardController.getLayoutId()) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle(Constants.applicationLanguage.getProperty("message.delete.layout"));
				alert.setHeaderText(Constants.applicationLanguage.getProperty("message.delete.and.change.layout").replace("{layout}", layout.layoutNameProperty()));
				alert.initOwner((Stage)cancelButton.getScene().getWindow());
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
					
					Integer layoutId = layout.getLayoutID();
					try{
							Boolean isLayoutDeleted = userService.deleteLayoutByLayoutId(layoutId);
							if(isLayoutDeleted) {
								layouts.remove(layout);
								changeLayout(1);
							}else{
								ApplicationUtil.displayAlert("Unable to delete layout");
							}
						
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
					}
					
				}
				
			} else {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle(Constants.applicationLanguage.getProperty("message.delete.layout"));
				alert.setHeaderText(Constants.applicationLanguage.getProperty("message.confirm.delete.layout").replace("{layout}", layout.layoutNameProperty()));
				alert.initOwner((Stage)cancelButton.getScene().getWindow());
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
					Integer layoutId = layout.getLayoutID();
					try{
						Boolean isLayoutDeleted = userService.deleteLayoutByLayoutId(layoutId);
						if(isLayoutDeleted){
							layouts.remove(layout);
						} else {
							ApplicationUtil.displayAlert("Unable to delete layout");
						}
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} else { 
			ApplicationUtil.displayAlert("Select a layout");
		}
	}
	private void showSavedLayout () {
		try {
			List<Layout> layout = userService.fetchLayoutListByUserId((Integer)Context.getContext().get(Constants.USERID_KEY));
			if(layout != null) {
				layouts = FXCollections.observableList(layout);
				layoutListView.setItems(layouts);
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	private void  setEvents() {
		layoutListView.setCellFactory(new Callback<ListView<Layout>, ListCell<Layout>>(){
			@Override
			public ListCell<Layout> call(ListView<Layout> p) {
				ListCell<Layout> cell = new ListCell<Layout>(){
					@Override
					protected void updateItem(Layout t, boolean bln) {
						super.updateItem(t, bln);
						if (t != null) {
							setText(t.layoutNameProperty());
						} else {
							setText(null);
						}
					}
				};
				return cell;
			}
		});
		
		layoutListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Layout>() {
			@Override
			public void changed(ObservableValue<? extends Layout> arg0, Layout arg1, Layout arg2) {
	        	Layout layout = (Layout)layoutListView.getSelectionModel().getSelectedItem();
	            if(layout.getLayoutID() == 1) { 
	            	openButton.setDisable(false);
	            	deleteButton.setDisable(true);
	            } else {
	            	openButton.setDisable(false);
	            	deleteButton.setDisable(false);
	            }
	        }
	    });
	}
	private void changeLayout(Integer layoutId) throws ClassNotFoundException, SQLException {
		String layoutJson = userService.getLayoutJsonByLayoutId(layoutId);
		if(layoutJson != null) {
			Integer userId = (Integer)Context.getContext().get(Constants.USERID_KEY);
			Boolean islayoutIdChanged =  userService.saveLayoutIdByUserId(userId, layoutId);
			if(islayoutIdChanged){
				dashboardController.setLayoutId(layoutId);
				Type listType = new TypeToken<ParentLayout>() {}.getType();
				Gson gson = new Gson();
				ParentLayout layoutParent = gson.fromJson(layoutJson, listType);
				dashboardController.drawCustomSavedLayout(layoutParent);
			} else {
				ApplicationUtil.displayAlert("Unable to changeLayout");
			}
		} else {
			ApplicationUtil.displayAlert("Couldn't fetch layout");
		}
	}
}
