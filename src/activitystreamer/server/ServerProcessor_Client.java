package activitystreamer.server;

import activitystreamer.util.JsonHelper;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class ServerProcessor_Client {

   /**
    * This function will be called when a message is received through the network
    *
    * @param argConnection The ServerConnection object of this message
    * @param argJsonObject The json object containing the message data
    */
   public static boolean ProcessNetworkMessage(ServerConnection argConnection, JsonObject argJsonObject) {
      String _command = JsonHelper.GetValue(argJsonObject, "command");
      switch (_command) {
         case "REGISTER": {
            String _userName = JsonHelper.GetValue(argJsonObject, "username");
            String _userSecret = JsonHelper.GetValue(argJsonObject, "secret");
            if (argConnection.isRegistered || argConnection.connectionType != ServerConnection.ConnectionType.Undefined || _userName == null || _userSecret == null) {
               JsonObject _message = ServerCommandData.InvalidMessage("Should contain userName and userSecret");
               ServerAPIHelper.SendMessage(argConnection, _message);
               return true;
            }
            if (_userName.contains(",") || _userSecret.contains(",")) {
               JsonObject _message = ServerCommandData.InvalidMessage("Should not contain dot in the user name or the user secret");
               ServerAPIHelper.SendMessage(argConnection, _message);
               return true;
            }
            if (ServerAPIHelper.TestIsUserNameExisted(_userName) == true && ServerAPIHelper.TestIsUserInfoExisted(_userName, _userSecret) == false) {
               JsonObject _message = ServerCommandData_Client.REGISTER_FAILED(_userName);
               ServerAPIHelper.SendMessage(argConnection, _message);
               return true;
            }
            // Send info to the client
            JsonObject _message = ServerCommandData_Client.REGISTER_SUCCESS(_userName);
            ServerAPIHelper.SendMessage(argConnection, _message);
            // Send info to all other server to update the userList
            ServerAPIHelper.UpdateUserInfoList(new String[]{_userName}, new String[]{_userSecret});
            _message = ServerCommandData_Server.USER_LIST_UPDATE();
            ServerAPIHelper.BroadcastToServer(_message);
            return false;
         }
         case "LOGIN":
            return DealWith_LOGIN(argConnection, argJsonObject);
         case "ACTIVITY_MESSAGE":
            return DealWith_ACTIVITY_MESSAGE(argConnection, argJsonObject);
      }
      return false;
   }

   private static boolean DealWith_LOGIN(ServerConnection argConnection, JsonObject argJsonObject) {
      String _userName = JsonHelper.GetValue(argJsonObject, "username");
      String _userSecret = JsonHelper.GetValue(argJsonObject, "secret");
      if (_userName == null || (_userName != "anonymous" && _userSecret == "null")) {
         JsonObject _message = ServerCommandData.InvalidMessage("Should contain userName and userSecret");
         ServerAPIHelper.SendMessage(argConnection, _message);
         return true;
      }
      if (argConnection.isRegistered || argConnection.connectionType != ServerConnection.ConnectionType.Undefined) {
         JsonObject _message = ServerCommandData.InvalidMessage("Should contain userName and userSecret");
         ServerAPIHelper.SendMessage(argConnection, _message);
         return true;
      }
      boolean _isExisted = ServerAPIHelper.TestIsUserInfoExisted(_userName, _userSecret);
      if (_isExisted == false) {
         JsonObject _message = ServerCommandData_Client.LOGIN_FAILED(_userName);
         ServerAPIHelper.SendMessage(argConnection, _message);
         return false;
      }
      boolean _isRedirected = IsRedirectNeeded(argConnection);
      if (_isRedirected == true) {
         return true;
      } else {
         JsonObject _message = ServerCommandData_Client.LOGIN_SUCCESS(_userName);
         ServerAPIHelper.SendMessage(argConnection, _message);
         argConnection.connectionType = ServerConnection.ConnectionType.ConnectedToClient;
         argConnection.clientInfo = new String[]{_userName, _userSecret};
         return false;
      }
   }

   private static boolean IsRedirectNeeded(ServerConnection argConnectionItem) {
      ServerConnection _connection = null;
      int _clientAmount = ServerAPIHelper.GetConnectedClientAmount();
      ArrayList<ServerConnection> _blkConnection = ServerAPIHelper.GetServerConnection();
      for (ServerConnection _connectionItem : _blkConnection) {
         if (_clientAmount > _connectionItem.clientAmount) {
            _clientAmount = _connectionItem.clientAmount;
            _connection = _connectionItem;
         }
      }
      if (_connection != null) {
         JsonObject _message = ServerCommandData_Client.REDIRECT(_connection);
         ServerAPIHelper.SendMessage(argConnectionItem, _message);
         return true;
      }
      return false;
   }

   private static boolean DealWith_ACTIVITY_MESSAGE(ServerConnection argConnection, JsonObject argJsonObject) {
      // Deal with the verify logic
      boolean _userCorrect = argConnection.clientInfo[1].equals(JsonHelper.GetValue(argJsonObject, "username"));
      boolean _secretCorrect = argConnection.clientInfo[0].equals(JsonHelper.GetValue(argJsonObject, "username"));
      if (_userCorrect == false || _secretCorrect == false) {
         JsonObject _message = ServerCommandData_Server.AUTHENTICATION_FAIL("Not logged in yet");
         ServerAPIHelper.SendMessage(argConnection, _message);
         return true;
      }

      return false;
   }

}