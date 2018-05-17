package activitystreamer.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Utility class used to for JSON convert
 */
public class JsonHelper {

   private static JsonParser parser = new JsonParser();

   public static String ObjectToString(JsonObject argObject) {
      String _content = argObject.toString();
      return _content;
   }

   public static JsonObject StringToObject(String argContent) {
      JsonObject _obj = (JsonObject) parser.parse(argContent);
      return _obj;
   }

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

   public static int GetValueAsInt(JsonObject argJsonObject, String argName) {
      String _value = JsonHelper.GetValue(argJsonObject, argName);
      if (_value == null) return 0;
      try {
         return Integer.parseInt(_value);
      } catch (Exception e) {
         return 0;
      }
   }

}