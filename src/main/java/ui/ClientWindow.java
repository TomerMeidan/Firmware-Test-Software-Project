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
import model.NameCommand;
import model.TypeCommand;
import util.JsonFileReader;

public class ClientWindow {

	private Stage primaryStage;
	private VBox homePageVBox;
	private Scene scene;
	private ClientPortalView view;
	private JSONArray commandsArrayDictionary;
	private HashMap<String, TypeCommand> types;

	// Selected items from gui
	private String selectedType = null;
	private String selectedGroup = null;
	private String selectedCommandName = null;
	private String selectedAxis = null;

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
	private Label commandFormatTextBox;

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
	void onComboCommandNameClick(ActionEvent event) {
		handleComboBoxSelection(event);

	}

	@FXML
	void onComboGroupClick(ActionEvent event) {
		handleComboBoxSelection(event);

	}

	@FXML
	void onComboAxisClick(ActionEvent event) {
		handleComboBoxSelection(event);

	}

	@FXML
	void onAddButtonClick(MouseEvent event) {

	}

	@FXML
	void onClearButtonClick(ActionEvent event) {

		// Clear the selection
		comboType.getSelectionModel().clearSelection();
		comboGroup.getItems().clear();
		comboName.getItems().clear();
		comboAxis.getSelectionModel().clearSelection();

		// Disable the ComboBox
		comboGroup.setDisable(true);
		comboName.setDisable(true);

		selectedType = null;
		selectedGroup = null;
		selectedCommandName = null;
		selectedAxis = null;

		commandDescriptionTextBox.setText("");
		commandUnitTextBox.setText("None");
		commandFormatTextBox.setText("None");

		System.out.println("ClientWindow: onClearButtonClick: cleared selections from screen.");

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

		try {
			for (int i = 0; i < commandsArrayDictionary.size(); i++) {
				JSONObject json = (JSONObject) commandsArrayDictionary.get(i);
				TypeCommand typeCommand = new TypeCommand(json);
				types.put(typeCommand.getName(), typeCommand);
				comboType.getItems().add((String) json.get("type"));
			}
			System.out.println("ClientWindow: API Commands loaded succesfully.");

		} catch (Exception e) {
			System.out.println("ClientWindow: (Exception Error) API Commands not loaded.");
			e.printStackTrace();
		}

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
			comboGroup.getItems().clear();
			comboName.getItems().clear();
			selectedGroup = null;
			selectedCommandName = null;
			System.out.println("ClientWindow: handleComboBoxSelection: Selected command type: " + selectedItem);

			TypeCommand typeCommand = types.get(selectedItem);
			selectedType = selectedItem;

			// Building group type list
			typeCommand.getGroups().forEach((groupName, commandList) -> {
				comboGroup.getItems().add(groupName);

//				commandList.getCommandNames().forEach((commandName, v) -> {
//					comboName.getItems().add(commandName);
//				});

			});

			comboGroup.setDisable(false);

			break;
		case "comboGroup":

			comboName.getItems().clear();
			selectedCommandName = null;

			selectedGroup = selectedItem;
			System.out.println("ClientWindow: handleComboBoxSelection: Selected command group: " + selectedItem);

			TypeCommand typeCommand1 = types.get(selectedType);
			GroupCommand groupCommand = typeCommand1.getGroups().get(selectedGroup);

			// Building group type list
			groupCommand.getCommandNames().forEach((groupName, commandList) -> {
				comboName.getItems().add(groupName);
			});

			comboName.setDisable(false);

			break;
		case "comboName":

			commandDescriptionTextBox.setText("");
			commandUnitTextBox.setText("None");
			commandFormatTextBox.setText("None");

			System.out.println("ClientWindow: handleComboBoxSelection: Selected command name: " + selectedItem);
			selectedCommandName = selectedItem;

			TypeCommand typeCommand2 = types.get(selectedType);
			GroupCommand groupCommand1 = typeCommand2.getGroups().get(selectedGroup);
			NameCommand nameCommand = groupCommand1.getCommandNames().get(selectedCommandName);

			commandDescriptionTextBox.setText(nameCommand.getDescription());

			if (nameCommand.getDataSendUnit().equals("None"))
				commandUnitTextBox.setText(nameCommand.getDataReturnUnit() + " (Returning data)");
			else if (nameCommand.getDataReturnUnit().equals("None"))
				commandUnitTextBox.setText(nameCommand.getDataSendUnit() + " (Sending data)");
			
			if (nameCommand.getDataSendFormat().equals("None"))
				commandFormatTextBox.setText(nameCommand.getReturnFormat() + " (Returning data)");
			else if (nameCommand.getReturnFormat().equals("None"))
				commandFormatTextBox.setText(nameCommand.getDataSendFormat() + " (Sending data)");
			
			break;

		case "comboAxis":
			System.out.println("ClientWindow: handleComboBoxSelection: Selected axis: " + selectedItem);
			selectedAxis = selectedItem;
			break;

		default:
			break;
		}

	}

}
