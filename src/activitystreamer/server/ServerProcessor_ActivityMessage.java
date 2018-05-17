package activitystreamer.server;

import activitystreamer.util.JsonHelper;
import com.google.gson.JsonObject;

public class ServerProcessor_ActivityMessage {

   /**
    * This function will be called when a message is received through the network
    *
    * @param argConnection The ServerConnection object of this message
    * @param argJsonObject The json object containing the message data
    */
   public static boolean ProcessNetworkMessage(ServerConnection argConnection, JsonObject argJsonObject) {
      String _command = JsonHelper.GetValue(argJsonObject, "command");
      switch (_command) {
         case "ACTIVITY_MESSAGE": {
            // Deal with the verify logic
            boolean _userCorrect = argConnection.clientInfo[1].equals(JsonHelper.GetValue(argJsonObject, "username"));
            boolean _secretCorrect = argConnection.clientInfo[0].equals(JsonHelper.GetValue(argJsonObject, "username"));
            if (_userCorrect == false || _secretCorrect == false) {
               JsonObject _message = ServerCommandData_Server.AUTHENTICATION_FAIL("Not logged in yet");
               ServerAPIHelper.SendMessage(argConnection, _message);
               return true;
            }
            JsonObject _message = ServerCommandData_ActivityMessage.ACTIVITY_BROADCAST(JsonHelper.GetValue(argJsonObject, "activity"));
            ServerAPIHelper.BroadcastToClient(_message);
            ServerAPIHelper.BroadcastToServer(_message);
            return false;
         }
         case "ACTIVITY_BROADCAST": {
            JsonObject _message = ServerCommandData_ActivityMessage.ACTIVITY_BROADCAST(JsonHelper.GetValue(argJsonObject, "activity"));
            ServerAPIHelper.BroadcastToClient(_message);
            ServerAPIHelper.BroadcastToServer(_message, argConnection);
            return false;
         }
      }
      return false;
   }
}