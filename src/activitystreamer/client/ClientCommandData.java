package activitystreamer.client;

import activitystreamer.util.Settings;
import com.google.gson.JsonObject;

public class ClientCommandData {

   public static JsonObject REGISTER() {
      JsonObject _message = new JsonObject();
      _message.addProperty("command", "REGISTER");
      _message.addProperty("username", Settings.getUsername());
      _message.addProperty("secret", Settings.getSecret());
      return _message;
   }

   public static JsonObject LOGIN() {
      JsonObject _message = new JsonObject();
      _message.addProperty("command", "LOGIN");
      _message.addProperty("username", Settings.getUsername());
      _message.addProperty("secret", Settings.getSecret());
      return _message;
   }

}