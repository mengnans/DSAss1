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
}
