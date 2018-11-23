package com.fousalert.customfx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class F1ListTitleBar extends DockTitleBar {
	private static final long serialVersionUID = 4099748205621320794L;
	
	private Button filterButton;

	public F1ListTitleBar(String title) {
		super(title);
		
		filterButton = new Button();
		filterButton.getStyleClass().add("setting-icon");
		StackPane.setAlignment(filterButton, Pos.CENTER_RIGHT);
		
		this.getChildren().add(filterButton);
	}

	public Button getFilterButton() {
		return filterButton;
	}

	public void setFilterButton(Button filterButton) {
		this.filterButton = filterButton;
	}
}