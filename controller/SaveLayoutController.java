package com.fousalert.controller;

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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SaveLayoutController {

	@FXML private TextField layoutNameTextField ;

	@FXML private ListView<Layout> layoutListView;
	@FXML private Button save;
	@FXML private Button cancel;
	private ObservableList<Layout> layouts;
	private UserService userService ; 
	private DashboardController dashboardController; 
	public SaveLayoutController() {
		userService = new UserService();
	}
	public void setDashboardController(DashboardController dashboardController) {
		this.dashboardController = dashboardController;
	}
	@FXML private void initialize() {
		setEvents();
		showSavedLayout();
	}

	private void showSavedLayout () {
		try {
			List<Layout> layout = userService.fetchLayoutListByUserId((Integer)Context.getContext().get(Constants.USERID_KEY));
			if(layout != null) {
				layouts = FXCollections.observableList(layout);
				layoutListView.setItems(layouts);
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML private void saveLayout() {
		close();
		ParentLayout parentLayout = dashboardController.getParentLayout();
		parentLayout.setLayoutList(dashboardController.getLayoutList());
		String json = new Gson().toJson(parentLayout);
		Integer userId = (Integer) Context.getContext().get(Constants.USERID_KEY);
		String layoutName = layoutNameTextField.getText();
		if(layoutName != null && !layoutName.isEmpty()) {
			try {
				Integer layoutId = userService.saveLayoutListByUserId(userId, json, layoutName);
				if(layoutId != null ) {
					boolean ispreferencesSaved = userService.saveLayoutIdByUserId(userId, layoutId);
					if(ispreferencesSaved) {
						dashboardController.setLayoutId(layoutId);
					} else {
						ApplicationUtil.displayAlert("Couldn't save layout.");
					}
					
				} else {
					ApplicationUtil.displayAlert("Couldn't save layout.");
				}
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		} else {
			ApplicationUtil.displayAlert(Constants.applicationLanguage.getProperty("message.specify.layout.name"));
		}
	}
	
	@FXML private void close() {
		Stage stage = (Stage)save.getScene().getWindow();
		stage.close();
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
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirm Overwrite?");
				
				String overwriteMessage = Constants.applicationLanguage.getProperty("message.confirm.overwrite.layout").replace("{layout}", layout.layoutNameProperty());
				alert.setHeaderText(overwriteMessage);
				
				alert.initOwner((Stage)save.getScene().getWindow());

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
					close();
					ParentLayout parentLayout = dashboardController.getParentLayout();
					parentLayout.setLayoutList(dashboardController.getLayoutList());
					String json = new Gson().toJson(parentLayout);
					Integer userId = (Integer)Context.getContext().get(Constants.USERID_KEY);
					try {
						boolean isLayoutTransactionCompleted = userService.updateLayoutJson(layout.getLayoutID(), json);
						if(isLayoutTransactionCompleted) {
							boolean ispreferencesSaved = userService.saveLayoutIdByUserId(userId, layout.getLayoutID());
							if(ispreferencesSaved) {
								dashboardController.setLayoutId(layout.getLayoutID());
							}else{
								ApplicationUtil.displayAlert("Couldn't save layout.");
							}
						}else {
							ApplicationUtil.displayAlert("Couldn't save layout.");
						}
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}
