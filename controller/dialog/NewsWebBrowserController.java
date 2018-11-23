package com.fousalert.controller.dialog;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import com.fousalert.bean.NewsBean;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class NewsWebBrowserController implements Initializable {
	
	private Stage stage;
	@FXML private Label addressLabel;
	@FXML private Hyperlink reloadHyperlink;
	@FXML private Hyperlink copyHyperlink;
	@FXML private Hyperlink openInBrowserHyperlink;
	@FXML private Button closeWebBrowserBtn;
	@FXML private WebView newsWebView;
	
	private NewsBean newsBean;
	
	public NewsWebBrowserController() {
		super();
	}
	
	
	@FXML
	public void closeClickHandler() {
		stage.close();
	}
	
	@FXML
	public void reloadHyperlinkClickHandler() {
		openPage();
	}
	
	@FXML
	public void copyHyperlinkClickHandler() {
		final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(newsBean.getUrl());
        clipboard.setContent(content);
	}
	
	@FXML
	public void openInBrowserHyperlinkClickHandler() {
		 try {
			Desktop.getDesktop().browse(new URI(newsBean.getUrl()));
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
	
	public void openPage() {
		WebEngine webEngine = newsWebView.getEngine();
		webEngine.load(newsBean.getUrl());
		addressLabel.setText(newsBean.getUrl());
	}


	public NewsBean getNewsBean() {
		return newsBean;
	}
	public void setNewsBean(NewsBean newsBean) {
		this.newsBean = newsBean;
	}
}