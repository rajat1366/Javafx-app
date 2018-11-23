package com.fousalert.customfx;
/**
 * @author  Kuldeep Singh
 * @since   2015-02-10 
 */
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AppLoaderDialog extends Application {

	private Timeline task;
	private String titleMessage;
	private String dialogContent;
	private Stage progressBarStage = new Stage(StageStyle.TRANSPARENT);
	
	private Label titleLbl = new Label();
	private Label descriptionLbl = new Label();
	
	@Override 
	public void start(Stage stage) {
		task = new Timeline();

		titleLbl.setText(titleMessage);
		titleLbl.setStyle("-fx-font-weight: BOLD");
		titleLbl.setTextFill(Color.WHITE);
		titleLbl.setFont(new Font("Open Sans", 16));
		//titleLbl.setFont(Font.font(titleLbl.getFont().getFamily(), 20));
		
		descriptionLbl.setText(dialogContent);
		descriptionLbl.setFont(new Font("Open Sans", 16));
		descriptionLbl.setTextFill(Color.WHITE);
		
	    VBox layout = new VBox(10);
	    layout.getChildren().setAll(titleLbl, descriptionLbl);
	    layout.setPadding(new Insets(10));
	    layout.setAlignment(Pos.CENTER);
	    layout.setStyle("-fx-background-color: #2a2a2a");
	    
	    Scene progressScene = new Scene(layout, 400, 150);
	    
	    Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - 500) / 2); 
        stage.setY((primScreenBounds.getHeight() - 150) / 2); 
	    
	    stage.setScene(progressScene);
	    stage.setAlwaysOnTop(true);
	    //stage.initModality(Modality.APPLICATION_MODAL);
	    stage.show();
	}
	
	public String getTitleMessage() {
		return titleMessage;
	}
	public void setTitleMessage(String titleMessage) {
		titleLbl.setText(titleMessage);
		this.titleMessage = titleMessage;
	}

	public String getDialogContent() {
		return dialogContent;
	}
	public void setDialogContent(String dialogContent) {
		descriptionLbl.setText(dialogContent);
		this.dialogContent = dialogContent;
	}





	public void startProgressBar(String titleMessage, String dialogContent) {	
		this.titleMessage = titleMessage;
		this.dialogContent = dialogContent;
		try {
			progressBarStage.initStyle(StageStyle.UNDECORATED);
		} catch(Exception e) {
			e.printStackTrace();
		}
		start(progressBarStage);
		task.playFromStart();
	}
	
	public void stopProgressBar() {
		progressBarStage.close();
	}
}