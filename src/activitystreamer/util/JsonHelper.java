package activitystreamer.util;

import com.google.gson.JsonObject;

public class JsonHelper {
   public static String ToJson(JsonObject argObject) {
      return argObject.toString();
   }

   public class TestObject {
      public String name;
      public int age;

   }
}


