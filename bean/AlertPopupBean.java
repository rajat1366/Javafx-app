package com.fousalert.bean;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AlertPopupBean {

	private Label messageLabel;
	private Label dateLabel;
	private final String OK_BUTTON = "OK";
	private final String EDIT_BUTTON = "EDIT";
	private Button okButton;
	private Button editButton;
	private StackPane stackPane;
	private VBox parentVBox;
	private TrendLineParamBean trendLine;
	
	public AlertPopupBean(String messageLabel, String dateLabel) {
		super();
		this.messageLabel = new Label(messageLabel);
		this.dateLabel = new Label(dateLabel);
		this.messageLabel.getStyleClass().add("white-text-color");
		this.dateLabel.getStyleClass().add("white-text-color");
		
		okButton = new Button(OK_BUTTON);
		editButton = new Button(EDIT_BUTTON);
	}
	
	public AlertPopupBean(String messageLabel, String dateLabel, TrendLineParamBean trendline) {
		this(messageLabel, dateLabel);
		this.trendLine = trendline;
	}
	
	public StackPane getFormattedAlertPane() {
		ImageView imageView = new ImageView(new Image(getClass().getResource("/alerticon.png").toExternalForm()));
		imageView.setFitHeight(100.0);
		imageView.setFitWidth(50.0);
		imageView.setStyle("-fx-background-color: red");
		
		stackPane = new StackPane();
		stackPane.setMinWidth(290.0);
		stackPane.setMaxWidth(290.0);
		stackPane.setMinHeight(100.0);
		stackPane.setMaxHeight(100.0);
		stackPane.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
		stackPane.getStyleClass().add("alert-popup-background");
		stackPane.getStyleClass().addAll("transparent-background", "scrollpane-transparent-background");
		
		okButton.getStyleClass().addAll("transparent-background", "white-text-color");
		editButton.getStyleClass().addAll("transparent-background", "white-text-color");

		HBox buttonHbox = new HBox(editButton, okButton);
		StackPane.setAlignment(buttonHbox, Pos.BOTTOM_RIGHT);
		
		StackPane contentPane = new StackPane();
		VBox messageVBox = new VBox(messageLabel, dateLabel);
		messageVBox.setPadding(new Insets(20.0, 0.0, 0.0, 0.0));
		contentPane.getChildren().addAll(messageVBox);
		contentPane.setMinWidth(235);
		contentPane.setMaxWidth(240);
		contentPane.setMinHeight(80);
		contentPane.setMaxHeight(80);
		contentPane.getStyleClass().addAll("transparent-background", "scrollpane-transparent-background");
		StackPane.setMargin(contentPane, new Insets(0.0, 0.0, 0.0, 10.0));
		messageLabel.setMinWidth(235);
		dateLabel.setMinWidth(235);
		messageLabel.setAlignment(Pos.CENTER);
		dateLabel.setAlignment(Pos.CENTER);
		StackPane.setAlignment(messageVBox, Pos.CENTER);
		
		
		HBox imageViewHBox = new HBox(imageView);
		imageViewHBox.getStyleClass().addAll("transparent-background", "scrollpane-transparent-background");
		imageViewHBox.setStyle("-fx-background-color: #49b649");
		imageViewHBox.setPadding(new Insets(0, 10, 0, 10));
		/*imageViewHBox.setMaxHeight(10);
		imageViewHBox.setPrefHeight(10);*/
		
		HBox hbox = new HBox(imageViewHBox, contentPane);
		buttonHbox.setMaxWidth(80);
		buttonHbox.setMaxHeight(20);
		StackPane.setMargin(hbox, new Insets(0.0, 0.0, 0.0, 5.0));
		stackPane.getChildren().addAll(hbox, buttonHbox);
		StackPane.setAlignment(buttonHbox, Pos.BOTTOM_RIGHT);
		hbox.getStyleClass().addAll("transparent-background", "scrollpane-transparent-background");
		
		okButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				parentVBox.getChildren().remove(stackPane);
			}
		});
		
		return stackPane;
	}

	public Button getEditButton() {
		return editButton;
	}

	public void setParentVBox(VBox parentVBox) {
		this.parentVBox = parentVBox;
	}

	public TrendLineParamBean getTrendLine() {
		return trendLine;
	}

	public void setTrendLine(TrendLineParamBean trendLine) {
		this.trendLine = trendLine;
	}
}