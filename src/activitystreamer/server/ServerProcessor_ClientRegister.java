package activitystreamer.server;

import activitystreamer.util.JsonHelper;
import com.google.gson.JsonObject;

public class ServerProcessor_ClientRegister {

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
            if (ServerAPIHelper.TestIsUserNameExisted(_userName)) {
               JsonObject _message = ServerCommandData_ClientRegister.REGISTER_FAILED(_userName);
               ServerAPIHelper.SendMessage(argConnection, _message);
               return true;
            }
            // Send info to the client
            JsonObject _message = ServerCommandData_ClientRegister.REGISTER_SUCCESS(_userName);
            ServerAPIHelper.SendMessage(argConnection, _message);
            // Send info to all other server to update the userList
            ServerAPIHelper.UpdateUserInfoList(new String[]{_userName}, new String[]{_userSecret});
            _message = ServerCommandData_ServerConnection.USER_LIST_UPDATE();
            ServerAPIHelper.BroadcastToServer(_message, argConnection);
            return false;
         }
         case "LOGIN": {
            String _userName = JsonHelper.GetValue(argJsonObject, "username");
            String _userSecret = JsonHelper.GetValue(argJsonObject, "secret");
            if (argConnection.isRegistered || argConnection.connectionType != ServerConnection.ConnectionType.Undefined || _userName == null || _userSecret == null) {
               JsonObject _message = ServerCommandData.InvalidMessage("Should contain userName and userSecret");
               ServerAPIHelper.SendMessage(argConnection, _message);
               return true;
            }
            boolean _isExisted = ServerAPIHelper.TestIsUserInfoExisted(_userName, _userSecret);
            if (_isExisted == true) {
               JsonObject _message = ServerCommandData_ClientRegister.LOGIN_SUCCESS(_userName);
               ServerAPIHelper.SendMessage(argConnection, _message);
               return false;
            } else {
               JsonObject _message = ServerCommandData_ClientRegister.LOGIN_FAILED(_userName);
               ServerAPIHelper.SendMessage(argConnection, _message);
               return true;
            }
         }
      }
      return false;
   }

}