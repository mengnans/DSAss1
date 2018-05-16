package activitystreamer.server;

import activitystreamer.util.JsonHelper;
import activitystreamer.util.Settings;
import com.google.gson.JsonObject;

public class ServerProcessor_ServerConnection {

   /**
    * This function will be called when the server succeed in connecting to another server
    *
    * @param argConnection The ServerConnection object of this new connection
    */
   public static void DoAuthenticate(ServerConnection argConnection) {
      JsonObject _message = ServerCommandData_Register.AUTHENTICATE();
      ServerAPIHelper.SendMessage(argConnection, _message);
   }

   public static void SendRegisteredUserList(ServerConnection argConnection) {
      JsonObject _message = ServerCommandData_Register.LOCK_SERVER_JOIN();
      ServerAPIHelper.SendMessage(argConnection, _message);
   }

   /**
    * This function will be called when a message is received through the network
    *
    * @param argConnection The ServerConnection object of this message
    * @param argJsonObject The json object containing the message data
    */
   public static boolean ProcessNetworkMessage(ServerConnection argConnection, JsonObject argJsonObject) {
      String _command = JsonHelper.GetValue(argJsonObject, "command");
      switch (_command) {
         case "AUTHENTICATE":
            if (argConnection.isRegistered) {
               JsonObject _message = ServerCommandData.InvalidMessage("Already Registered");
               ServerAPIHelper.SendMessage(argConnection, _message);
               return false;
            }
            if (Settings.getSecret().equals(JsonHelper.GetValue(argJsonObject, "secret")) == false) {
               JsonObject _message = ServerCommandData_Register.AUTHENTICATION_FAIL("Wrong secrets value");
               ServerAPIHelper.SendMessage(argConnection, _message);
               return true;
            }
            argConnection.isRegistered = true;
            return false;
         case "LOCK_SERVER_JOIN": {
            boolean _isChanged = false;
            String[] _userName = JsonHelper.GetValue(argJsonObject, "connectedClientUserName").split("\r");
            String[] _userSecret = JsonHelper.GetValue(argJsonObject, "connectedClientSecret").split("\r");
            if (_userName.length != _userSecret.length) {
               JsonObject _message = ServerCommandData_Register.AUTHENTICATION_FAIL("The length of user name and user secret is not the same");
               ServerAPIHelper.SendMessage(argConnection, _message);
               return false;
            }
            for (int _newUserIndex = 0; _newUserIndex < _userName.length; _newUserIndex++) {
               if (ServerAPIHelper.TestIsUserNameExisted(_userName[_newUserIndex])) {
                  ServerItem.lstUserInfo.add(new String[]{_userName[_newUserIndex], _userSecret[_newUserIndex]});
                  _isChanged = true;
               }
            }
            if (_isChanged) {
               JsonObject _message = ServerCommandData_Register.USER_LIST_UPDATE();
               ServerAPIHelper.BroadcastToServer(_message);
            } else {
               JsonObject _message = ServerCommandData_Register.USER_LIST_UPDATE();
               ServerAPIHelper.SendMessage(argConnection, _message);
            }
            return false;
         }
         case "USER_LIST_UPDATE": {
            boolean _isChanged = false;
            String[] _userName = JsonHelper.GetValue(argJsonObject, "connectedClientUserName").split("\r");
            String[] _userSecret = JsonHelper.GetValue(argJsonObject, "connectedClientSecret").split("\r");
            if (_userName.length != _userSecret.length) {
               JsonObject _message = ServerCommandData_Register.AUTHENTICATION_FAIL("The length of user name and user secret is not the same");
               ServerAPIHelper.SendMessage(argConnection, _message);
               return false;
            }
            for (int _newUserIndex = 0; _newUserIndex < _userName.length; _newUserIndex++) {
               if (ServerAPIHelper.TestIsUserNameExisted(_userName[_newUserIndex])) {
                  ServerItem.lstUserInfo.add(new String[]{_userName[_newUserIndex], _userSecret[_newUserIndex]});
                  _isChanged = true;
               }
            }
            if (_isChanged) {
               JsonObject _message = ServerCommandData_Register.USER_LIST_UPDATE();
               ServerAPIHelper.BroadcastToServer(_message, argConnection);
            }
            return false;
         }
      }
      return false;
   }

}