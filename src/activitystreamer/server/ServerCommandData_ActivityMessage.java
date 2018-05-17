package activitystreamer.server;

import com.google.gson.JsonObject;

public class ServerCommandData_ActivityMessage {

   public static JsonObject ACTIVITY_BROADCAST(String argActivity) {
      JsonObject _message = new JsonObject();
      _message.addProperty("command", "ACTIVITY_BROADCAST");
      _message.addProperty("activity", argActivity);
      return _message;
   }

}