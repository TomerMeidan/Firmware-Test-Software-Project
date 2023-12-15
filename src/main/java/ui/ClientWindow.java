package ui;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import controllers.ClientPortalView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import model.CapturePacket;
import model.GroupCommand;
import model.NameCommand;
import model.TypeCommand;
import util.JsonFileReader;
import util.Parser;

@SuppressWarnings({ "restriction", "unchecked" })
public class ClientWindow {

	private Stage primaryStage;
	private VBox homePageVBox;
	private Scene scene;
	private ClientPortalView view;
	private JSONArray commandsArrayDictionary;
	private HashMap<String, TypeCommand> commandTypes;
	private HashMap<String, Byte> axisList;
	private ArrayList<CapturePacket> packetArray = null;

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
	void onAddButtonClick(ActionEvent event) {

		// Find the relevant command based of selections
		if (selectedType == null || selectedGroup == null || selectedCommandName == null) {
			System.out.println(
					"ClientWindow: onAddButtonClick: One or more paramaters are not selected for the command to be built.");
			return;
		}
		NameCommand requestedCommandData = commandTypes.get(selectedType).getGroups().get(selectedGroup)
				.getCommandNames().get(selectedCommandName);

		// Build the packet
		CapturePacket commandPacket = buildRequestedCommandPacket(requestedCommandData);
		System.out.println("ClientWindow: onAddButtonClick: Packet is built based on user selections.");

		// Add the packet to array of packets
		packetArray.add(commandPacket);
		System.out.println("ClientWindow: onAddButtonClick: Packet has been added to packet list.");
		String packetTitle = selectedType + " " + selectedGroup + " " + selectedCommandName + " " + selectedAxis;
		commandsListView.getItems().add(packetTitle);

	}

	private CapturePacket buildRequestedCommandPacket(NameCommand requestedCommand) {
		CapturePacket commandPacket = new CapturePacket();

		// Group ID Byte
		commandPacket.setGroupId((byte) 0);
		// Axis ID Byte
		byte axisID = 0;
		if (selectedAxis != null)
			axisID = axisList.get(selectedAxis);
		commandPacket.setAxisId(axisID);

		// Opcode High and Low
		String[] str = Parser.splitHex(requestedCommand.getOpCode());
		String opCodeHigh = str[0];
		String opCodeLow = str[1];

		byte highOpByte = Parser.hexStringToByte(opCodeHigh);
		byte lowOpByte = Parser.hexStringToByte(opCodeLow);

		commandPacket.setOpcodeHigh(highOpByte);
		commandPacket.setOpcodeLow(lowOpByte);

		// Data
		// TODO What to do about send or receive data format (SET/GET)
		buildPacketData(requestedCommand, commandPacket);

		// Checksum
		commandPacket.setChecksum(commandPacket.calculateChecksum());

		// Length byte
		commandPacket.setLength(commandPacket.calculatePacketByteLength());
		return commandPacket;
	}

	private void buildPacketData(NameCommand requestedCommand, CapturePacket commandPacket) {
		if (!dataInputTextBox.getText().isEmpty()) {
			String dataString = dataInputTextBox.getText();
			try {
				byte[] data = Parser.getByteArrayByFormatType(requestedCommand.getDataSendFormat(), dataString);
				commandPacket.setDataArray(data);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				commandPacket.setDataArray(null);
			}
		} else
			commandPacket.setDataArray(null);
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
		packetArray = new ArrayList<>();

		commandDescriptionTextBox.setText("");
		commandUnitTextBox.setText("None");
		commandFormatTextBox.setText("None");
		commandsListView.getItems().clear();
		
		System.out.println("ClientWindow: onClearButtonClick: cleared selections from screen.");

	}

	@FXML
	void onSaveToFileButtonPress(ActionEvent event) {
		// TODO On save file button click
	}

	@FXML
	void onSendCommandButtonPress(ActionEvent event) {
		// TODO On send command button click

		packetArray = new ArrayList<>();
	}

	@FXML
	void onComboTypeClick(ActionEvent event) {
		handleComboBoxSelection(event);

	}

	public void init(VBox homePageVBox, Stage primaryStage, ClientPortalView clientPortalView) {
		this.homePageVBox = homePageVBox;
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Micro Controller API Tester");
		this.view = clientPortalView;
		scene = new Scene(homePageVBox);
		packetArray = new ArrayList<>();
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
			commandSendButton.setDisable(false);
			addCommandButton.setDisable(false);
			clearCommandButton.setDisable(false);

		});
	}

	public void onStatusDisconnected() {
		Platform.runLater(() -> {
			// log
			System.out.println("ClientWindow: updating status to: disconnected");

			controllerConnectionStatus.setText("OFFLINE");
			controllerConnectionStatus.setTextFill(Paint.valueOf("RED"));
			commandSaveButton.setDisable(true);
			commandSendButton.setDisable(true);
			addCommandButton.setDisable(true);
			clearCommandButton.setDisable(true);
		});
	}

	public void initDataTypes() {
		String filePath = "src/main/java/util/commandsDictionary.json";
		commandsArrayDictionary = JsonFileReader.readJsonFile(filePath);

		commandTypes = new HashMap<>();

		try {
			for (int i = 0; i < commandsArrayDictionary.size(); i++) {
				JSONObject json = (JSONObject) commandsArrayDictionary.get(i);
				TypeCommand typeCommand = new TypeCommand(json);
				commandTypes.put(typeCommand.getName(), typeCommand);
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

		axisList = new HashMap<>();
		axisList.put("Yaw", (byte) 0x01);
		axisList.put("Pitch", (byte) 0x02);
		axisList.put("Roll", (byte) 0x03);
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

			TypeCommand typeCommand = commandTypes.get(selectedItem);
			selectedType = selectedItem;

			// Building group type list
			typeCommand.getGroups().forEach((groupName, commandList) -> {
				comboGroup.getItems().add(groupName);
			});

			comboGroup.setDisable(false);

			break;
		case "comboGroup":

			comboName.getItems().clear();
			selectedCommandName = null;

			selectedGroup = selectedItem;
			System.out.println("ClientWindow: handleComboBoxSelection: Selected command group: " + selectedItem);

			TypeCommand typeCommand1 = commandTypes.get(selectedType);
			GroupCommand groupCommand = typeCommand1.getGroups().get(selectedGroup);

			// Building group type list
			groupCommand.getCommandNames().forEach((commandName, commandList) -> {
				comboName.getItems().add(commandName);
			});

			comboName.setDisable(false);

			break;
		case "comboName":

			commandDescriptionTextBox.setText("");
			commandUnitTextBox.setText("None");
			commandFormatTextBox.setText("None");

			System.out.println("ClientWindow: handleComboBoxSelection: Selected command name: " + selectedItem);
			selectedCommandName = selectedItem;

			TypeCommand typeCommand2 = commandTypes.get(selectedType);
			GroupCommand groupCommand1 = typeCommand2.getGroups().get(selectedGroup);
			NameCommand nameCommand = groupCommand1.getCommandNames().get(selectedCommandName);

			setCommandInformation(nameCommand);

			break;

		case "comboAxis":
			System.out.println("ClientWindow: handleComboBoxSelection: Selected axis: " + selectedItem);
			selectedAxis = selectedItem;
			break;

		default:
			break;
		}

	}

	private void setCommandInformation(NameCommand nameCommand) {
		commandDescriptionTextBox.setText(nameCommand.getDescription());

		if (nameCommand.getDataSendUnit().equals("None"))
			commandUnitTextBox.setText(nameCommand.getDataReturnUnit() + " (Returning data)");
		else if (nameCommand.getDataReturnUnit().equals("None"))
			commandUnitTextBox.setText(nameCommand.getDataSendUnit() + " (Sending data)");

		if (nameCommand.getDataSendFormat().equals("None"))
			commandFormatTextBox.setText(nameCommand.getReturnFormat() + " (Returning data)");
		else if (nameCommand.getReturnFormat().equals("None"))
			commandFormatTextBox.setText(nameCommand.getDataSendFormat() + " (Sending data)");
	}

}
