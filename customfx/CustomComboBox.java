package com.fousalert.customfx;

import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CustomComboBox {
	
	private Stage stage = new Stage();
	private ListView<String> listView = new ListView<String>();
	private TextField textField = new TextField();
	
	public CustomComboBox(double x, double y) {
		super();
		VBox vbox = initializeComponents();
		setupStageAndShow(vbox, x, y);
		textField.getStyleClass().add("ticker-text-field");
		listView.getStyleClass().add("ticker-list-view");
		stage.show();
	}
	
	private void setupStageAndShow(VBox vbox, double x, double y) {
		Scene scene = new Scene(vbox);
		stage.setScene(scene);
		stage.getScene().getWindow().setX(x);
		stage.getScene().getWindow().setY(y);
		stage.initStyle(StageStyle.TRANSPARENT);
	}
	
	private VBox initializeComponents() {
		VBox vbox = new VBox();
		vbox.getChildren().addAll(textField, listView);
		vbox.setMaxSize(90, 300);
		return vbox;
	}

	public ListView<String> getListView() {
		return listView;
	}

	public TextField getTextField() {
		return textField;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}