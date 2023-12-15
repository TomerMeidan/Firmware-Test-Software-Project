package controllers;

import java.io.IOException;
import java.net.URL;

import org.json.simple.JSONObject;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.ClientWindow;

@SuppressWarnings("restriction")
public class ClientPortalView implements PortalViewInterface {

	private Stage primaryStage;
	private ComController com;
	private VBox homePageVBox;
	private ClientWindow clientWindow;

	public ClientPortalView(Stage primaryStage, ComController com) {
		this.primaryStage = primaryStage;
		this.com = com;
	}

	@Override
	public void handleMsg(Object descriptor) {

		switch ((String) ((JSONObject) descriptor).get("command")) {
		case "online":
			clientWindow.onStatusConnected();
			break;
		case "handshake":

			switch ((String) ((JSONObject) descriptor).get("notOk")) {

			default:
				break;
			}
			break;
		case "server response":
			break;

		default:
			break;
		}
	}

	@Override
	public void init() {
		try {
			FXMLLoader loader = new FXMLLoader();
			ClassLoader classLoader = ClientPortalView.class.getClassLoader();
			URL url = classLoader.getResource("fxml/template.fxml");

			loader.setLocation(url);
			homePageVBox = loader.load();
			clientWindow = loader.getController();
			clientWindow.init(homePageVBox, primaryStage, this);
			clientWindow.showWindow();
		} catch (IOException e) {
			System.out.println("ClientPortalView: init: IOException was thrown");
			e.printStackTrace();
		}
	}

	@Override
	public ComController getClientController() {
		return com;
	}

}
