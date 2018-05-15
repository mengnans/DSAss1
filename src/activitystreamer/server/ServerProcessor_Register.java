package activitystreamer.server;

import activitystreamer.util.JsonHelper;
import activitystreamer.util.Settings;
import com.google.gson.JsonObject;

public class ServerProcessor_Register {

    /**
     * This function will be called when the server succeed in connecting to another server
     *
     * @param argConnection The ServerConnection object of this new connection
     */
    public static void DoAuthenticate(ServerConnection argConnection) {
        JsonObject _message = ServerCommandData.Authenticate();
        ServerAPIHelper.SendMessage(argConnection, _message);
    }

    public static void SendRegistedUserList(ServerConnection argConnection) {

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
                    JsonObject _message = ServerCommandData.AuthenticationFail("Wrong secrets value");
                    ServerAPIHelper.SendMessage(argConnection, _message);
                    return true;
                }
                argConnection.isRegistered = true;
                return false;
        }
        return false;
    }

}