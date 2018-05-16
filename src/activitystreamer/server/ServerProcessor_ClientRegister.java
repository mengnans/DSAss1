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
         case "REGISTER":

            break;
      }
      return false;
   }

}