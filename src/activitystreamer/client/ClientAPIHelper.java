package activitystreamer.client;

import com.google.gson.JsonObject;

public class ClientAPIHelper {

   /**
    * Make the client connect to a given server.
    *
    * @param argHost The data of the host of the server
    * @param argPort The data of the port of the server
    * @return True if succeed; False if failed
    */
   public static boolean SetConnection(String argHost, int argPort) {
      return ClientItem.clientItem.SetConnect(argHost, argPort);
   }

   /**
    * This function will be called when a client wants
    * to close connection with a server.
    */
   public static void CloseConnection() {
      ClientItem.clientItem.disconnect();
   }

   /**
    * Show given json object on screen
    *
    * @param argMessageObject The json message object
    */
   public static void SetDisplayMessage(JsonObject argMessageObject) {
      ClientItem.clientItem.SetDisplay(argMessageObject);
   }

   /**
    * Show given json object on screen
    *
    * @param argMessage The String message object
    */
   public static void SetDisplayMessage(String argMessage) {

      ClientItem.clientItem.SetDisplay(argMessage);
   }

   /**
    * Send the json object to the server
    *
    * @param activityObject The json message object
    */
   public static void SendMessage(JsonObject activityObject) {
      ClientItem.clientItem.SendMessageToServer(activityObject);
   }

}
