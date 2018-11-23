package com.fousalert.customfx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

public class WatchTitleBar extends DockTitleBar {

	private Button addTickerButton;
	
	public WatchTitleBar(String title) {
		super(title);
		Pane fillPane = new Pane();
		HBox.setHgrow(fillPane, Priority.ALWAYS);

		addTickerButton = new Button();
		addTickerButton.setVisible(false);
		addTickerButton.setDisable(true);
		addTickerButton.setId("add-ticker-button");

		StackPane.setAlignment(label, Pos.CENTER_LEFT);
		StackPane.setAlignment(addTickerButton, Pos.CENTER_RIGHT);
		addTickerButton.getStyleClass().add("dock-add-ticker-button");

		getChildren().addAll(fillPane, addTickerButton);
	}
	
	public Button getAddTickerButton() {
		return addTickerButton;
	}
	public void setAddTickerButton(Button addTickerButton) {
		this.addTickerButton = addTickerButton;
	}

}