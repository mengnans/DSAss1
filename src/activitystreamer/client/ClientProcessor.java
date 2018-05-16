package activitystreamer.client;

import activitystreamer.util.JsonHelper;
import activitystreamer.util.Settings;
import com.google.gson.JsonObject;

public class ClientProcessor {

   /**
    * This function will be called when the client wants to connect a server
    */
   public static void ProcessConnection() {
      String serverAddress = Settings.getRemoteHostname();
      int serverPort = Settings.getRemotePort();

      boolean isConnection = ClientAPIHelper.SetConnection(serverAddress, serverPort);

      if (isConnection) {
         ClientAPIHelper.SetDisplayMessage("The client successfully connects to the server");
         if (Settings.getUsername() != null) {
            JsonObject _message = ClientCommandData.REGISTER();
            ClientAPIHelper.SendMessage(_message);
         }
      } else {
         ClientAPIHelper.SetDisplayMessage("The client fails to connect the server");
      }
   }

   /**
    * This function will be called when a message is received through the network
    *
    * @param argJsonObject The json object received
    */
   public static void ProcessNetworkMessage(JsonObject argJsonObject) {
      String _command = JsonHelper.GetValue(argJsonObject, "command");
      switch (_command) {
         case "INVALID_MESSAGE":
            ClientAPIHelper.SetDisplayMessage(argJsonObject.get("info").toString());
            break;
         case "AUTHENTICATION_FAIL":
            ClientAPIHelper.CloseConnection();
            break;
         case "LOGIN_SUCCESS":
            ClientAPIHelper.SetDisplayMessage(argJsonObject.get("info").toString());
            break;
         case "LOGIN_FAILED":
            ClientAPIHelper.SetDisplayMessage("Failed to log in: " + argJsonObject.get("info"));
            ClientAPIHelper.CloseConnection();
            break;
         case "ACTIVITY_BROADCAST":
            ClientAPIHelper.SetDisplayMessage(argJsonObject);
            break;
         case "REDIRECT":
            ClientAPIHelper.SetDisplayMessage("Redirected to host:" + argJsonObject.get("hostname") + " prot:" + argJsonObject.get("port"));
            ClientAPIHelper.CloseConnection();
            String hostName = argJsonObject.get("hostname").toString();
            int port = Integer.parseInt(argJsonObject.get("port").toString());

            JsonObject ReDirectJsonObject = new JsonObject();
            ReDirectJsonObject.addProperty("command", "REDIRECT");
            ReDirectJsonObject.addProperty("username", Settings.getUsername());
            ReDirectJsonObject.addProperty("secret", Settings.getSecret());
            ClientAPIHelper.SendMessage(ReDirectJsonObject);

            boolean isConnection = ClientAPIHelper.SetConnection(hostName, port);
            if (isConnection) {
               ClientAPIHelper.SetDisplayMessage("The client successfully connects to the server: " + hostName);
            } else {
               ClientAPIHelper.SetDisplayMessage("The client fails to connect the server");
            }
            break;
         case "REGISTER_FAILED":
            ClientAPIHelper.SetDisplayMessage("Failed to register because " + argJsonObject.get("info"));
            break;
         case "REGISTER_SUCCESS":
            ClientAPIHelper.SetDisplayMessage(argJsonObject.get("info").toString());
            JsonObject logInJsonObject = new JsonObject();
            logInJsonObject.addProperty("command", "LOGIN");
            logInJsonObject.addProperty("username", Settings.getUsername());
            logInJsonObject.addProperty("secret", Settings.getSecret());
            ClientAPIHelper.SendMessage(logInJsonObject);
            break;
         default:
      }
   }

   /**
    * This function will be called when a message is received through the GUI
    *
    * @param argJsonObject The json object received
    */
   public static void ProcessUserMessage(JsonObject argJsonObject) {
      String _command = JsonHelper.GetValue(argJsonObject, "command");
      switch (_command) {
         case "REGISTER":
            ClientAPIHelper.SendMessage(argJsonObject);
            break;
         case "LOGIN":
            ClientAPIHelper.SendMessage(argJsonObject);
            break;
         case "LOGOUT":
            ClientAPIHelper.SendMessage(argJsonObject);
            ClientAPIHelper.CloseConnection();
            break;
         case "ACTIVITY_MESSAGE":
            ClientAPIHelper.SendMessage(argJsonObject);
            break;
         default:
            ClientAPIHelper.SetDisplayMessage("Illegal message");
            break;
      }
   }
}