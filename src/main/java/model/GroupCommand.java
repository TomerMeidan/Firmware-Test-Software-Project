package model;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GroupCommand extends Command {

	HashMap<String, NameCommand> commandNames;

	public GroupCommand(JSONObject groupCommand) {
		setName((String) groupCommand.get("group"));
		commandNames = new HashMap<>();
		
		commandNames = new HashMap<>();
		JSONArray commandsArrayDictionary = (JSONArray) groupCommand.get("payload");

		for (int i = 0; i < commandsArrayDictionary.size(); i++) {
		    JSONObject nameJsonObject = (JSONObject) commandsArrayDictionary.get(i);

		    String name = (String) nameJsonObject.get("name");
		    String opCode = (String) nameJsonObject.get("opCode");
		    String dataSendFormat = (String) nameJsonObject.get("dataSendFormat");
		    String dataSendUnit = (String) nameJsonObject.get("dataSendUnit");
		    String returnFormat = (String) nameJsonObject.get("returnFormat");
		    String dataReturnUnit = (String) nameJsonObject.get("dataReturnUnit");
		    String description = (String) nameJsonObject.get("description");
		    String remark = (String) nameJsonObject.get("remark");

		    NameCommand nameCommand = new NameCommand(name, opCode, dataSendFormat, dataSendUnit,
		            returnFormat, dataReturnUnit, description, remark);

		    commandNames.put(nameCommand.getName(), nameCommand);
		}
	}
	
	public HashMap<String, NameCommand> getCommandNames() {
		return commandNames;
	}

}
