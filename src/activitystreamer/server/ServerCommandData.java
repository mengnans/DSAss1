package activitystreamer.server;

import activitystreamer.util.Settings;
import com.google.gson.JsonObject;

public class ServerCommandData {

   public static JsonObject ServerAnnounce() {
      JsonObject _message = new JsonObject();
      _message.addProperty("command", "SERVER_ANNOUNCE");
      _message.addProperty("id", Settings.getServerID());
      _message.addProperty("load", ServerAPIHelper.GetConnectedClientAmount());
      _message.addProperty("hostname", Settings.getLocalHostname());
      _message.addProperty("port", Settings.getLocalPort());
      return _message;
   }

   public static JsonObject InvalidMessage(String argInfo) {
      JsonObject _message = new JsonObject();
      _message.addProperty("command", "INVALID_MESSAGE");
      _message.addProperty("secret", argInfo);
      return _message;
   }

}