package util;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Parser {
	public static String encode(JSONObject json) {
		String ret = JSONValue.toJSONString(json);
		ret += '\n';
		return ret;
	}

	/*
	 * decode function converts String to JSONObject. assuming str is the message
	 * received from server and was built with Parser.code()
	 */
	public static JSONObject decode(Object str) {
		if (!(str instanceof String)) {
			// TODO maybe throw exception instead of returning null
			return null;
		}

		String msg = (String) str;

		StringBuilder string = new StringBuilder(msg);
		string.deleteCharAt(msg.length() - 1);
		JSONObject jsonObj = (JSONObject) JSONValue.parse(string.toString());
		return jsonObj;
	}

	public static Object getFormatType(String formatName) {
		switch (formatName) {
		case "Double precision 64-bit":
			return 0.0; // Example value for double
		case "Floating point 32-bit":
			return 0.0f; // Example value for float
		case "Unsigned Int 32-bit":
			return 0L; // Example value for long
		case "Unsigned Int 16-bit":
			return (short) 0; // Example value for short
		case "Unsigned Int 8-bit":
			return (byte) 0; // Example value for byte
		case "Signed Int 8-bit":
			return (byte) 0; // Example value for byte
		case "ASCII string":
			return ""; // Example value for String
		default:
			return null; // or throw an exception for an unknown format
		}
	}
}
