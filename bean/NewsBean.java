package com.fousalert.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fousalert.controller.dialog.NewsWebBrowserController;
import com.fousalert.controller.dialog.PrePostDataDialogController;
import com.fousalert.utils.Constants;
import com.fousalert.utils.Context;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NewsBean {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Label newsHeading;
	private Label newsSource;
	private Hyperlink newsTitle;
	private Label time;
	private String url;
	
	private NewsBean self;

	public NewsBean(String newsHeading, String newsSource, String url, String time) {
		super();
		this.newsHeading = new Label(newsHeading);
		this.newsSource = new Label(newsSource);
		this.newsTitle = new Hyperlink(newsHeading);
		this.newsTitle.setWrapText(true);
		this.newsTitle.getStyleClass().add("custom-hyperlink");
		this.time = new Label(time);
		this.url = url;

		setupEvents();
	}
	
	private void setupEvents() {
		self = this;
		newsTitle.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				FXMLLoader webBrowserFxmlLoader = new FXMLLoader(getClass().getResource("/views/NewsWebBrowserView.fxml"));
				try {
					Parent root = (Parent) webBrowserFxmlLoader.load();
					Scene scene = new Scene(root);
					scene.getStylesheets().add(getClass().getResource("/dialogBox.css").toExternalForm());
					Stage parentStage = (Stage)Context.getContext().get(Constants.PARENT_STAGE);
					Stage stage = new Stage(StageStyle.TRANSPARENT);
					stage.setScene(scene);
					stage.initOwner(parentStage);
					stage.show();
					NewsWebBrowserController controller = webBrowserFxmlLoader.getController();
					controller.setStage(stage);
					controller.setNewsBean(self);
					
					controller.openPage();
				} catch(Exception e) {
					e.printStackTrace();
					logger.error("Error while opening the news in the in-app browser.");
				}
				
			}
		});
	}
	
	public StackPane getFormattedNews() {
		StackPane stackPane = new StackPane();
		HBox content = new HBox();
		VBox container = new VBox();
	
	    stackPane.setPadding(new Insets(10.0, 20.0 ,10.0,0.0 ));
		
		newsHeading.prefWidthProperty().bind(stackPane.widthProperty());
	    newsSource.prefWidthProperty().bind(stackPane.widthProperty());
	    time.prefWidthProperty().bind(stackPane.widthProperty());
	    
	    newsHeading.getStyleClass().add("news-heading");
	    newsSource.getStyleClass().add("news-source");
	    time.getStyleClass().add("news-time");
	    time.setAlignment(Pos.TOP_RIGHT);
	    
	    newsHeading.setWrapText(true);
	    newsSource.setWrapText(true);
	    time.setWrapText(true);
	    
	    content.getChildren().addAll(newsSource,time);
		
		stackPane.getStyleClass().addAll("news-border");
		
		container.getChildren().addAll(newsTitle, content);
		stackPane.getChildren().addAll(container);
		
		return stackPane;
	}

	
	
	public Label getNewsHeading() {
		return newsHeading;
	}

	public void setNewsHeading(Label newsHeading) {
		this.newsHeading = newsHeading;
	}

	public Label getNewsSource() {
		return newsSource;
	}

	public void setNewsSource(Label newsSource) {
		this.newsSource = newsSource;
	}

	public Hyperlink getNewsTitle() {
		return newsTitle;
	}

	public void setNewsTitle(Hyperlink newsTitle) {
		this.newsTitle = newsTitle;
	}

	public Label getTime() {
		return time;
	}

	public void setTime(Label time) {
		this.time = time;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}