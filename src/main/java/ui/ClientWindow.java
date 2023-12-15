package ui;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import controllers.ClientPortalView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import model.GroupCommand;
import model.TypeCommand;
import util.JsonFileReader;

public class ClientWindow {

	private Stage primaryStage;
	private VBox homePageVBox;
	private Scene scene;
	private ClientPortalView view;
	private JSONArray commandsArrayDictionary;
	private HashMap<String, TypeCommand> types;

	// BUTTONS
	@FXML
	private Button addCommandButton;

	@FXML
	private Button clearCommandButton;

	@FXML
	private Button commandSaveButton;

	@FXML
	private Button commandSendButton;

	// LABELS
	@FXML
	private Label commandDescriptionTextBox;

	@FXML
	private Label commandUnitTextBox;

	@FXML
	private Label controllerConnectionStatus;

	@FXML
	private Label lastCommandStatusLabel;

	// COMBO BOXS
	@FXML
	private ComboBox<String> comboAxis;

	@FXML
	private ComboBox<String> comboGroup;

	@FXML
	private ComboBox<String> comboName;

	@FXML
	private ComboBox<String> comboType;

	// OTHERS
	@FXML
	private ListView<String> commandsListView;

	@FXML
	private TextField dataInputTextBox;

	@FXML
	void onAddButtonClick(MouseEvent event) {

	}

	@FXML
	void onClearButtonClick(ActionEvent event) {

		// Clear the selection
		comboType.getSelectionModel().clearSelection();
		comboGroup.getSelectionModel().clearSelection();
		comboName.getSelectionModel().clearSelection();
		comboAxis.getSelectionModel().clearSelection();

		// Disable the ComboBox
		comboGroup.setDisable(true);
		comboName.setDisable(true);

		System.out.println("ClientWindow: command line cleared");

	}

	@FXML
	void onCommandButtonPress(ActionEvent event) {

	}

	@FXML
	void onSaveFileButtonClick(MouseEvent event) {

	}

	@FXML
	void onSendCommandButtonClick(MouseEvent event) {

	}

	@FXML
	void onComboTypeClick(ActionEvent event) {
		handleComboBoxSelection(event);

	}

	public void init(VBox homePageVBox, Stage primaryStage, ClientPortalView clientPortalView) {
		this.homePageVBox = homePageVBox;
		this.primaryStage = primaryStage;
		this.view = clientPortalView;
		scene = new Scene(homePageVBox);
		initDataTypes();
		initAxisTypes();
	}

	/**
	 * Show Window
	 * <p>
	 * 
	 * This method initiates the FXML main scene for the CEO home page. After
	 * showing the window, the method creates a JSON Object that will send a
	 * response, To the server side confirming that the window is now showing for
	 * the client.
	 * 
	 */
	public void showWindow() {
		// log
		System.out.println("ClientWindow: showWindow: showing the client window");

		Platform.runLater(() -> {
			primaryStage.setScene(scene);
			primaryStage.show();
		});
	}

	public void onStatusConnected() {
		Platform.runLater(() -> {
			// log
			System.out.println("ClientWindow: updating status to: connected");

			controllerConnectionStatus.setText("ONLINE");
			controllerConnectionStatus.setTextFill(Paint.valueOf("GREEN"));
			commandSaveButton.setDisable(false);

		});
	}

	public void onStatusDisconnected() {
		Platform.runLater(() -> {
			// log
			System.out.println("ClientWindow: updating status to: disconnected");

			controllerConnectionStatus.setText("OFFLINE");
			controllerConnectionStatus.setTextFill(Paint.valueOf("RED"));
			commandSaveButton.setDisable(true);
		});
	}

	public void initDataTypes() {
		String filePath = "src/main/java/util/commandsDictionary.json";
		commandsArrayDictionary = JsonFileReader.readJsonFile(filePath);
		
		types = new HashMap<>();

        
		for (int i = 0; i < commandsArrayDictionary.size(); i++) {
			JSONObject json = (JSONObject) commandsArrayDictionary.get(i);
			TypeCommand typeCommand = new TypeCommand(json);
			types.put(typeCommand.getName(), typeCommand);
			comboType.getItems().add((String) json.get("type"));
		}
		
		System.out.println("Test");
	}

	public void initAxisTypes() {
		comboAxis.getItems().addAll("Yaw", "Pitch", "Roll");
	}

	private void handleComboBoxSelection(ActionEvent event) {
		ComboBox<String> comboBox = (ComboBox<String>) event.getTarget();
		String selectedItem = (String) comboBox.getSelectionModel().getSelectedItem();
		if (selectedItem == null)
			return;
		String comboBoxID = comboBox.getId();

		switch (comboBoxID) {
			case "comboType":

				// Clear the selection
				comboGroup.getSelectionModel().clearSelection();
				comboName.getSelectionModel().clearSelection();

				// Disable the ComboBox
				comboGroup.setDisable(true);
				comboName.setDisable(true);

				System.out.println("Selected command type: " + selectedItem);
				
				TypeCommand typeCommand = types.get(selectedItem);
				
//		        ObservableList<String> items = FXCollections.observableArrayList();
//		        for (GroupCommand groupCommand : typeCommand.getGroups().values()) {
//		            items.add(groupCommand.getName());
//		        }

				// Build the group type list

				break;
			case "comboGroup":
				System.out.println("Selected command group: " + selectedItem);
				break;
			case "comboName":
				System.out.println("Selected command name: " + selectedItem);
				break;

			default:
				break;
		}

	}

}
