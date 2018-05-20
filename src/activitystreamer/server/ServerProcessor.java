package activitystreamer.server;

import activitystreamer.util.JsonHelper;
import com.google.gson.JsonObject;

public class ServerProcessor {

   public static void ProcessServerAnnounce() {
      JsonObject _message = ServerCommandData.ServerAnnounce();
      ServerAPIHelper.BroadcastToServer(_message);
   }

   /**
    * This function will be called when a message is received through the network
    *
    * @param argConnection The ServerConnection object of this message
    * @param argJsonObject The json object containing the message data
    */
   public static boolean ProcessNetworkMessage(ServerConnection argConnection, JsonObject argJsonObject) {
      String _command = JsonHelper.GetValue(argJsonObject, "command");
      if (_command == null) return false;
      switch (_command) {
         case "AUTHENTICATE":
         case "LOCK_SERVER_JOIN":
         case "USER_LIST_UPDATE":
         case "SERVER_ANNOUNCE":
            return ServerProcessor_Server.ProcessNetworkMessage(argConnection, argJsonObject);
         case "REGISTER":
         case "LOGIN":
         case "LOGOUT":
            return ServerProcessor_Client.ProcessNetworkMessage(argConnection, argJsonObject);
         case "ACTIVITY_MESSAGE":
         case "ACTIVITY_BROADCAST":
            return ServerProcessor_ActivityMessage.ProcessNetworkMessage(argConnection, argJsonObject);
      }
      return false;
   }

}