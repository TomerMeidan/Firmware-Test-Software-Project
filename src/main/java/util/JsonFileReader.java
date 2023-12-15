package util;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class JsonFileReader {
    public static JSONArray readJsonFile(String filePath) {
        // Create a JSON parser
        JSONParser parser = new JSONParser();

        try {
            // Parse the JSON file
            Object obj = parser.parse(new FileReader(filePath));

            // Cast the parsed object to a JSONArray
            return (JSONArray) obj;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            // Handle exceptions appropriately, you might want to log or rethrow
        }

        // Return an empty array if an error occurs
        return new JSONArray();
    }
}
