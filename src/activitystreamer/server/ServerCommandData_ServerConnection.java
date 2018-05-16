package activitystreamer.server;

import activitystreamer.util.Settings;
import com.google.gson.JsonObject;

public class ServerCommandData_ServerConnection {

   public static JsonObject AUTHENTICATE() {
      JsonObject _message = new JsonObject();
      _message.addProperty("command", "AUTHENTICATE");
      _message.addProperty("secret", Settings.getSecret());
      return _message;
   }

   public static JsonObject AUTHENTICATION_FAIL(String argInfo) {
      JsonObject _message = new JsonObject();
      _message.addProperty("command", "AUTHENTICATION_FAIL");
      _message.addProperty("info", argInfo);
      return _message;
   }

   public static JsonObject LOCK_SERVER_JOIN() {
      String _userName = "";
      String _userSecret = "";
      for (String[] _info : ServerItem.lstUserInfo) {
         _userName += _info[0] + "\r";
         _userSecret += _info[1] + "\r";
      }
      JsonObject _message = new JsonObject();
      _message.addProperty("command", "LOCK_SERVER_JOIN");
      _message.addProperty("connectedClientUserName", _userName);
      _message.addProperty("connectedClientSecret", _userSecret);
      return _message;
   }

   public static JsonObject USER_LIST_UPDATE() {
      String _userName = "";
      String _userSecret = "";
      for (String[] _info : ServerItem.lstUserInfo) {
         _userName += _info[0] + "\r";
         _userSecret += _info[1] + "\r";
      }
      JsonObject _message = new JsonObject();
      _message.addProperty("command", "USER_LIST_UPDATE");
      _message.addProperty("connectedClientUserName", _userName);
      _message.addProperty("connectedClientSecret", _userSecret);
      return _message;
   }

}