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
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoadingAppDialog extends Application {

	private Timeline task;
	private String titleMessage;
	private String dialogContent;
	private Stage progressBarStage = new Stage(StageStyle.TRANSPARENT);
	
	@Override 
	public void start(Stage stage) {
		task = new Timeline();

		Label pleaseWait = new Label(titleMessage);
		pleaseWait.setStyle("-fx-font-weight: BOLD");
		pleaseWait.setTextFill(Color.WHITE);
		pleaseWait.setFont(Font.font(pleaseWait.getFont().getFamily(), 20));
		
		Label loadingData = new Label(dialogContent);
		loadingData.setTextFill(Color.WHITE);
		
	    VBox layout = new VBox(10);
	    layout.getChildren().setAll(
	        pleaseWait,
	        loadingData
	    );
	    layout.setPadding(new Insets(10));
	    layout.setAlignment(Pos.CENTER);
	    layout.setStyle("-fx-background-color: #2a2a2a");
	    
	    Scene progressScene = new Scene(layout, 400, 150);
	    
	    Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - 500) / 2); 
        stage.setY((primScreenBounds.getHeight() - 150) / 2); 
	    
	    stage.setScene(progressScene);
	    stage.initModality(Modality.APPLICATION_MODAL);
	    stage.show();
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