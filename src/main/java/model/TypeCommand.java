package model;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TypeCommand extends Command {

	HashMap<String, GroupCommand> groups;

	public TypeCommand(String name) {
		setName(name);
	}

	public TypeCommand(JSONObject typeCommand) {
		setName((String) typeCommand.get("type"));
		groups = new HashMap<>();
				JSONArray commandsArrayDictionary = (JSONArray) typeCommand.get("payload");
		
		for (int i = 0; i < commandsArrayDictionary.size(); i++) {
			JSONObject groupJsonObject = (JSONObject) commandsArrayDictionary.get(i);
			String groupName = (String) groupJsonObject.get("group");
			GroupCommand groupCommand = new GroupCommand(groupJsonObject);
			groups.put(groupName, groupCommand);
		}
	}
	
	public HashMap<String, GroupCommand> getGroups() {
		return groups;
	}

}
