package activitystreamer.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Utility class used to for JSON convert
 */
public class JsonHelper {

    private static JsonParser parser = new JsonParser();

    /**
     * Convert a JsonObject object to string
     *
     * @param argObject The object to be converted
     * @return
     */
    public static String ObjectToString(JsonObject argObject) {
        String _content = argObject.toString();
        return _content;
    }

    /**
     * Convert string to a JsonObject object
     *
     * @param argContent Json data string
     * @return
     */
    public static JsonObject StringToObject(String argContent) {
        JsonObject _obj = (JsonObject) parser.parse(argContent);
        return _obj;
    }

    /**
     * Get the value of a JSON object as String
     *
     * @param argJsonObject Json data object
     * @param argName       The name of the key
     * @return
     */
    public static String GetValue(JsonObject argJsonObject, String argName) {
        if (argJsonObject.has(argName) == false) {
            return null;
        }
        String _value = argJsonObject.get(argName).toString();
        while (_value.startsWith("\"")) {
            _value = _value.substring(1, _value.length());
        }
        while (_value.endsWith("\"")) {
            _value = _value.substring(0, _value.length() - 1);
        }
        return _value;
    }

}