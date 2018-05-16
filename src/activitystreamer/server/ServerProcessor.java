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
            return ServerProcessor_ServerConnection.ProcessNetworkMessage(argConnection, argJsonObject);
         case "REGISTER":
         case "LOGIN":
            return ServerProcessor_ClientRegister.ProcessNetworkMessage(argConnection, argJsonObject);
      }
      return false;
   }

}