package activitystreamer.server;

import com.google.gson.JsonObject;

public class ServerCommandData_ClientRegister {

   public static JsonObject REGISTER_FAILED(String argUserName) {
      JsonObject _message = new JsonObject();
      _message.addProperty("command", "REGISTER_FAILED");
      _message.addProperty("info", argUserName + " is already registered with the system");
      return _message;
   }

   public static JsonObject REGISTER_SUCCESS(String argUserName) {
      JsonObject _message = new JsonObject();
      _message.addProperty("command", "REGISTER_SUCCESS");
      _message.addProperty("info", "register success for " + argUserName);
      return _message;
   }

   public static JsonObject LOGIN_SUCCESS(String argUserName) {
      JsonObject _message = new JsonObject();
      _message.addProperty("command", "LOGIN_SUCCESS");
      _message.addProperty("info", "logged in as user " + argUserName);
      return _message;
   }

   public static JsonObject LOGIN_FAILED(String argUserName) {
      JsonObject _message = new JsonObject();
      _message.addProperty("command", "LOGIN_FAILED");
      _message.addProperty("info", argUserName + " attempts to login with wrong secret");
      return _message;
   }

   public static JsonObject REDIRECT(ServerConnection argConnection) {
      String _hostName = argConnection.getSocket().getInetAddress().toString();
      if (_hostName.startsWith("/")) _hostName = _hostName.substring(1);
      JsonObject _message = new JsonObject();
      _message.addProperty("command", "REDIRECT");
      _message.addProperty("hostname", _hostName);
      _message.addProperty("port", argConnection.portForClient);
      return _message;
   }

}