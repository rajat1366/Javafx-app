package com.fousalert.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class ApplicationUtil {

	 private File file=null;
	 private FileChannel fileChannel=null;
	 private FileLock fileLock=null;
	 
	 public ApplicationUtil(){}
	 
	public File getFile() {
		return file;
	}
	public void setFile(String fileName) {
		this.file = new File(fileName);
	}
	public FileChannel getFileChannel() {
		return fileChannel;
	}
	public void setFileChannel(String mode ) throws FileNotFoundException {

		if(this.file !=null)
		{
		this.fileChannel =  new RandomAccessFile(this.file, mode).getChannel();
		}
		
	}
	public FileLock getFileLock() {
		return fileLock;
	}
	public void setFileLock() throws IOException {
		if(this.fileChannel != null)
		{
		this.fileLock = this.fileChannel.tryLock();
		}
		
	}
   public void closeResources()
   { 
	   
	   try{
			if( fileLock != null)
			{
				fileLock.release();
				fileChannel.close();
				file.delete();
			}
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
   }
   
	public static void displayAlert(String msg) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle(msg);
				alert.setHeaderText(msg);
				alert.showAndWait();
			}
		});
		
		
	}
	
	public static String getStringifiedStartDateWithTrailingZeros(Date date) {
		String finalStringDate = null;
		DecimalFormat decimalFormat= new DecimalFormat("00");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		finalStringDate =  "" + calendar.get(Calendar.YEAR) + decimalFormat.format(Double.valueOf((calendar.get(Calendar.MONTH) + 1))) + decimalFormat.format(Double.valueOf((calendar.get(Calendar.DAY_OF_MONTH)))) + "000000";
		
		return finalStringDate;
	}
	
	public static void playAlertSound() throws URISyntaxException {
		String soundMedia = Constants.applicationProperties.getProperty("audio.alert.sound"); 
		URL resource = ApplicationUtil.class.getResource(soundMedia);
		playSound(resource);
	}

	public static void playSound(URL audioUrl) throws URISyntaxException {
		/*URL audioUrl = getClass().getResource("/audio/Titan.mp3");
		ApplicationUtil.playSound(audioUrl);*/
		Media audioMedia = new Media(audioUrl.toURI().toString());
		MediaPlayer player = new MediaPlayer(audioMedia);
		player.play();
	}
	
}
