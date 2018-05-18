package activitystreamer.client;

import activitystreamer.util.JsonHelper;
import activitystreamer.util.Settings;
import com.google.gson.JsonObject;

public class ClientProcessor {

   /**
    * This function will be called when the client wants to connect a server
    */
   public static void ProcessConnectionAndRegister() {
      boolean _isConnected = ClientAPIHelper.SetConnection(Settings.getRemoteHostname(), Settings.getRemotePort());
      if (_isConnected) {
         ClientAPIHelper.SetDisplayMessage("The client successfully connects to the server");
         if (Settings.getUsername() != null) {
            if (Settings.getUsername().equals("anonymous")) {
               JsonObject _message = ClientCommandData.LOGIN();
               ClientAPIHelper.SendMessage(_message);
            } else {
               JsonObject _message = ClientCommandData.REGISTER();
               ClientAPIHelper.SendMessage(_message);
            }
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
            ClientAPIHelper.SetDisplayMessage(argJsonObject.get("info").toString());
            break;
         case "LOGIN_SUCCESS":
            ClientAPIHelper.SetDisplayMessage(argJsonObject.get("info").toString());
            break;
         case "LOGIN_FAILED":
            ClientAPIHelper.SetDisplayMessage("Failed to log in: " + argJsonObject.get("info"));
            break;
         case "ACTIVITY_BROADCAST":
            ClientAPIHelper.SetDisplayMessage("Received a message: " + JsonHelper.GetValue(argJsonObject, "activity"));
            break;
         case "REDIRECT": {
            ClientAPIHelper.SetDisplayMessage("Redirected to host:" + argJsonObject.get("hostname") + " port:" + argJsonObject.get("port"));
            Settings.setRemoteHostname(JsonHelper.GetValue(argJsonObject, "hostname"));
            Settings.setRemotePort(Integer.parseInt(argJsonObject.get("port").toString()));

            ClientAPIHelper.SetConnection(Settings.getRemoteHostname(), Settings.getRemotePort());
            JsonObject _message = ClientCommandData.LOGIN();
            ClientAPIHelper.SendMessage(_message);
            break;
         }
         case "REGISTER_FAILED":
            ClientAPIHelper.SetDisplayMessage("Failed to register because " + argJsonObject.get("info"));
            break;
         case "REGISTER_SUCCESS": {
            ClientAPIHelper.SetDisplayMessage(argJsonObject.get("info").toString());
            JsonObject _message = ClientCommandData.LOGIN();
            ClientAPIHelper.SendMessage(_message);
            break;
         }
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
            Settings.setUsername(JsonHelper.GetValue(argJsonObject, "username"));
            Settings.setSecret(JsonHelper.GetValue(argJsonObject, "secret"));
            ClientAPIHelper.SendMessage(argJsonObject);
            break;
         case "LOGIN":
            Settings.setUsername(JsonHelper.GetValue(argJsonObject, "username"));
            Settings.setSecret(JsonHelper.GetValue(argJsonObject, "secret"));
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